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

import com.gadbacorp.api.entity.seguridad.RolModulo;
import com.gadbacorp.api.service.seguridad.IRolModuloService;

@RestController
@RequestMapping("/api/minimarket")
@CrossOrigin(origins = "*")
public class RolModuloController {

    @Autowired
    private IRolModuloService rolModuloService;

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
        return ResponseEntity.ok(rolModuloService.guardar(rolModulo));
    }

    @PutMapping("/rolesmodulos")
    public ResponseEntity<RolModulo> actualizar(@RequestBody RolModulo rolModulo) {
        return ResponseEntity.ok(rolModuloService.actualizar(rolModulo));
    }

    @DeleteMapping("/rolesmodulos/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        rolModuloService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
