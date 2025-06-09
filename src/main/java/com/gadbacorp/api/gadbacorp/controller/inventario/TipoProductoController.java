package com.gadbacorp.api.gadbacorp.controller.inventario;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

import com.gadbacorp.api.gadbacorp.entity.inventario.TipoProducto;
import com.gadbacorp.api.gadbacorp.entity.inventario.TipoProductoDTO;
import com.gadbacorp.api.gadbacorp.service.inventario.ITipoProductoService;

@RestController
@RequestMapping("/api/minimarket/tipoproducto")
public class TipoProductoController {

    @Autowired
    private ITipoProductoService serviceTipoProducto;

    // Convertir Entity -> DTO
    private TipoProductoDTO toDTO(TipoProducto entity) {
        return new TipoProductoDTO(entity.getIdtipoproducto(), entity.getNombre());
    }

    // Convertir DTO -> Entity
    private TipoProducto toEntity(TipoProductoDTO dto) {
        TipoProducto entity = new TipoProducto();
        entity.setIdtipoproducto(dto.getIdtipoproducto());
        entity.setNombre(dto.getNombre());
        return entity;
    }

    // Listar todos los tipos de producto
    @GetMapping
    public List<TipoProductoDTO> listarTodos() {
        return serviceTipoProducto.buscarTodos()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // Buscar por ID
    @GetMapping("/{id}")
    public ResponseEntity<TipoProductoDTO> buscarPorId(@PathVariable Integer id) {
        Optional<TipoProducto> tipo = serviceTipoProducto.buscarId(id);
        return tipo.map(value -> ResponseEntity.ok(toDTO(value)))
                   .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Guardar nuevo tipo de producto
    @PostMapping
    public ResponseEntity<?> guardar(@RequestBody TipoProductoDTO dto) {
    try {
        TipoProducto guardado = serviceTipoProducto.guardar(toEntity(dto));
        return ResponseEntity.ok(toDTO(guardado));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    // Modificar tipo de producto
    @PutMapping
    public ResponseEntity<?> modificar(@RequestBody TipoProductoDTO dto) {
    try {
        Optional<TipoProducto> existente = serviceTipoProducto.buscarId(dto.getIdtipoproducto());

        if (existente.isPresent()) {
            TipoProducto actualizado = serviceTipoProducto.modificar(toEntity(dto));
            return ResponseEntity.ok(toDTO(actualizado));
        } else {
            return ResponseEntity.notFound().build();
        }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    // Eliminar tipo de producto
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Integer id) {
        Optional<TipoProducto> tipo = serviceTipoProducto.buscarId(id);
        if (tipo.isPresent()) {
            serviceTipoProducto.eliminar(id);
            return ResponseEntity.ok("Tipo de producto eliminado correctamente");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
