package com.gadbacorp.api.controller.compras;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.gadbacorp.api.entity.compras.DevolucionesCompra;
import com.gadbacorp.api.entity.compras.DevolucionesCompraDTO;
import com.gadbacorp.api.service.compras.IDevolucionesCompraService;

@RestController
@RequestMapping("/api/minimarket/devoluciones-compra")
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class DevolucionesCompraController {

    @Autowired
    private IDevolucionesCompraService devolucionesCompraService;

    // GET - Obtener todas las devoluciones
    @GetMapping
    public ResponseEntity<List<DevolucionesCompra>> obtenerTodasDevoluciones() {
        List<DevolucionesCompra> devoluciones = devolucionesCompraService.findAll();
        return new ResponseEntity<>(devoluciones, HttpStatus.OK);
    }

    // GET - Obtener una devolución por ID
    @GetMapping("/{id}")
    public ResponseEntity<DevolucionesCompra> obtenerDevolucionPorId(@PathVariable Integer id) {
        Optional<DevolucionesCompra> devolucion = devolucionesCompraService.findById(id);
        return devolucion.map(ResponseEntity::ok)
                        .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // POST - Crear una nueva devolución
    @PostMapping
    public ResponseEntity<?> crearDevolucion(@RequestBody DevolucionesCompraDTO devolucionDTO) {
        try {
            // Validaciones básicas
            if(devolucionDTO.getIdCompra() == null || devolucionDTO.getIdProducto() == null || 
               devolucionDTO.getIdMetodoPago() == null) {
                return ResponseEntity.badRequest().body("Los campos idCompra, idProducto e idMetodoPago son obligatorios");
            }

            if(devolucionDTO.getCantidadDevuelta() == null || devolucionDTO.getCantidadDevuelta() <= 0) {
                return ResponseEntity.badRequest().body("La cantidad devuelta debe ser mayor a cero");
            }

            if(devolucionDTO.getMotivo() == null || devolucionDTO.getMotivo().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("El motivo es obligatorio");
            }

            DevolucionesCompra nuevaDevolucion = devolucionesCompraService.save(devolucionDTO);
            
            if(nuevaDevolucion == null) {
                return ResponseEntity.badRequest().body("No se pudo crear la devolución. Verifique los IDs proporcionados");
            }

            return new ResponseEntity<>(nuevaDevolucion, HttpStatus.CREATED);
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error al crear la devolución: " + e.getMessage());
        }
    }

    // PUT - Actualizar una devolución existente
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarDevolucion(
            @PathVariable Integer id, 
            @RequestBody DevolucionesCompraDTO devolucionDTO) {
        try {
            // Validar que el ID de la URL coincida con el del cuerpo si está presente
            if(devolucionDTO.getIdDevolucion() != null && !devolucionDTO.getIdDevolucion().equals(id)) {
                return ResponseEntity.badRequest().body("El ID de la devolución no coincide");
            }

            // Validaciones básicas
            if(devolucionDTO.getIdCompra() == null || devolucionDTO.getIdProducto() == null || 
               devolucionDTO.getIdMetodoPago() == null) {
                return ResponseEntity.badRequest().body("Los campos idCompra, idProducto e idMetodoPago son obligatorios");
            }

            if(devolucionDTO.getCantidadDevuelta() == null || devolucionDTO.getCantidadDevuelta() <= 0) {
                return ResponseEntity.badRequest().body("La cantidad devuelta debe ser mayor a cero");
            }

            if(devolucionDTO.getMotivo() == null || devolucionDTO.getMotivo().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("El motivo es obligatorio");
            }

            DevolucionesCompra devolucionActualizada = devolucionesCompraService.update(id, devolucionDTO);
            
            if(devolucionActualizada == null) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(devolucionActualizada);
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error al actualizar la devolución: " + e.getMessage());
        }
    }

    // DELETE - Eliminar una devolución (soft delete)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarDevolucion(@PathVariable Integer id) {
        try {
            Optional<DevolucionesCompra> devolucionExistente = devolucionesCompraService.findById(id);
            
            if(devolucionExistente.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            devolucionesCompraService.delete(id);
            return ResponseEntity.ok("Devolución eliminada correctamente");
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error al eliminar la devolución: " + e.getMessage());
        }
    }
}