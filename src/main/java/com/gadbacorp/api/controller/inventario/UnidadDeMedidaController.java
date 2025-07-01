package com.gadbacorp.api.controller.inventario;

import java.util.Collections;
import java.util.List;
import java.util.Map;
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

import com.gadbacorp.api.entity.inventario.UnidadDeMedida;
import com.gadbacorp.api.entity.inventario.UnidadDeMedidaDTO;
import com.gadbacorp.api.service.inventario.IUnidadDeMedidaService;

@RestController
@RequestMapping("/api/minimarket/unidad_medida")
@CrossOrigin(origins = "http://localhost:4200")
public class UnidadDeMedidaController {

    @Autowired
    private IUnidadDeMedidaService service;

    private UnidadDeMedidaDTO toDTO(UnidadDeMedida u) {
        return new UnidadDeMedidaDTO(u.getIdunidadmedida(), u.getNombre());
    }

    private UnidadDeMedida toEntity(UnidadDeMedidaDTO dto) {
        UnidadDeMedida u = new UnidadDeMedida();
        u.setIdunidadmedida(dto.getIdunidadmedida());
        u.setNombre(dto.getNombre());
        return u;
    }

    /** Listar todas las unidades */
    @GetMapping
    public ResponseEntity<List<UnidadDeMedidaDTO>> listar() {
        List<UnidadDeMedidaDTO> dtos = service.buscarTodos()
            .stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /** Buscar una unidad por ID */
    @GetMapping("/{id}")
    public ResponseEntity<UnidadDeMedidaDTO> buscar(@PathVariable Integer id) {
        return service.buscarId(id)
            .map(u -> ResponseEntity.ok(toDTO(u)))
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /** Crear nueva unidad (lanza 409 si ya existe) */
    @PostMapping
    public ResponseEntity<UnidadDeMedidaDTO> crear(@RequestBody UnidadDeMedidaDTO dto) {
        UnidadDeMedida guardada = service.guardar(toEntity(dto));
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(toDTO(guardada));
    }

    /** Actualizar existente (lanza 409 si nombre duplicado) */
    @PutMapping("/unidad_medida")
    public ResponseEntity<UnidadDeMedidaDTO> modificar(@RequestBody UnidadDeMedidaDTO dto) {
        UnidadDeMedida updated = service.modificar(toEntity(dto));
        return ResponseEntity.ok(toDTO(updated));
    }

    /** Eliminar por ID (lanza 400 si está en uso) */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String,String>> eliminar(@PathVariable Integer id) {
        service.eliminar(id);
        return ResponseEntity.ok(
            Collections.singletonMap("message", "Unidad eliminada")
        );
    }
}
