package com.gadbacorp.api.controller.compras;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gadbacorp.api.entity.compras.DetallesCompras;
import com.gadbacorp.api.entity.compras.DetallesComprasDTO;
import com.gadbacorp.api.service.compras.IDetallesComprasService;

import jakarta.validation.Valid;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/minimarket/detalles-compras")
@Validated
public class DetallesComprasController {

    @Autowired
    private IDetallesComprasService detallesComprasService;

    @PostMapping
    public ResponseEntity<?> crearDetalleCompra(@Valid @RequestBody DetallesComprasDTO detalleDTO) {
        try {
            if (detalleDTO.getIdCompra() == null || detalleDTO.getIdProducto() == null) {
                return ResponseEntity.badRequest().body("ID de compra y producto son obligatorios");
            }
            
            if (detalleDTO.getCantidad() == null || detalleDTO.getCantidad() <= 0) {
                return ResponseEntity.badRequest().body("La cantidad debe ser mayor a cero");
            }
            
            if (detalleDTO.getPrecioUnitario() == null || detalleDTO.getPrecioUnitario().compareTo(BigDecimal.ZERO) <= 0) {
                return ResponseEntity.badRequest().body("El precio unitario debe ser mayor a cero");
            }
            
            DetallesCompras detalle = new DetallesCompras();
            detalle.setCantidad(detalleDTO.getCantidad());
            detalle.setPrecioUnitario(detalleDTO.getPrecioUnitario());
            
            // Calcular subtotal si no viene en el DTO
            if (detalleDTO.getSubTotal() == null) {
                BigDecimal subTotal = detalleDTO.getPrecioUnitario().multiply(new BigDecimal(detalleDTO.getCantidad()));
                detalle.setSubTotal(subTotal);
            } else {
                detalle.setSubTotal(detalleDTO.getSubTotal());
            }
            
            detalle.setEstado(detalleDTO.getEstado() != null ? detalleDTO.getEstado() : 1);
            
            DetallesCompras nuevoDetalle = detallesComprasService.guardarDetalle(
                detalle, 
                detalleDTO.getIdCompra(), 
                detalleDTO.getIdProducto()
            );
            
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoDetalle);
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("Error al crear detalle de compra: " + e.getMessage());
        }
    }

    @PutMapping
    public ResponseEntity<?> actualizarDetalleCompra(@Valid @RequestBody DetallesComprasDTO detalleDTO) {
        try {
            if (detalleDTO.getIdDetalleCompra() == null || detalleDTO.getIdDetalleCompra() <= 0) {
                return ResponseEntity.badRequest().body("El ID del detalle es obligatorio y debe ser válido");
            }
            
            if (detalleDTO.getCantidad() == null || detalleDTO.getCantidad() <= 0) {
                return ResponseEntity.badRequest().body("La cantidad debe ser mayor a cero");
            }
            
            if (detalleDTO.getPrecioUnitario() == null || detalleDTO.getPrecioUnitario().compareTo(BigDecimal.ZERO) <= 0) {
                return ResponseEntity.badRequest().body("El precio unitario debe ser mayor a cero");
            }
            
            DetallesCompras detalle = new DetallesCompras();
            detalle.setIdDetalleCompra(detalleDTO.getIdDetalleCompra());
            detalle.setCantidad(detalleDTO.getCantidad());
            detalle.setPrecioUnitario(detalleDTO.getPrecioUnitario());
            
            if (detalleDTO.getSubTotal() == null) {
                BigDecimal subTotal = detalleDTO.getPrecioUnitario().multiply(new BigDecimal(detalleDTO.getCantidad()));
                detalle.setSubTotal(subTotal);
            } else {
                detalle.setSubTotal(detalleDTO.getSubTotal());
            }
            
            detalle.setEstado(detalleDTO.getEstado() != null ? detalleDTO.getEstado() : 1);
            
            DetallesCompras detalleActualizado = detallesComprasService.actualizarDetalle(detalle);
            return ResponseEntity.ok(detalleActualizado);
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("Error al actualizar detalle de compra: " + e.getMessage());
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarDetalleCompra(@PathVariable Integer id) {
        try {
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest().body("ID de detalle inválido");
            }
            
            detallesComprasService.eliminarDetalle(id);
            return ResponseEntity.ok("Detalle de compra eliminado correctamente");
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("Error al eliminar detalle de compra: " + e.getMessage());
        }
    }

    @GetMapping("/compra/{idCompra}")
    public ResponseEntity<?> obtenerDetallesPorCompra(@PathVariable Integer idCompra) {
        try {
            if (idCompra == null || idCompra <= 0) {
                return ResponseEntity.badRequest().body("ID de compra inválido");
            }
            
            List<DetallesCompras> detalles = detallesComprasService.buscarPorIdCompra(idCompra);
            if (detalles.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body("No hay detalles registrados para esta compra");
            }
            
            return ResponseEntity.ok(detalles);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("Error al obtener detalles de compra: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerDetallePorId(@PathVariable Integer id) {
        try {
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest().body("ID de detalle inválido");
            }
            
            Optional<DetallesCompras> detalle = detallesComprasService.buscarDetallePorId(id);
            if (detalle.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Detalle no encontrado o eliminado");
            }
            
            return ResponseEntity.ok(detalle.get());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("Error al obtener detalle: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> listarTodosDetalles() {
        try {
            List<DetallesCompras> detalles = detallesComprasService.listarTodosDetalles();
            if (detalles.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body("No hay detalles de compras registrados");
            }
            return ResponseEntity.ok(detalles);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("Error al listar detalles de compras: " + e.getMessage());
        }
    }
}