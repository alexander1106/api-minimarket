package com.gadbacorp.api.controller.inventario;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
import org.springframework.web.server.ResponseStatusException;

import com.gadbacorp.api.entity.inventario.Almacenes;
import com.gadbacorp.api.entity.inventario.Inventario;
import com.gadbacorp.api.entity.inventario.InventarioDTO;
import com.gadbacorp.api.service.inventario.IInventarioService;

@RestController
@RequestMapping("/api/minimarket/inventario")
public class InventarioController {

    @Autowired
    private IInventarioService serviceInventario;

    private InventarioDTO toDTO(Inventario inventario) {
        InventarioDTO dto = new InventarioDTO();
        dto.setIdinventario(inventario.getIdinventario());
        dto.setIdalmacen(inventario.getAlmacen().getIdalmacen());
        dto.setNombre(inventario.getNombre());
        dto.setDescripcion(inventario.getDescripcion());
        return dto;
    }

    private Inventario toEntity(InventarioDTO dto) {
        Inventario inventario = new Inventario();
        if (dto.getIdinventario() != null) {
            inventario.setIdinventario(dto.getIdinventario());
        }
        inventario.setNombre(dto.getNombre());
        inventario.setDescripcion(dto.getDescripcion());
            Almacenes almacen =
            new Almacenes();
        almacen.setIdalmacen(dto.getIdalmacen());
        inventario.setAlmacen(almacen);
        return inventario;
    }

    @GetMapping
    public ResponseEntity<List<InventarioDTO>> listarTodos() {
        List<InventarioDTO> lista = serviceInventario.buscarTodos().stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventarioDTO> obtenerPorId(@PathVariable Integer id) {
        Optional<Inventario> opt = serviceInventario.buscarId(id);
        return opt.map(this::toDTO)
                  .map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody InventarioDTO dto) {
        try {
            Inventario entidad = toEntity(dto);
            Inventario creado = serviceInventario.guardar(entidad);
            return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(creado));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error al crear inventario");
        }
    }

    @PutMapping
    public ResponseEntity<?> actualizar(@RequestBody InventarioDTO dto) {
        try {
            // Aquí el id ya viene en el dto
            Inventario entidad = toEntity(dto);
            Inventario modificado = serviceInventario.modificar(entidad);
            return ResponseEntity.ok(toDTO(modificado));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("Error al actualizar inventario");
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Integer id) {
        try {
            serviceInventario.eliminar(id);
            return ResponseEntity.ok("Inventario eliminado correctamente");
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error al eliminar inventario");
        }
    }
}
