package com.gadbacorp.api.controller.inventario;

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

import com.gadbacorp.api.entity.inventario.UnidadDeMedida;
import com.gadbacorp.api.entity.inventario.UnidadDeMedidaDTO;
import com.gadbacorp.api.service.inventario.IUnidadDeMedidaService;

@RestController
@RequestMapping("/api/minimarket/unidad_medida")
public class UnidadDeMedidaController {

    @Autowired
    private IUnidadDeMedidaService serviceUnidadDeMedida;

    // Conversión Entity -> DTO
    private UnidadDeMedidaDTO toDTO(UnidadDeMedida entity) {
        return new UnidadDeMedidaDTO(entity.getIdunidadmedida(), entity.getNombre());
    }

    // Conversión DTO -> Entity
    private UnidadDeMedida toEntity(UnidadDeMedidaDTO dto) {
        UnidadDeMedida entity = new UnidadDeMedida();
        entity.setIdunidadmedida(dto.getIdunidadmedida());
        entity.setNombre(dto.getNombre());
        return entity;
    }

    // Listar todas las unidades de medida
    @GetMapping
    public List<UnidadDeMedidaDTO> listarTodos() {
        return serviceUnidadDeMedida.buscarTodos()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // Buscar unidad de medida por ID
    @GetMapping("/{id}")
    public ResponseEntity<UnidadDeMedidaDTO> buscarPorId(@PathVariable("id") Integer id) {
        Optional<UnidadDeMedida> unidad = serviceUnidadDeMedida.buscarId(id);
        return unidad.map(value -> ResponseEntity.ok(toDTO(value)))
                     .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Crear nueva unidad de medida
    @PostMapping
    public ResponseEntity<?> guardar(@RequestBody UnidadDeMedidaDTO dto) {
    try {
        UnidadDeMedida guardado = serviceUnidadDeMedida.guardar(toEntity(dto));
        return ResponseEntity.ok(toDTO(guardado));
    } catch (IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}

    // Modificar unidad de medida existente
    @PutMapping
    public ResponseEntity<?> modificar(@RequestBody UnidadDeMedidaDTO dto) {
    try {
        Optional<UnidadDeMedida> existente = serviceUnidadDeMedida.buscarId(dto.getIdunidadmedida());

        if (existente.isPresent()) {
            UnidadDeMedida actualizado = serviceUnidadDeMedida.modificar(toEntity(dto));
            return ResponseEntity.ok(toDTO(actualizado));
        } else {
            return ResponseEntity.notFound().build();
        }
    } catch (IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}

    // Eliminar (lógico) por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Integer id) {
        Optional<UnidadDeMedida> unidad = serviceUnidadDeMedida.buscarId(id);
        if (unidad.isPresent()) {
            serviceUnidadDeMedida.eliminar(id);
            return ResponseEntity.ok("Unidad de medida eliminada correctamente");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
