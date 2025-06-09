package com.gadbacorp.api.gadbacorp.controller.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.gadbacorp.api.gadbacorp.entity.security.Rol;
import com.gadbacorp.api.gadbacorp.service.security.RolService;

import java.util.List;

@RestController
@RequestMapping("/api/minimarket")
@CrossOrigin("/**")// Ajusta según tu frontend
public class RolController {

    @Autowired
    private RolService rolService;

//  
    @GetMapping("/roles")
    public ResponseEntity<List<Rol>> listarRoles() {
        return ResponseEntity.ok(rolService.listarTodos());
    }

    @GetMapping("/rol/{id}")
    public ResponseEntity<Rol> obtenerRol(@PathVariable Long id) {
        return rolService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/rol")
    public ResponseEntity<Rol> crearRol(@RequestBody Rol rol) {
        return ResponseEntity.ok(rolService.guardar(rol));
    }

    @PutMapping("/rol/{id}")
    public ResponseEntity<Rol> actualizarRol(@PathVariable Long id, @RequestBody Rol rol) {
        return ResponseEntity.ok(rolService.actualizar(id, rol));
    }

    @DeleteMapping("/rol/{id}")
    public ResponseEntity<Void> eliminarRol(@PathVariable Long id) {
        rolService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
