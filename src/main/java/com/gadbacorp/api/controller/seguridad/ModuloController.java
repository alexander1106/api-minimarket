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

import com.gadbacorp.api.config.SeguridadCatalogo;
import com.gadbacorp.api.entity.seguridad.Modulo;
import com.gadbacorp.api.excepciones.OperacionNoPermitidaException;
import com.gadbacorp.api.service.seguridad.IModuloService;

import io.swagger.v3.oas.annotations.tags.Tag;
@Tag(name = "Seguridad - Módulos")
@RestController
@RequestMapping("/api/minimarket")
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
        moduloService.obtenerPorId(modulo.getIdModulo()).ifPresent(actual -> {
            if (SeguridadCatalogo.esModuloDelSistema(actual.getNombre())
                    && !actual.getNombre().equalsIgnoreCase(modulo.getNombre())) {
                throw new OperacionNoPermitidaException(
                        "El módulo " + actual.getNombre() + " es del sistema y no puede cambiar de nombre.");
            }
        });
        return ResponseEntity.ok(moduloService.actualizar( modulo));
    }

    @DeleteMapping("/modulos/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        moduloService.obtenerPorId(id).ifPresent(actual -> {
            if (SeguridadCatalogo.esModuloDelSistema(actual.getNombre())) {
                throw new OperacionNoPermitidaException(
                        "El módulo " + actual.getNombre() + " es del sistema y no se puede eliminar.");
            }
        });
        moduloService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}