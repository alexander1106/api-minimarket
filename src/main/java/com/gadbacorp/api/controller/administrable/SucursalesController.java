package com.gadbacorp.api.controller.administrable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.gadbacorp.api.entity.dto.administrable.SucursalDTO;
import com.gadbacorp.api.service.administrable.ISucursalesService;

import java.util.List;

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
        // Aquí deberías convertir el DTO a entidad, guardar y luego convertir a DTO nuevamente
        return ResponseEntity.ok(sucursalDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SucursalDTO> actualizar(
            @PathVariable Integer id,
            @RequestBody SucursalDTO sucursalDTO) {
        // Lógica de actualización
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