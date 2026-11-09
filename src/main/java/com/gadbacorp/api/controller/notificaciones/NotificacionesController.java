package com.gadbacorp.api.controller.notificaciones;

import java.util.Map;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gadbacorp.api.entity.notificaciones.Notificacion;
import com.gadbacorp.api.service.jpa.notificaciones.NotificacionService;

import io.swagger.v3.oas.annotations.tags.Tag;

/** Las notificaciones del usuario autenticado: cualquier usuario ve y marca solo las suyas. */
@Tag(name = "Notificaciones")
@RestController
@RequestMapping("/api/minimarket/notificaciones")
public class NotificacionesController {

    private final NotificacionService service;

    public NotificacionesController(NotificacionService service) {
        this.service = service;
    }

    /** Lista paginada, la mas reciente primero. Respuesta: {items, total, pagina, totalPaginas, noLeidas}. */
    @GetMapping
    public Map<String, Object> listar(@RequestParam(defaultValue = "false") boolean soloNoLeidas,
                                      @RequestParam(required = false) String tipo,
                                      @RequestParam(defaultValue = "0") int pagina,
                                      @RequestParam(defaultValue = "20") int tamano) {
        return service.listar(soloNoLeidas, tipo, pagina, tamano);
    }

    /** Para el numerito rojo de la campana. Consultalo cada 30-60 segundos. */
    @GetMapping("/contador")
    public Map<String, Long> contador() {
        return Map.of("noLeidas", service.noLeidas());
    }

    @PutMapping("/{id}/leida")
    public Notificacion marcarLeida(@PathVariable Long id) {
        return service.marcar(id, true);
    }

    @PutMapping("/{id}/no-leida")
    public Notificacion marcarNoLeida(@PathVariable Long id) {
        return service.marcar(id, false);
    }

    @PutMapping("/marcar-todas")
    public Map<String, Integer> marcarTodas() {
        return Map.of("marcadas", service.marcarTodasLeidas());
    }

    @DeleteMapping("/{id}")
    public Map<String, String> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return Map.of("message", "Notificación eliminada");
    }

    /** Borra todas las ya leidas del usuario. */
    @DeleteMapping("/leidas")
    public Map<String, Integer> eliminarLeidas() {
        return Map.of("eliminadas", service.eliminarLeidas());
    }
}
