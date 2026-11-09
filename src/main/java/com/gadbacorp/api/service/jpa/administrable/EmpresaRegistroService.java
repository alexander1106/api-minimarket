package com.gadbacorp.api.service.jpa.administrable;

import java.time.Clock;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gadbacorp.api.entity.administrable.Empresas;
import com.gadbacorp.api.entity.administrable.Plan;
import com.gadbacorp.api.entity.administrable.RegistroEmpresaRequest;
import com.gadbacorp.api.entity.administrable.Sucursales;
import com.gadbacorp.api.excepciones.LimitePlanExcedidoException;
import com.gadbacorp.api.excepciones.RecursoDuplicadoException;
import com.gadbacorp.api.excepciones.RegistroInvalidoException;
import com.gadbacorp.api.repository.administrable.EmpresasRepository;
import com.gadbacorp.api.repository.administrable.PlanRepository;
import com.gadbacorp.api.repository.administrable.SucursalesRepository;

/**
 * Alta y edicion de empresas desde la consola del Superadministrador. El alta crea la empresa y sus sucursales en
 * una sola transaccion: si algo falla (RUC repetido, cupo del plan excedido...) no queda nada a medias.
 */
@Service
public class EmpresaRegistroService {

    public static final String ESTADO_ACTIVO = "ACTIVO";
    public static final String ESTADO_PRUEBA = "PRUEBA";
    public static final String ESTADO_SUSPENDIDO = "SUSPENDIDO";

    private final EmpresasRepository empresasRepository;
    private final SucursalesRepository sucursalesRepository;
    private final PlanRepository planRepository;
    private final Clock clock;
    private final int diasPrueba;

    public EmpresaRegistroService(EmpresasRepository empresasRepository,
                                  SucursalesRepository sucursalesRepository,
                                  PlanRepository planRepository,
                                  Clock clock,
                                  @Value("${app.suscripcion.dias-prueba:14}") int diasPrueba) {
        this.empresasRepository = empresasRepository;
        this.sucursalesRepository = sucursalesRepository;
        this.planRepository = planRepository;
        this.clock = clock;
        this.diasPrueba = diasPrueba;
    }

    /**
     * @return el id de la empresa creada
     * @throws RegistroInvalidoException datos incompletos o mal formados (errores por campo)
     * @throws LimitePlanExcedidoException pidio mas sucursales de las que permite el plan
     * @throws RecursoDuplicadoException RUC ya registrado
     */
    @Transactional
    public Integer registrar(RegistroEmpresaRequest req) {
        Map<String, String> errores = validarAlta(req);
        if (!errores.isEmpty()) {
            throw new RegistroInvalidoException(errores);
        }
        String ruc = req.ruc().trim();
        if (empresasRepository.existsByRuc(ruc)) {
            throw new RecursoDuplicadoException("Ya existe una empresa registrada con el RUC " + ruc + ".");
        }

        int cantidadSucursales = req.sucursales() == null ? 1 : req.sucursales();
        Plan plan = req.idPlan() == null ? null : planRepository.findById(req.idPlan())
                .orElseThrow(() -> new RegistroInvalidoException(Map.of("idPlan", "El plan no existe.")));
        if (plan != null && plan.getMaxSucursales() != null && cantidadSucursales > plan.getMaxSucursales()) {
            throw new LimitePlanExcedidoException("El plan " + plan.getNombre() + " permite hasta "
                    + plan.getMaxSucursales() + " sucursal(es) y se pidieron " + cantidadSucursales + ".");
        }

        LocalDate hoy = LocalDate.now(clock);
        Empresas e = new Empresas();
        e.setFechaRegistro(hoy);
        e.setEstado(1);
        e.setPlan(plan);
        aplicar(e, req, true);
        aplicarEstadoInicial(e, normalizarEstado(req.estadoInicial()), req.fechaVencimiento(), hoy);
        e = empresasRepository.save(e);

        for (int i = 1; i <= cantidadSucursales; i++) {
            Sucursales s = new Sucursales();
            s.setNombreSucursal(i == 1 ? "Sede Principal" : "Sucursal " + i);
            s.setContacto(e.getTelefono());
            s.setDireccion(e.getDireccion());
            s.setEstado(1);
            s.setEmpresa(e);
            sucursalesRepository.save(s);
        }
        return e.getIdempresa();
    }

    /** Edita solo los campos que llegan; no crea sucursales ni cambia el estado de la suscripcion. */
    @Transactional
    public void actualizar(Integer id, RegistroEmpresaRequest req) {
        Empresas e = empresasRepository.findById(id)
                .orElseThrow(() -> new java.util.NoSuchElementException("Empresa no encontrada con ID: " + id));
        Map<String, String> errores = new LinkedHashMap<>();
        if (req.ruc() != null) {
            if (!req.ruc().trim().matches("\\d{11}")) {
                errores.put("ruc", "El RUC debe tener 11 dígitos.");
            } else if (!req.ruc().trim().equals(e.getRuc()) && empresasRepository.existsByRuc(req.ruc().trim())) {
                throw new RecursoDuplicadoException("Ya existe una empresa registrada con el RUC " + req.ruc().trim() + ".");
            }
        }
        if (req.correo() != null && !correoValido(req.correo())) {
            errores.put("correo", "El correo electrónico no es válido.");
        }
        if (req.telefono() != null && !telefonoValido(req.telefono())) {
            errores.put("telefono", "El teléfono debe tener al menos 9 dígitos.");
        }
        if (req.nombreComercial() != null && req.nombreComercial().isBlank()) {
            errores.put("nombreComercial", "El nombre comercial no puede estar vacío.");
        }
        if (!errores.isEmpty()) {
            throw new RegistroInvalidoException(errores);
        }
        if (req.idPlan() != null) {
            e.setPlan(planRepository.findById(req.idPlan())
                    .orElseThrow(() -> new RegistroInvalidoException(Map.of("idPlan", "El plan no existe."))));
        }
        aplicar(e, req, false);
        empresasRepository.save(e);
    }

