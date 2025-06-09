package com.gadbacorp.api.gadbacorp.controller.security;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.gadbacorp.api.gadbacorp.entity.security.Modulo;
import com.gadbacorp.api.gadbacorp.service.security.ModuloService;

import java.util.List;

@RestController
@RequestMapping("/api/minimarket")
@CrossOrigin(origins = "*") // Ajusta según tu frontend
public class ModuloController {

    @Autowired
    private ModuloService moduloService;

    @GetMapping("/modulos")
    public ResponseEntity<List<Modulo>> listar() {
        return ResponseEntity.ok(moduloService.listarTodos());
    }

    @GetMapping("/modulo/{id}")
    public ResponseEntity<Modulo> obtener(@PathVariable Long id) {
        return moduloService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/modulo")
    public ResponseEntity<Modulo> crear(@RequestBody Modulo modulo) {
        return ResponseEntity.ok(moduloService.guardar(modulo));
    }

    @PutMapping("/modulo/{id}")
    public ResponseEntity<Modulo> actualizar(@PathVariable Long id, @RequestBody Modulo modulo) {
        return ResponseEntity.ok(moduloService.actualizar(id, modulo));
    }

    @DeleteMapping("/modulo/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        moduloService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
