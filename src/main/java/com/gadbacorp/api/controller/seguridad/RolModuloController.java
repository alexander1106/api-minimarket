package com.gadbacorp.api.controller.seguridad;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gadbacorp.api.entity.seguridad.RolModulo;
import com.gadbacorp.api.excepciones.RecursoDuplicadoException;
import com.gadbacorp.api.service.seguridad.IRolModuloService;

import io.swagger.v3.oas.annotations.tags.Tag;
@Tag(name = "Seguridad - Permisos por rol")
@RestController
@RequestMapping("/api/minimarket")
public class RolModuloController {

    @Autowired
    private IRolModuloService rolModuloService;

    @Autowired
    private com.gadbacorp.api.config.PermisosService permisosService;

    @GetMapping("/rolesmodulos")
    public ResponseEntity<List<RolModulo>> listar() {
        return ResponseEntity.ok(rolModuloService.listarTodos());
    }

    @GetMapping("/rolesmodulos/{id}")
    public ResponseEntity<RolModulo> obtener(@PathVariable Integer id) {
        return rolModuloService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/rolesmodulos")
    public ResponseEntity<RolModulo> crear(@RequestBody RolModulo rolModulo) {
        if (rolModulo.getRol() != null && rolModulo.getModulo() != null) {
            boolean repetido = rolModuloService.listarTodos().stream().anyMatch(rm -> rm.getRol() != null
                    && rm.getModulo() != null
                    && rm.getRol().getId().equals(rolModulo.getRol().getId())
                    && rm.getModulo().getIdModulo().equals(rolModulo.getModulo().getIdModulo()));
            if (repetido) {
                throw new RecursoDuplicadoException("El rol ya tiene asignado ese módulo.");
            }
        }
        RolModulo guardado = rolModuloService.guardar(rolModulo);
        permisosService.limpiar();
        return ResponseEntity.ok(guardado);
    }

    @PutMapping("/rolesmodulos")
    public ResponseEntity<RolModulo> actualizar(@RequestBody RolModulo rolModulo) {
        RolModulo guardado = rolModuloService.actualizar(rolModulo);
        permisosService.limpiar();
        return ResponseEntity.ok(guardado);
    }

    @DeleteMapping("/rolesmodulos/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        rolModuloService.eliminar(id);
        permisosService.limpiar();
        return ResponseEntity.noContent().build();
    }
}