    static Map<String, String> validarAlta(RegistroEmpresaRequest r) {
        Map<String, String> errores = new LinkedHashMap<>();
        if (vacio(r.nombreComercial())) {
            errores.put("nombreComercial", "El nombre comercial es obligatorio.");
        }
        if (vacio(r.ruc()) || !r.ruc().trim().matches("\\d{11}")) {
            errores.put("ruc", "El RUC debe tener 11 dígitos.");
        }
        if (vacio(r.propietario())) {
            errores.put("propietario", "El nombre del propietario o representante es obligatorio.");
        }
        if (vacio(r.correo()) || !correoValido(r.correo())) {
            errores.put("correo", "Ingresa un correo electrónico válido.");
        }
        if (vacio(r.telefono()) || !telefonoValido(r.telefono())) {
            errores.put("telefono", "El teléfono debe tener al menos 9 dígitos.");
        }
        if (vacio(r.direccion())) {
            errores.put("direccion", "La dirección de la sede principal es obligatoria.");
        }
        if (r.sucursales() != null && r.sucursales() < 1) {
            errores.put("sucursales", "Debe haber al menos 1 sucursal.");
        }
        if (r.ciclo() != null && !r.ciclo().isBlank() && !cicloValido(r.ciclo())) {
            errores.put("ciclo", "La frecuencia de cobro debe ser MENSUAL o ANUAL.");
        }
        if (r.estadoInicial() != null && !r.estadoInicial().isBlank() && normalizarEstado(r.estadoInicial()) == null) {
            errores.put("estadoInicial", "El estado inicial debe ser ACTIVO, PRUEBA o SUSPENDIDO.");
        }
        if (!vacio(r.fechaVencimiento())) {
            try {
                LocalDate.parse(r.fechaVencimiento());
            } catch (DateTimeParseException ex) {
                errores.put("fechaVencimiento", "La fecha debe tener el formato AAAA-MM-DD.");
            }
        }
        return errores;
    }

    /** Copia los campos presentes; en el alta, los ausentes toman su valor por defecto. */
    private static void aplicar(Empresas e, RegistroEmpresaRequest r, boolean alta) {
        if (r.nombreComercial() != null) e.setNombreComercial(r.nombreComercial().trim());
        if (r.ruc() != null) e.setRuc(r.ruc().trim());
        if (r.razonsocial() != null && !r.razonsocial().isBlank()) {
            e.setRazonsocial(r.razonsocial().trim());
        } else if (alta) {
            e.setRazonsocial(e.getNombreComercial()); // "opcional si es identica"
        }
        if (r.propietario() != null) e.setPropietario(r.propietario().trim());
        if (r.correo() != null) e.setCorreo(r.correo().trim());
        if (r.telefono() != null) e.setTelefono(r.telefono().trim());
        if (r.direccion() != null) e.setDireccion(r.direccion().trim());
        if (r.distrito() != null) e.setDistrito(r.distrito().trim());
        if (r.ciudad() != null) e.setCiudad(r.ciudad().trim());
        if (r.departamento() != null) e.setDepartamento(r.departamento().trim());
        if (r.ciclo() != null && !r.ciclo().isBlank()) {
            e.setCiclo(r.ciclo().trim().toUpperCase(Locale.ROOT));
        } else if (alta) {
            e.setCiclo("MENSUAL");
        }
        if (r.facturacionElectronica() != null) {
            e.setFacturacionElectronica(r.facturacionElectronica());
        }
        if (r.notas() != null) e.setNotas(r.notas().trim());
    }

    /** ACTIVO / PRUEBA / SUSPENDIDO -> banderas de la empresa y fecha de vencimiento si no se indico. */
    private void aplicarEstadoInicial(Empresas e, String estado, String fechaVencimiento, LocalDate hoy) {
        String efectivo = estado == null ? ESTADO_ACTIVO : estado;
        e.setEnPrueba(ESTADO_PRUEBA.equals(efectivo));
        e.setSuspendida(ESTADO_SUSPENDIDO.equals(efectivo));
        if (!vacio(fechaVencimiento)) {
            e.setFechaVencimiento(LocalDate.parse(fechaVencimiento));
        } else if (ESTADO_PRUEBA.equals(efectivo)) {
            e.setFechaVencimiento(hoy.plusDays(diasPrueba));
        } else {
            e.setFechaVencimiento("ANUAL".equals(e.getCiclo()) ? hoy.plusYears(1) : hoy.plusMonths(1));
        }
    }

    static String normalizarEstado(String estado) {
        if (vacio(estado)) {
            return ESTADO_ACTIVO;
        }
        String s = estado.trim().toUpperCase(Locale.ROOT);
        return switch (s) {
            case "ACTIVO" -> ESTADO_ACTIVO;
            case "PRUEBA", "EN PRUEBA" -> ESTADO_PRUEBA;
            case "SUSPENDIDO" -> ESTADO_SUSPENDIDO;
            default -> null;
        };
    }

    private static boolean cicloValido(String c) {
        String s = c.trim().toUpperCase(Locale.ROOT);
        return s.equals("MENSUAL") || s.equals("ANUAL");
    }

    private static boolean correoValido(String c) {
        return c != null && c.trim().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    }

    private static boolean telefonoValido(String t) {
        return t != null && t.replaceAll("\\D", "").length() >= 9;
    }

    private static boolean vacio(String s) {
        return s == null || s.isBlank();
    }
}
