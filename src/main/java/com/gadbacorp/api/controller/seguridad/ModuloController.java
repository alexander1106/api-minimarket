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
@CrossOrigin(origins = "*") 
public class ModuloController {

    @Autowired
    private IModuloService moduloService;

    @GetMapping("/modulos")
    public ResponseEntity<List<Modulo>> listar() {
        return ResponseEntity.ok(moduloService.listarTodos());
    }

    @GetMapping("/modulos/{id}")
    public ResponseEntity<Modulo> obtener(@PathVariable Integer id) {
        return moduloService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/modulos")
    public ResponseEntity<Modulo> crear(@RequestBody Modulo modulo) {
        return ResponseEntity.ok(moduloService.guardar(modulo));
    }

    @PutMapping("/modulos")
    public ResponseEntity<Modulo> actualizar(@RequestBody Modulo modulo) {
        return ResponseEntity.ok(moduloService.actualizar( modulo));
    }

    @DeleteMapping("/modulos/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        moduloService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}