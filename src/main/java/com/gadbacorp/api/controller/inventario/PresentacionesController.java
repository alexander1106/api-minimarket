package com.gadbacorp.api.controller.inventario;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gadbacorp.api.entity.inventario.PresentacionDTO;
import com.gadbacorp.api.service.jpa.inventario.PresentacionService;

import io.swagger.v3.oas.annotations.tags.Tag;

/** Presentaciones de un producto: "Saco 50 kg" (factor 50), "Paquete x12" (factor 12)... Modulo Productos. */
@Tag(name = "Inventario - Presentaciones")
@RestController
@RequestMapping("/api/minimarket")
public class PresentacionesController {

    @Autowired
    private PresentacionService presentacionService;

    @GetMapping("/productos/{idProducto}/presentaciones")
    public List<PresentacionDTO> listar(@PathVariable Integer idProducto) {
        return presentacionService.listar(idProducto);
    }

    @PostMapping("/productos/{idProducto}/presentaciones")
    public ResponseEntity<PresentacionDTO> crear(@PathVariable Integer idProducto, @RequestBody PresentacionDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(presentacionService.crear(idProducto, dto));
    }

    @PutMapping("/presentaciones")
    public PresentacionDTO actualizar(@RequestBody PresentacionDTO dto) {
        return presentacionService.actualizar(dto);
    }

    @DeleteMapping("/presentaciones/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        presentacionService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
