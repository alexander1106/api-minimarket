package com.gadbacorp.api.service.jpa.auditoria;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.gadbacorp.api.entity.auditoria.RegistroAuditoria;
import com.gadbacorp.api.repository.auditoria.AuditoriaRepository;

/** Registro de quien hizo que en el sistema. Lo consulta solo el SUPERADMIN. */
@Service
public class AuditoriaService {

    private static final Logger log = LoggerFactory.getLogger(AuditoriaService.class);
    private static final ObjectMapper JSON = new ObjectMapper();

    /** Campos que nunca se guardan: se reemplazan por "***". */
    private static final Set<String> SECRETOS = Set.of("password", "contrasena", "contraseña", "clave", "token",
            "access_token", "refresh_token", "llave_secreta", "secret", "newpassword", "currentpassword");
    private static final int MAX_TEXTO = 200;
    private static final int MAX_DETALLE = 4000;

    private final AuditoriaRepository auditoria;
    private final int retencionDias;
    private final Set<String> accionesRegistradas;

    /**
     * @param acciones cuales se guardan: EDITAR, ELIMINAR, CREAR, INICIO_SESION, INICIO_SESION_FALLIDO u OTRO.
     *                 Por defecto solo las que modifican o borran lo que ya existe.
     */
    public AuditoriaService(AuditoriaRepository auditoria, @Value("${app.auditoria.retencion-dias:365}") int retencionDias,
                            @Value("${app.auditoria.acciones:EDITAR,ELIMINAR}") String acciones) {
        this.auditoria = auditoria;
        this.retencionDias = retencionDias;
        this.accionesRegistradas = java.util.Arrays.stream(acciones.split(",")).map(String::trim)
                .map(s -> s.toUpperCase(Locale.ROOT)).filter(s -> !s.isEmpty()).collect(java.util.stream.Collectors.toSet());
    }

    public boolean registra(String accion) {
        return accionesRegistradas.contains(accion);
    }

