package com.gadbacorp.api.controller.compras;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.gadbacorp.api.entity.compras.DetallesCompras;
import com.gadbacorp.api.entity.compras.DetallesComprasDTO;
import com.gadbacorp.api.entity.compras.Compras;
import com.gadbacorp.api.entity.inventario.Productos;
import com.gadbacorp.api.service.compras.IDetallesComprasService;
import com.gadbacorp.api.service.compras.IComprasService;
import com.gadbacorp.api.service.inventario.IProductosService;

@RestController
@RequestMapping("/api/minimarket/detalles-compras")
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class DetallesComprasController {

    @Autowired
    private IDetallesComprasService detallesComprasService;
    
    @Autowired
    private IComprasService comprasService;
    
    @Autowired
    private IProductosService productosService;

    @GetMapping
    public ResponseEntity<List<DetallesCompras>> listarTodosDetalles() {
        List<DetallesCompras> detalles = detallesComprasService.listar();
        return new ResponseEntity<>(detalles, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> crearDetalle(@RequestBody DetallesComprasDTO detalleDTO) {
        try {
            // Validar campos obligatorios
            if(detalleDTO.getIdCompra() == null || detalleDTO.getIdProducto() == null || 
               detalleDTO.getCantidad() == null || detalleDTO.getPrecioUnitario() == null) {
                return ResponseEntity.badRequest().body("Compra, producto, cantidad y precio unitario son obligatorios");
            }
            
            // Validar que existe la compra
            Optional<Compras> compra = comprasService.buscarId(detalleDTO.getIdCompra());
            if(compra.isEmpty()) {
                return ResponseEntity.badRequest().body("Compra no encontrada");
            }
            
            // Validar que existe el producto
            Optional<Productos> producto = productosService.buscarId(detalleDTO.getIdProducto());
            if(producto.isEmpty()) {
                return ResponseEntity.badRequest().body("Producto no encontrado");
            }
            
            // Validar cantidad positiva
            if(detalleDTO.getCantidad() <= 0) {
                return ResponseEntity.badRequest().body("La cantidad debe ser mayor a cero");
            }
            
            // Validar precio positivo
            if(detalleDTO.getPrecioUnitario().compareTo(BigDecimal.ZERO) <= 0) {
                return ResponseEntity.badRequest().body("El precio unitario debe ser mayor a cero");
            }
            
            // Calcular subtotal si no viene
            if(detalleDTO.getSubtotal() == null) {
                detalleDTO.setSubtotal(detalleDTO.getPrecioUnitario().multiply(new BigDecimal(detalleDTO.getCantidad())));
            }
            
            // Establecer estado por defecto si no viene
            if(detalleDTO.getEstado() == null) {
                detalleDTO.setEstado(1);
            }
            
            // Mapear DTO a entidad
            DetallesCompras detalle = new DetallesCompras();
            detalle.setCompra(compra.get());
            detalle.setProducto(producto.get());
            detalle.setCantidad(detalleDTO.getCantidad());
            detalle.setPrecioUnitario(detalleDTO.getPrecioUnitario());
            detalle.setSubtotal(detalleDTO.getSubtotal());
            detalle.setEstado(detalleDTO.getEstado());
            
            // Guardar detalle
            DetallesCompras detalleGuardado = detallesComprasService.guardar(detalle);
            return ResponseEntity.status(HttpStatus.CREATED).body(detalleGuardado);
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("Error al crear detalle de compra: " + e.getMessage());
        }
    }

    @PutMapping
    public ResponseEntity<?> actualizarDetalle(@RequestBody DetallesComprasDTO detalleDTO) {
        try {
            // Validar que se envió el ID del detalle
            if(detalleDTO.getIdDetalleCompra() == null) {
                return ResponseEntity.badRequest().body("El ID del detalle es requerido");
            }
            
            // Validar que existe el detalle
            Optional<DetallesCompras> detalleExistente = detallesComprasService.obtenerPorId(detalleDTO.getIdDetalleCompra());
            if(detalleExistente.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            // Validar compra si se envió
            if(detalleDTO.getIdCompra() != null) {
                Optional<Compras> compra = comprasService.buscarId(detalleDTO.getIdCompra());
                if(compra.isEmpty()) {
                    return ResponseEntity.badRequest().body("Compra no encontrada");
                }
            }
            
            // Validar producto si se envió
            if(detalleDTO.getIdProducto() != null) {
                Optional<Productos> producto = productosService.buscarId(detalleDTO.getIdProducto());
                if(producto.isEmpty()) {
                    return ResponseEntity.badRequest().body("Producto no encontrado");
                }
            }
            
            // Validar cantidad positiva si se envió
            if(detalleDTO.getCantidad() != null && detalleDTO.getCantidad() <= 0) {
                return ResponseEntity.badRequest().body("La cantidad debe ser mayor a cero");
            }
            
            // Validar precio positivo si se envió
            if(detalleDTO.getPrecioUnitario() != null && 
               detalleDTO.getPrecioUnitario().compareTo(BigDecimal.ZERO) <= 0) {
                return ResponseEntity.badRequest().body("El precio unitario debe ser mayor a cero");
            }
            
            // Actualizar entidad
            DetallesCompras detalle = detalleExistente.get();
            
            if(detalleDTO.getIdCompra() != null) {
                detalle.setCompra(comprasService.buscarId(detalleDTO.getIdCompra()).get());
            }
            
            if(detalleDTO.getIdProducto() != null) {
                detalle.setProducto(productosService.buscarId(detalleDTO.getIdProducto()).get());
            }
            
            if(detalleDTO.getCantidad() != null) {
                detalle.setCantidad(detalleDTO.getCantidad());
            }
            
            if(detalleDTO.getPrecioUnitario() != null) {
                detalle.setPrecioUnitario(detalleDTO.getPrecioUnitario());
            }
            
            // Recalcular subtotal si cambió cantidad o precio
            if(detalleDTO.getCantidad() != null || detalleDTO.getPrecioUnitario() != null) {
                detalle.setSubtotal(detalle.getPrecioUnitario().multiply(new BigDecimal(detalle.getCantidad())));
            } else if(detalleDTO.getSubtotal() != null) {
                detalle.setSubtotal(detalleDTO.getSubtotal());
            }
            
            if(detalleDTO.getEstado() != null) {
                detalle.setEstado(detalleDTO.getEstado());
            }
            
            // Guardar cambios
            DetallesCompras detalleActualizado = detallesComprasService.guardar(detalle);
            return ResponseEntity.ok(detalleActualizado);
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("Error al actualizar detalle de compra: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarDetalle(@PathVariable Integer id) {
        try {
            Optional<DetallesCompras> detalle = detallesComprasService.obtenerPorId(id);
            if(detalle.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            detallesComprasService.eliminar(id);
            return ResponseEntity.ok("Detalle de compra eliminado correctamente");
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("Error al eliminar detalle de compra: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerDetallePorId(@PathVariable Integer id) {
        try {
            Optional<DetallesCompras> detalle = detallesComprasService.obtenerPorId(id);
            return detalle.map(ResponseEntity::ok)
                        .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("Error al obtener detalle de compra: " + e.getMessage());
        }
    }
    
    @GetMapping("/compra/{idCompra}")
    public ResponseEntity<?> obtenerDetallesPorCompra(@PathVariable Integer idCompra) {
        try {
            // Aquí necesitarías implementar un método en el servicio para buscar por idCompra
            List<DetallesCompras> detalles = detallesComprasService.listarPorCompra(idCompra);
            return ResponseEntity.ok(detalles);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("Error al obtener detalles de compra: " + e.getMessage());
        }
    }
}