package com.gadbacorp.api.controller.seguridad;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gadbacorp.api.entity.seguridad.Rol;
import com.gadbacorp.api.service.seguridad.IRolService;

@RestController
@RequestMapping("/api/minimarket")
@CrossOrigin(origins = "http://localhost:4200")
public class RolController {

    @Autowired
    private IRolService rolService;

    @GetMapping("/roles")
    public ResponseEntity<List<Rol>> listar() {
        return ResponseEntity.ok(rolService.listarTodos());
    }

    @GetMapping("/roles/{id}")
    public ResponseEntity<Rol> obtener(@PathVariable Integer id) {
        return rolService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/roles")
    public ResponseEntity<Rol> crear(@RequestBody Rol rol) {
        return ResponseEntity.ok(rolService.guardar(rol));
    }

    @PutMapping("/roles")
    public ResponseEntity<Rol> actualizar(@RequestBody Rol rol) {
        return ResponseEntity.ok(rolService.actualizar(rol));
    }

    @DeleteMapping("/roles/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        rolService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
