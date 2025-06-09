package com.gadbacorp.api.controller.administrable;

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

import com.gadbacorp.api.entity.administrable.SucursalDTO;
import com.gadbacorp.api.service.administrable.ISucursalesService;
@RestController
@RequestMapping("/api/minimarket/sucursales")
public class SucursalesController {

    private final ISucursalesService sucursalesService;

    @Autowired
    public SucursalesController(ISucursalesService sucursalesService) {
        this.sucursalesService = sucursalesService;
    }

    @GetMapping
    public ResponseEntity<List<SucursalDTO>> listarTodas() {
        return ResponseEntity.ok(sucursalesService.buscarTodosDTO());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SucursalDTO> buscarPorId(@PathVariable Integer id) {
        return sucursalesService.buscarIdDTO(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<SucursalDTO> crear(@RequestBody SucursalDTO sucursalDTO) {
        return ResponseEntity.ok(sucursalDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SucursalDTO> actualizar(
            @PathVariable Integer id,
            @RequestBody SucursalDTO sucursalDTO) {
        return ResponseEntity.ok(sucursalDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Integer id) {
    if (sucursalesService.buscarIdDTO(id).isEmpty()) {
        return ResponseEntity.notFound().build();
    }
    sucursalesService.eliminar(id);
    return ResponseEntity.ok("Sucursal eliminada correctamente.");
}
}