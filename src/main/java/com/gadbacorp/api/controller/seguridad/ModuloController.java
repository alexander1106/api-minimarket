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

import com.gadbacorp.api.entity.seguridad.Modulo;
import com.gadbacorp.api.service.seguridad.IModuloService;

@RestController
@RequestMapping("/api/minimarket")
@CrossOrigin(origins = "*") // Ajusta según tu frontend
public class ModuloController {

    @Autowired
    private IModuloService moduloService;

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