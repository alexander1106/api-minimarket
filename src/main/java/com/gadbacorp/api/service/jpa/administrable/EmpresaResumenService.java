package com.gadbacorp.api.service.jpa.administrable;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Clock;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gadbacorp.api.entity.administrable.EmpresaResumenDTO;
import com.gadbacorp.api.entity.administrable.EmpresaResumenDTO.EstadoSuscripcion;
import com.gadbacorp.api.entity.administrable.Empresas;
import com.gadbacorp.api.entity.administrable.Plan;
import com.gadbacorp.api.excepciones.LimitePlanExcedidoException;
import com.gadbacorp.api.repository.administrable.EmpresasRepository;
import com.gadbacorp.api.repository.administrable.SucursalesRepository;
import com.gadbacorp.api.repository.empleados.UsuarioRepository;

/** Arma el resumen comercial de cada empresa (plan, ingreso, vencimiento, usuarios, estado) y controla los cupos del plan. */
@Service
public class EmpresaResumenService {

    public static final String ACTIVO = "ACTIVO";
    public static final String PRUEBA = "PRUEBA";
    public static final String VENCIDO = "VENCIDO";
    public static final String SUSPENDIDO = "SUSPENDIDO";

    private final EmpresasRepository empresasRepository;
    private final SucursalesRepository sucursalesRepository;
    private final UsuarioRepository usuarioRepository;
    private final Clock clock;
    private final String uploadDir;

    public EmpresaResumenService(EmpresasRepository empresasRepository,
                                 SucursalesRepository sucursalesRepository,
                                 UsuarioRepository usuarioRepository,
                                 Clock clock,
                                 @Value("${upload.dir}") String uploadDir) {
        this.empresasRepository = empresasRepository;
        this.sucursalesRepository = sucursalesRepository;
        this.usuarioRepository = usuarioRepository;
        this.clock = clock;
        this.uploadDir = uploadDir;
    }

    @Transactional(readOnly = true)
    public List<EmpresaResumenDTO> listar() {
        Map<Integer, Long> sucursales = contar(sucursalesRepository.contarPorEmpresa());
        Map<Integer, Long> usuarios = contar(usuarioRepository.contarPorEmpresa());
        LocalDate hoy = LocalDate.now(clock);
        return empresasRepository.findAll().stream()
                .map(e -> resumir(e, sucursales.getOrDefault(e.getIdempresa(), 0L),
                        usuarios.getOrDefault(e.getIdempresa(), 0L), hoy, cargarLogo(e.getLogo())))
                .toList();
    }

    @Transactional(readOnly = true)
    public java.util.Optional<EmpresaResumenDTO> buscar(Integer idEmpresa) {
        return empresasRepository.findById(idEmpresa).map(e -> resumir(e,
                sucursalesRepository.countByEmpresaIdempresa(idEmpresa),
                usuarioRepository.countBySucursal_Empresa_Idempresa(idEmpresa),
                LocalDate.now(clock), cargarLogo(e.getLogo())));
    }

    /** @throws LimitePlanExcedidoException si la empresa ya tiene tantos usuarios como permite su plan */
    @Transactional(readOnly = true)
    public void verificarCupoUsuarios(Empresas empresa) {
        Plan plan = empresa.getPlan();
        if (plan == null || plan.getMaxUsuarios() == null) {
            return;
        }
        long usados = usuarioRepository.countBySucursal_Empresa_Idempresa(empresa.getIdempresa());
        if (usados >= plan.getMaxUsuarios()) {
            throw new LimitePlanExcedidoException("Se alcanzo el limite de usuarios del plan " + plan.getNombre()
                    + " (" + usados + "/" + plan.getMaxUsuarios() + "). Cambia de plan para agregar mas.");
        }
    }

    /** @throws LimitePlanExcedidoException si la empresa ya tiene tantas sucursales como permite su plan */
    @Transactional(readOnly = true)
    public void verificarCupoSucursales(Empresas empresa) {
        Plan plan = empresa.getPlan();
        if (plan == null || plan.getMaxSucursales() == null) {
            return;
        }
        long usadas = sucursalesRepository.countByEmpresaIdempresa(empresa.getIdempresa());
        if (usadas >= plan.getMaxSucursales()) {
            throw new LimitePlanExcedidoException("Se alcanzo el limite de sucursales del plan " + plan.getNombre()
                    + " (" + usadas + "/" + plan.getMaxSucursales() + "). Cambia de plan para agregar mas.");
        }
    }

