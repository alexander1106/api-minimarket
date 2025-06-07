package com.gadbacorp.api.controller.inventario;

import java.util.List;
import java.util.Optional;

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

import com.gadbacorp.api.entity.inventario.InventarioProductosDTO;
import com.gadbacorp.api.service.inventario.IInventarioProductoService;

@RestController
@RequestMapping("/api/minimarket/inventarioproducto")
public class InventarioProductoController {

    @Autowired
    private IInventarioProductoService serviceInvProd;

    @GetMapping
    public ResponseEntity<List<InventarioProductosDTO>> listarTodos() {
        List<InventarioProductosDTO> lista = serviceInvProd.listarTodosDTO();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventarioProductosDTO> obtenerPorId(@PathVariable Integer id) {
        Optional<InventarioProductosDTO> opt = serviceInvProd.buscarPorIdDTO(id);
        return opt.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody InventarioProductosDTO dto) {
        try {
            InventarioProductosDTO creado = serviceInvProd.crearDTO(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(creado);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error al crear registro de InventarioProducto");
        }
    }

    @PutMapping
    public ResponseEntity<?> actualizar(@RequestBody InventarioProductosDTO dto) {
    try {
        Integer id = dto.getIdinventarioproducto();
        if (id == null) {
            return ResponseEntity
                .badRequest()
                .body("El campo 'idinventarioproducto' es requerido en el body");
        }
        InventarioProductosDTO actualizado = serviceInvProd.actualizarDTO(id, dto);
                return ResponseEntity.ok(actualizado);
            } catch (ResponseStatusException e) {
                return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .body("Error al actualizar registro de InventarioProducto");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Integer id) {
        try {
            serviceInvProd.eliminar(id);
            return ResponseEntity.ok("InventarioProducto eliminado correctamente");
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error al eliminar InventarioProducto");
        }
    }
}