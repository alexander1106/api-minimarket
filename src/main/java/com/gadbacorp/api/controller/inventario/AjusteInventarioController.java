package com.gadbacorp.api.controller.inventario;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
import org.springframework.web.server.ResponseStatusException;

import com.gadbacorp.api.entity.inventario.AjusteInventario;
import com.gadbacorp.api.entity.inventario.AjusteInventarioDTO;
import com.gadbacorp.api.service.inventario.IAjusteInventarioService;

@RestController
@RequestMapping("/api/minimarket/ajusteinventario")
@CrossOrigin("*")
public class AjusteInventarioController {

    @Autowired
    private IAjusteInventarioService serviceAjusteInventario;

    // Mapper: Entity -> DTO
    private AjusteInventarioDTO toDTO(AjusteInventario aj) {
        AjusteInventarioDTO dto = new AjusteInventarioDTO();
        dto.setIdajusteinventario(aj.getIdajusteinventario());
        dto.setIdinventario(aj.getInventario().getIdinventario());
        dto.setCantidad(aj.getCantidad());
        dto.setDescripcion(aj.getDescripcion());
        dto.setFechaAjuste(aj.getFechaAjuste());
        return dto;
    }

    @PostMapping
    public ResponseEntity<AjusteInventarioDTO> crear(@RequestBody AjusteInventarioDTO dto) {
        try {
            AjusteInventario creado = serviceAjusteInventario.ajustarStock(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(creado));
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
        }
    }

    @GetMapping
    public ResponseEntity<List<AjusteInventarioDTO>> listar() {
        List<AjusteInventarioDTO> list = serviceAjusteInventario.buscarTodos().stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AjusteInventarioDTO> obtener(@PathVariable Integer id) {
        return serviceAjusteInventario.buscarId(id)
            .map(this::toDTO)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<AjusteInventarioDTO> actualizar(@PathVariable Integer id,
                                                           @RequestBody AjusteInventarioDTO dto) {
        try {
            dto.setIdajusteinventario(id);
            AjusteInventario actualizado = serviceAjusteInventario.modificarAjuste(dto);
            return ResponseEntity.ok(toDTO(actualizado));
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Integer id) {
        serviceAjusteInventario.eliminar(id);
        return ResponseEntity.ok("Ajuste de inventario eliminado");
    }
}