    public static EmpresaResumenDTO resumir(Empresas e, long sucursales, long usuarios, LocalDate hoy, String logo) {
        EstadoSuscripcion estado = calcularEstado(e, hoy);
        Plan plan = e.getPlan();
        String ciclo = e.getCiclo() == null ? "MENSUAL" : e.getCiclo();

        BigDecimal mrr = BigDecimal.ZERO;
        if (ACTIVO.equals(estado.codigo()) && plan != null && plan.getPrecioMensual() != null) {
            mrr = plan.getPrecioMensual();
        }

        return new EmpresaResumenDTO(
                e.getIdempresa(),
                e.getNombreComercial() != null && !e.getNombreComercial().isBlank() ? e.getNombreComercial() : e.getRazonsocial(),
                e.getRazonsocial(), e.getRuc(), e.getCiudad(), e.getDireccion(), e.getCorreo(),
                e.getFechaRegistro(), logo, e.getEstado(),
                sucursales,
                Boolean.TRUE.equals(e.getSunatVerificado()),
                Boolean.TRUE.equals(e.getFacturacionElectronica()),
                e.getNotas(),
                new EmpresaResumenDTO.Propietario(e.getPropietario(), e.getTelefono()),
                new EmpresaResumenDTO.Ubicacion(e.getCiudad(), e.getDistrito(), e.getDepartamento()),
                plan == null ? null : new EmpresaResumenDTO.PlanInfo(plan.getIdPlan(), plan.getNombre(), ciclo, plan.getPrecioMensual()),
                mrr,
                "PEN",
                e.getFechaVencimiento(),
                e.getFechaRegistro(),
                new EmpresaResumenDTO.UsuariosInfo(usuarios, plan == null ? null : plan.getMaxUsuarios()),
                estado);
    }

    /**
     * SUSPENDIDO si el dueno de la plataforma la suspendio; si no, PRUEBA mientras dure el periodo de prueba,
     * VENCIDO cuando paso su fecha de vencimiento y ACTIVO en cualquier otro caso (sin vencimiento = sin caducidad).
     */
    public static EstadoSuscripcion calcularEstado(Empresas e, LocalDate hoy) {
        LocalDate vence = e.getFechaVencimiento();
        Long dias = vence == null ? null : ChronoUnit.DAYS.between(hoy, vence);

        if (Boolean.TRUE.equals(e.getSuspendida())) {
            return new EstadoSuscripcion(SUSPENDIDO, "Suspendido", dias);
        }
        boolean vencida = dias != null && dias < 0;
        if (vencida) {
            return new EstadoSuscripcion(VENCIDO, "Vencido", dias);
        }
        if (Boolean.TRUE.equals(e.getEnPrueba())) {
            return new EstadoSuscripcion(PRUEBA, dias == null ? "Prueba" : "Prueba (" + dias + "d)", dias);
        }
        return new EstadoSuscripcion(ACTIVO, "Activo", dias);
    }

    private static Map<Integer, Long> contar(List<Object[]> filas) {
        Map<Integer, Long> mapa = new HashMap<>();
        for (Object[] fila : filas) {
            mapa.put(((Number) fila[0]).intValue(), ((Number) fila[1]).longValue());
        }
        return mapa;
    }

    /** El logo se guarda como nombre de archivo; el frontend lo espera como base64 (igual que antes). */
    private String cargarLogo(String nombreArchivo) {
        if (nombreArchivo == null) {
            return null;
        }
        try {
            Path imagen = Paths.get(uploadDir).resolve(nombreArchivo).toAbsolutePath().normalize();
            return Files.exists(imagen) ? Base64.getEncoder().encodeToString(Files.readAllBytes(imagen)) : null;
        } catch (IOException ex) {
            return null;
        }
    }
}
