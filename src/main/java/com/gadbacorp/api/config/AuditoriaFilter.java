package com.gadbacorp.api.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Set;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gadbacorp.api.entity.auditoria.RegistroAuditoria;
import com.gadbacorp.api.entity.empleados.Usuarios;
import com.gadbacorp.api.service.jpa.auditoria.AuditoriaService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Registra en la auditoria cada operacion que cambia datos (POST, PUT, PATCH, DELETE) y los inicios de sesion.
 * Va dentro de la cadena de seguridad, despues de la autorizacion, para conocer al usuario. Las operaciones que el
 * sistema rechaza por falta de permiso no llegan hasta aqui.
 */
public class AuditoriaFilter extends OncePerRequestFilter {

    private static final String PREFIJO = "/api/minimarket/";
    private static final Set<String> METODOS = Set.of("POST", "PUT", "PATCH", "DELETE");
    /** Operaciones de rutina que no aportan: marcar avisos como leidos, subir imagenes sueltas... */
    private static final Set<String> IGNORAR = Set.of("notificaciones", "productos/upload", "reset-password", "enviar-link-recuperacion");
    private static final ObjectMapper JSON = new ObjectMapper();

    private final AuditoriaService servicio;

    public AuditoriaFilter(AuditoriaService servicio) {
        this.servicio = servicio;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String ruta = request.getRequestURI();
        int i = ruta.indexOf(PREFIJO);
        if (i < 0 || !METODOS.contains(request.getMethod())) {
            return true;
        }
        String resto = ruta.substring(i + PREFIJO.length());
        if (IGNORAR.stream().anyMatch(resto::startsWith)) {
            return true;
        }
        // no se envuelve la peticion si la accion no se va a guardar (por defecto solo editar y eliminar)
        if (resto.startsWith("login")) {
            return !servicio.registra(RegistroAuditoria.INICIO_SESION) && !servicio.registra(RegistroAuditoria.INICIO_SESION_FALLIDO);
        }
        return !servicio.registra(AuditoriaService.accion(request.getMethod()));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        ContentCachingRequestWrapper req = new ContentCachingRequestWrapper(request, 20_000);
        ContentCachingResponseWrapper res = new ContentCachingResponseWrapper(response);
        long inicio = System.currentTimeMillis();
        try {
            chain.doFilter(req, res);
        } finally {
            try {
                registrar(req, res, System.currentTimeMillis() - inicio);
            } catch (RuntimeException e) {
                logger.warn("No se pudo preparar el registro de auditoria: " + e.getMessage());
            }
            res.copyBodyToResponse();
        }
    }

    private void registrar(ContentCachingRequestWrapper req, ContentCachingResponseWrapper res, long ms) {
        String ruta = req.getRequestURI();
        String resto = ruta.substring(ruta.indexOf(PREFIJO) + PREFIJO.length());
        String[] tramos = resto.split("/");
        String recurso = tramos[0];
        int estado = res.getStatus();
        boolean exito = estado < 400;

        String cuerpo = new String(req.getContentAsByteArray(), StandardCharsets.UTF_8);
        String respuesta = new String(res.getContentAsByteArray(), StandardCharsets.UTF_8);

        RegistroAuditoria a = new RegistroAuditoria();
        a.setMetodo(req.getMethod());
        a.setRuta(recortar(ruta + (req.getQueryString() == null ? "" : "?" + req.getQueryString()), 300));
        a.setRecurso(recortar(recurso, 60));
        a.setModulo(AuditoriaService.modulo(recurso));
        a.setEstadoHttp(estado);
        a.setExito(exito);
        a.setIp(ip(req));
        a.setUserAgent(recortar(req.getHeader("User-Agent"), 200));
        a.setDuracionMs(ms);
        usuario(a);

        if ("login".equals(recurso)) {
            a.setAccion(exito ? RegistroAuditoria.INICIO_SESION : RegistroAuditoria.INICIO_SESION_FALLIDO);
            if (a.getUsername() == null) {
                a.setUsername(recortar(texto(cuerpo, "username"), 80));
            }
            if (!exito) {
                a.setDetalle("Credenciales inválidas o usuario deshabilitado");
            }
        } else {
            a.setAccion(AuditoriaService.accion(req.getMethod()));
            a.setIdRegistro(idRegistro(tramos, respuesta, cuerpo));
            String datos = AuditoriaService.sanitizar(cuerpo);
            a.setDetalle(exito ? datos : (datos == null ? "" : datos + "\n") + "Respuesta: " + recortar(respuesta, 500));
        }
        if (!servicio.registra(a.getAccion())) {
            return;
        }
        a.setDescripcion(recortar(AuditoriaService.frase(a), 300));
        servicio.registrar(a);
    }

    private static void usuario(RegistroAuditoria a) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            return;
        }
        a.setUsername(auth.getName());
        if (auth.getPrincipal() instanceof Usuarios u) {
            a.setIdUsuario(u.getIdUsuario());
            a.setRol(u.getRol() == null ? null : u.getRol().getNombre());
            if (u.getSucursal() != null && u.getSucursal().getEmpresa() != null) {
                a.setIdEmpresa(u.getSucursal().getEmpresa().getIdempresa());
                a.setEmpresa(recortar(u.getSucursal().getEmpresa().getRazonsocial(), 160));
            }
        }
    }

    /** El id de lo afectado: el numero de la ruta (/productos/5), o el id del cuerpo o de la respuesta (al crear). */
    private static String idRegistro(String[] tramos, String respuesta, String cuerpo) {
        for (int i = 1; i < tramos.length; i++) {
            if (tramos[i].matches("\\d{1,12}")) {
                return tramos[i];
            }
        }
        String deRespuesta = primerId(respuesta);
        return deRespuesta != null ? deRespuesta : primerId(cuerpo);
    }

    private static String primerId(String json) {
        if (json == null || json.isBlank()) {
            return null;
        }
        try {
            JsonNode nodo = JSON.readTree(json);
            if (nodo.isObject()) {
                var campos = nodo.fields();
                while (campos.hasNext()) {
                    var e = campos.next();
                    String k = e.getKey().toLowerCase();
                    if (k.startsWith("id") && e.getValue().isIntegralNumber() && !k.equals("idsucursal") && !k.equals("idempresa")
                            && !k.equals("id_cliente") && !k.equals("id_metodo_pago")) {
                        return e.getValue().asText();
                    }
                }
            }
        } catch (Exception e) {
            // respuesta que no es JSON: sin id
        }
        return null;
    }

    private static String texto(String json, String campo) {
        try {
            JsonNode n = JSON.readTree(json).get(campo);
            return n == null ? null : n.asText();
        } catch (Exception e) {
            return null;
        }
    }

    private static String ip(HttpServletRequest req) {
        String reenviada = req.getHeader("X-Forwarded-For");
        String ip = reenviada != null && !reenviada.isBlank() ? reenviada.split(",")[0].trim() : req.getRemoteAddr();
        return recortar(ip, 45);
    }

    private static String recortar(String s, int max) {
        return s == null || s.length() <= max ? s : s.substring(0, max);
    }
}