    /** Nunca lanza: auditar no debe romper la operacion auditada. */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void registrar(RegistroAuditoria r) {
        try {
            auditoria.save(r);
        } catch (RuntimeException e) {
            log.warn("No se pudo registrar la auditoria: {}", e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public Map<String, Object> buscar(LocalDate desde, LocalDate hasta, String username, Integer idEmpresa, String accion,
                                      String modulo, Boolean exito, String q, int pagina, int tamano) {
        LocalDateTime ini = (desde == null ? LocalDate.now().minusDays(30) : desde).atStartOfDay();
        LocalDateTime fin = (hasta == null ? LocalDate.now() : hasta).plusDays(1).atStartOfDay().minusNanos(1);
        Page<RegistroAuditoria> p = auditoria.buscar(ini, fin, vacioANull(username), idEmpresa, vacioANull(accion),
                vacioANull(modulo), exito, vacioANull(q),
                PageRequest.of(Math.max(pagina, 0), Math.min(Math.max(tamano, 1), 200)));
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("items", p.getContent().stream().map(a -> fila(a, false)).toList());
        r.put("total", p.getTotalElements());
        r.put("pagina", p.getNumber());
        r.put("totalPaginas", p.getTotalPages());
        return r;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> detalle(Long id) {
        return fila(auditoria.findById(id).orElseThrow(() -> new NoSuchElementException("Registro de auditoría no encontrado")), true);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> resumen(LocalDate desde, LocalDate hasta) {
        LocalDateTime ini = (desde == null ? LocalDate.now().minusDays(30) : desde).atStartOfDay();
        LocalDateTime fin = (hasta == null ? LocalDate.now() : hasta).plusDays(1).atStartOfDay().minusNanos(1);
        Map<String, Long> porAccion = new LinkedHashMap<>();
        long total = 0;
        for (Object[] f : auditoria.contarPorAccion(ini, fin)) {
            porAccion.put(String.valueOf(f[0]), (Long) f[1]);
            total += (Long) f[1];
        }
        Map<String, Long> porModulo = new LinkedHashMap<>();
        for (Object[] f : auditoria.contarPorModulo(ini, fin)) {
            porModulo.put(String.valueOf(f[0]), (Long) f[1]);
        }
        Map<String, Long> porUsuario = new LinkedHashMap<>();
        for (Object[] f : auditoria.contarPorUsuario(ini, fin, PageRequest.of(0, 10))) {
            porUsuario.put(String.valueOf(f[0]), (Long) f[1]);
        }
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("total", total);
        r.put("fallidos", auditoria.contarFallidos(ini, fin));
        r.put("porAccion", porAccion);
        r.put("porModulo", porModulo);
        r.put("topUsuarios", porUsuario);
        return r;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> filtros() {
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("acciones", List.of(RegistroAuditoria.CREAR, RegistroAuditoria.EDITAR, RegistroAuditoria.ELIMINAR,
                RegistroAuditoria.INICIO_SESION, RegistroAuditoria.INICIO_SESION_FALLIDO, RegistroAuditoria.OTRO));
        r.put("modulos", auditoria.modulos());
        r.put("usuarios", auditoria.usuarios());
        return r;
    }

    private static Map<String, Object> fila(RegistroAuditoria a, boolean conDetalle) {
        Map<String, Object> f = new LinkedHashMap<>();
        f.put("id", a.getId());
        f.put("fecha", a.getFecha());
        f.put("username", a.getUsername());
        f.put("rol", a.getRol());
        f.put("idEmpresa", a.getIdEmpresa());
        f.put("empresa", a.getEmpresa());
        f.put("accion", a.getAccion());
        f.put("modulo", a.getModulo());
        f.put("recurso", a.getRecurso());
        f.put("idRegistro", a.getIdRegistro());
        f.put("descripcion", a.getDescripcion());
        f.put("exito", a.getExito());
        f.put("estadoHttp", a.getEstadoHttp());
        if (conDetalle) {
            f.put("idUsuario", a.getIdUsuario());
            f.put("metodo", a.getMetodo());
            f.put("ruta", a.getRuta());
            f.put("ip", a.getIp());
            f.put("userAgent", a.getUserAgent());
            f.put("duracionMs", a.getDuracionMs());
            f.put("detalle", a.getDetalle());
        }
        return f;
    }

    private static String vacioANull(String s) {
        return s == null || s.isBlank() ? null : s.trim();
    }

    @Scheduled(cron = "${app.auditoria.cron-depuracion:0 30 3 * * *}")
    @Transactional
    public void depurar() {
        int n = auditoria.borrarAnterioresA(LocalDateTime.now().minusDays(retencionDias));
        if (n > 0) {
            log.info("Auditoria: {} registros anteriores a {} dias eliminados", n, retencionDias);
        }
    }

    /** Quita contrasenas y tokens, acorta textos largos (imagenes en base64) y limita el tamano total. */
    public static String sanitizar(String json) {
        if (json == null || json.isBlank()) {
            return null;
        }
        try {
            JsonNode nodo = JSON.readTree(json);
            limpiar(nodo);
            String s = JSON.writeValueAsString(nodo);
            return s.length() > MAX_DETALLE ? s.substring(0, MAX_DETALLE) + "…" : s;
        } catch (Exception e) {
            return "[cuerpo no JSON]";
        }
    }

    private static void limpiar(JsonNode nodo) {
        if (nodo instanceof ObjectNode obj) {
            for (String campo : List.copyOf(iterable(obj.fieldNames()))) {
                JsonNode valor = obj.get(campo);
                if (SECRETOS.contains(campo.toLowerCase(Locale.ROOT))) {
                    obj.put(campo, "***");
                } else if (valor.isTextual() && valor.asText().length() > MAX_TEXTO) {
                    obj.put(campo, valor.asText().substring(0, MAX_TEXTO) + "… [" + valor.asText().length() + " caracteres]");
                } else {
                    limpiar(valor);
                }
            }
        } else if (nodo != null && nodo.isArray()) {
            nodo.forEach(AuditoriaService::limpiar);
        }
    }

    private static List<String> iterable(java.util.Iterator<String> it) {
        List<String> l = new java.util.ArrayList<>();
        it.forEachRemaining(l::add);
        return l;
    }

    /** Modulo del sistema al que pertenece el primer tramo de la ruta. */
    public static String modulo(String recurso) {
        if (recurso == null) {
            return null;
        }
        return switch (recurso) {
            case "login", "enviar-link-recuperacion", "reset-password" -> "Autenticación";
            case "productos", "categorias", "tipoproducto", "unidad_medida", "presentaciones", "promociones" -> "Productos";
            case "inventario", "inventarioproducto", "ajustes", "traslados", "kardex", "lotes" -> "Inventario";
            case "almacenes" -> "Almacenes";
            case "compras", "detalles-compras", "proveedores" -> "Compras";
            case "ventas", "detalles-ventas", "cotizaciones", "detalles-cotizaciones", "clientes", "metodos-pago",
                 "metodos-pago-catalogo", "devoluciones", "detalles-devoluciones" -> "Ventas";
            case "delivery", "vehiculo", "estadomovimiento" -> "Delivery";
            case "cajas", "aperturas-cajas", "aperturas-caja", "arqueos-cajas", "transacciones-cajas",
                 "transferencias-entre-cajas", "pagos", "saldo-metodo" -> "Caja";
            case "usuarios", "roles", "rolesmodulos", "modulos" -> "Seguridad";
            case "empresas", "sucursales", "planes", "registros" -> "Administrable";
            default -> recurso;
        };
    }

    public static String accion(String metodo) {
        return switch (metodo) {
            case "POST" -> RegistroAuditoria.CREAR;
            case "PUT", "PATCH" -> RegistroAuditoria.EDITAR;
            case "DELETE" -> RegistroAuditoria.ELIMINAR;
            default -> RegistroAuditoria.OTRO;
        };
    }

    public static String frase(RegistroAuditoria a) {
        String quien = a.getUsername() == null ? "Un visitante" : a.getUsername();
        String objetivo = a.getRecurso() + (a.getIdRegistro() == null ? "" : " (#" + a.getIdRegistro() + ")");
        String hecho = switch (a.getAccion()) {
            case RegistroAuditoria.CREAR -> "creó un registro en " + objetivo;
            case RegistroAuditoria.EDITAR -> "editó " + objetivo;
            case RegistroAuditoria.ELIMINAR -> "eliminó " + objetivo;
            case RegistroAuditoria.INICIO_SESION -> "inició sesión";
            case RegistroAuditoria.INICIO_SESION_FALLIDO -> "intentó iniciar sesión y falló";
            default -> "ejecutó " + a.getMetodo() + " " + a.getRecurso();
        };
        return quien + " " + hecho + (Boolean.FALSE.equals(a.getExito()) && !RegistroAuditoria.INICIO_SESION_FALLIDO.equals(a.getAccion())
                ? " (falló: HTTP " + a.getEstadoHttp() + ")" : "");
    }
}
