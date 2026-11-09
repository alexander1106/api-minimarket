package com.gadbacorp.api.controller.seguridad;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gadbacorp.api.config.SeguridadCatalogo;
import com.gadbacorp.api.entity.seguridad.RolResumenDTO;
import com.gadbacorp.api.entity.seguridad.RolResumenDTO.ModulosRequest;
import com.gadbacorp.api.entity.seguridad.RolResumenDTO.RolRequest;
import com.gadbacorp.api.service.jpa.seguridad.RolAdminService;

import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Roles y sus modulos. Consultar: cualquier usuario autenticado (los formularios de usuarios necesitan la lista).
 * Crear, editar, asignar modulos y eliminar: SUPERADMIN (modulo Administrable).
 */
@Tag(name = "Seguridad - Roles")
@RestController
@RequestMapping("/api/minimarket")
public class RolController {

    @Autowired
    private RolAdminService rolAdminService;

    /** Todos los roles con sus modulos y la cantidad de usuarios (matriz de permisos). */
    @GetMapping("/roles")
    public List<RolResumenDTO> listar() {
        return rolAdminService.listar();
    }

    /** Roles que puede asignar quien llama al crear un usuario (solo un SUPERADMIN ve SUPERADMIN). */
    @GetMapping("/roles/asignables")
    public List<RolResumenDTO> asignables() {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        boolean superadmin = a != null && a.getAuthorities().stream()
                .anyMatch(g -> SeguridadCatalogo.SUPERADMIN.equals(g.getAuthority()));
        return rolAdminService.asignables(superadmin);
    }

    @GetMapping("/roles/{id}")
    public RolResumenDTO obtener(@PathVariable Integer id) {
        return rolAdminService.obtener(id);
    }

    /** Crea un rol; {@code modulos} (ids) es opcional y se asigna en el mismo paso. */
    @PostMapping("/roles")
    public ResponseEntity<RolResumenDTO> crear(@RequestBody RolRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(rolAdminService.crear(req));
    }

    /** Edita el nombre (salvo roles del sistema) y/o reemplaza sus modulos si se envian. */
    @PutMapping("/roles")
    public RolResumenDTO actualizar(@RequestBody RolRequest req) {
        return rolAdminService.actualizar(req);
    }

    /** Guarda de una vez los modulos del rol: la lista enviada reemplaza a la actual. */
    @PutMapping("/roles/{id}/modulos")
    public RolResumenDTO asignarModulos(@PathVariable Integer id, @RequestBody ModulosRequest req) {
        return rolAdminService.asignarModulos(id, req.modulos());
    }

    @DeleteMapping("/roles/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        rolAdminService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
