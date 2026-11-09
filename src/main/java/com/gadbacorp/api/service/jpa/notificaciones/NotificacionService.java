package com.gadbacorp.api.service.jpa.notificaciones;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.gadbacorp.api.config.PermisosService;
import com.gadbacorp.api.entity.empleados.Usuarios;
import com.gadbacorp.api.entity.notificaciones.Notificacion;
import com.gadbacorp.api.repository.empleados.UsuarioRepository;
import com.gadbacorp.api.repository.notificaciones.NotificacionRepository;

/** Avisos dentro de la aplicacion: los genera el sistema y cada usuario los ve y marca solo para el. */
@Service
public class NotificacionService {

    private static final Logger log = LoggerFactory.getLogger(NotificacionService.class);

    private final NotificacionRepository notificaciones;
    private final UsuarioRepository usuarios;
    private final PermisosService permisos;

    public NotificacionService(NotificacionRepository notificaciones, UsuarioRepository usuarios, PermisosService permisos) {
        this.notificaciones = notificaciones;
        this.usuarios = usuarios;
        this.permisos = permisos;
    }

    /**
     * Avisa a los usuarios activos de una empresa cuyo rol tiene el modulo indicado. Si el aviso (misma clave) ya esta
     * sin leer para un usuario, no se repite. Un fallo al avisar nunca debe romper la operacion que lo origina.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void notificarEmpresa(Integer idEmpresa, String modulo, String tipo, String severidad, String titulo,
                                 String mensaje, String ruta, String clave) {
        try {
            for (Usuarios u : usuarios.findBySucursal_Empresa_Idempresa(idEmpresa)) {
                if (activo(u) && u.getRol() != null && permisos.rolTieneModulo(u.getRol().getNombre(), modulo)) {
                    crear(u.getIdUsuario(), tipo, severidad, titulo, mensaje, ruta, clave);
                }
            }
        } catch (RuntimeException e) {
            log.warn("No se pudo generar la notificacion '{}': {}", titulo, e.getMessage());
        }
    }

    /** Avisos para el dueno de la plataforma (usuarios SUPERADMIN). */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void notificarSuperadmins(String tipo, String severidad, String titulo, String mensaje, String ruta, String clave) {
        try {
            for (Usuarios u : usuarios.findAll()) {
                if (activo(u) && u.getRol() != null && "SUPERADMIN".equals(u.getRol().getNombre())) {
                    crear(u.getIdUsuario(), tipo, severidad, titulo, mensaje, ruta, clave);
                }
            }
        } catch (RuntimeException e) {
            log.warn("No se pudo generar la notificacion '{}': {}", titulo, e.getMessage());
        }
    }

    private static boolean activo(Usuarios u) {
        return u.getEnable() && (u.getEstado() == null || u.getEstado() == 1);
    }

    private void crear(Integer idUsuario, String tipo, String severidad, String titulo, String mensaje, String ruta, String clave) {
        if (clave != null && notificaciones.existsByIdUsuarioAndClaveAndLeidaFalse(idUsuario, clave)) {
            return;
        }
        Notificacion n = new Notificacion();
        n.setIdUsuario(idUsuario);
        n.setTipo(tipo);
        n.setSeveridad(severidad);
        n.setTitulo(titulo);
        n.setMensaje(mensaje);
        n.setRuta(ruta);
        n.setClave(clave);
        notificaciones.save(n);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> listar(boolean soloNoLeidas, String tipo, int pagina, int tamano) {
        Integer yo = usuarioActual();
        Page<Notificacion> p = notificaciones.buscar(yo, soloNoLeidas, tipo == null || tipo.isBlank() ? null : tipo,
                PageRequest.of(Math.max(pagina, 0), Math.min(Math.max(tamano, 1), 100)));
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("items", p.getContent());
        r.put("total", p.getTotalElements());
        r.put("pagina", p.getNumber());
        r.put("totalPaginas", p.getTotalPages());
        r.put("noLeidas", notificaciones.countByIdUsuarioAndLeidaFalse(yo));
        return r;
    }

    @Transactional(readOnly = true)
    public long noLeidas() {
        return notificaciones.countByIdUsuarioAndLeidaFalse(usuarioActual());
    }

    @Transactional
    public Notificacion marcar(Long id, boolean leida) {
        Notificacion n = propia(id);
        if (Boolean.TRUE.equals(n.getLeida()) != leida) {
            n.setLeida(leida);
            n.setFechaLectura(leida ? LocalDateTime.now() : null);
            notificaciones.save(n);
        }
        return n;
    }

    @Transactional
    public int marcarTodasLeidas() {
        return notificaciones.marcarTodasLeidas(usuarioActual(), LocalDateTime.now());
    }

    @Transactional
    public void eliminar(Long id) {
        notificaciones.delete(propia(id));
    }

    @Transactional
    public int eliminarLeidas() {
        return notificaciones.borrarLeidas(usuarioActual());
    }

    /** Limpieza periodica: las leidas hace mas de {@code dias} dias ya no hacen falta. */
    @Transactional
    public int depurar(int dias) {
        return notificaciones.borrarLeidasAnterioresA(LocalDateTime.now().minusDays(dias));
    }

    private Notificacion propia(Long id) {
        return notificaciones.findByIdNotificacionAndIdUsuario(id, usuarioActual())
                .orElseThrow(() -> new NoSuchElementException("Notificación no encontrada"));
    }

    private static Integer usuarioActual() {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        if (a != null && a.getPrincipal() instanceof Usuarios u && u.getIdUsuario() != null) {
            return u.getIdUsuario();
        }
        throw new NoSuchElementException("Sin usuario autenticado");
    }

    /** Solo para pruebas y procesos internos que necesitan ver lo que se genero para un usuario. */
    public List<Notificacion> deUsuario(Integer idUsuario) {
        return notificaciones.buscar(idUsuario, false, null, PageRequest.of(0, 100)).getContent();
    }
}
