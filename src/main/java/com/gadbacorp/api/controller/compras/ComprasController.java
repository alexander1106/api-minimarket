package com.gadbacorp.api.controller.compras;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.gadbacorp.api.entity.compras.*;
import com.gadbacorp.api.entity.ventas.MetodosPago;
import com.gadbacorp.api.service.compras.*;
import com.gadbacorp.api.service.ventas.IMetodosPagoService;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/minimarket/compras")
@Validated
public class ComprasController {

    @Autowired
    private IComprasService comprasService;
    
    @Autowired
    private IProveedoresService proveedoresService;
    
    @Autowired
    private IMetodosPagoService metodosPagoService;

    @Autowired
    private IDetallesComprasService detalleCompraService;

    @GetMapping
    public ResponseEntity<?> listarTodasCompras() {
        try {
            List<Compras> compras = comprasService.listarCompras();
            if (compras.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No hay compras registradas");
            }
            return ResponseEntity.ok(compras);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al listar compras: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerCompraPorId(@PathVariable Integer id) {
        try {
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest().body("ID de compra inválido");
            }
            
            Optional<Compras> compra = comprasService.buscarCompra(id);
            if (compra.isEmpty() || compra.get().getEstado() == 0) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Compra no encontrada o eliminada");
            }
            
            List<DetallesCompras> detalles = detalleCompraService.buscarPorIdCompra(id);
            
            // Respuesta usando HashMap
            Map<String, Object> response = new HashMap<>();
            response.put("compra", compra.get());
            response.put("detalles", detalles);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("Error al obtener compra: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> crearCompra(@Valid @RequestBody ComprasDTO compraDTO) {
        try {
            // Validar campos obligatorios
            if (compraDTO.getIdProveedor() == null) {
                return ResponseEntity.badRequest().body("El proveedor es obligatorio");
            }
            
            if (compraDTO.getIdMetodoPago() == null) {
                return ResponseEntity.badRequest().body("El método de pago es obligatorio");
            }
            
            if (compraDTO.getDetalles() == null || compraDTO.getDetalles().isEmpty()) {
                return ResponseEntity.badRequest().body("Debe incluir al menos un producto");
            }
            
            // Validar proveedor
            Optional<Proveedores> proveedor = proveedoresService.buscarId(compraDTO.getIdProveedor());
            if (proveedor.isEmpty() || proveedor.get().getEstado() == 0) {
                return ResponseEntity.badRequest().body("Proveedor no encontrado o inactivo");
            }
            
            // Validar método de pago
            Optional<MetodosPago> metodoPago = metodosPagoService.obtenerMetodoPago(compraDTO.getIdMetodoPago());
            if (metodoPago.isEmpty() || metodoPago.get().getEstado() == 0) {
                return ResponseEntity.badRequest().body("Método de pago no encontrado o inactivo");
            }
            
            // Validar detalles
            for (DetallesComprasDTO detalle : compraDTO.getDetalles()) {
                if (detalle.getIdProducto() == null) {
                    return ResponseEntity.badRequest().body("Producto inválido en uno de los detalles");
                }
                if (detalle.getCantidad() == null || detalle.getCantidad() <= 0) {
                    return ResponseEntity.badRequest().body("Cantidad inválida en uno de los detalles");
                }
                if (detalle.getPrecioCompra() == null || detalle.getPrecioCompra().compareTo(BigDecimal.ZERO) <= 0) {
                    return ResponseEntity.badRequest().body("Precio de compra inválido en uno de los detalles");
                }
                if (detalle.getPrecioVenta() == null || detalle.getPrecioVenta().compareTo(BigDecimal.ZERO) <= 0) {
                    return ResponseEntity.badRequest().body("Precio de venta inválido en uno de los detalles");
                }
            }
            
            // Mapear DTO a entidad
            Compras compra = new Compras();
            compra.setProveedor(proveedor.get());
            compra.setMetodoPago(metodoPago.get());
            compra.setFechaCompra(LocalDateTime.now());
            compra.setDescripcion(compraDTO.getDescripcion());
            compra.setEstado(1);
            
            // Calcular total
            BigDecimal total = compraDTO.getDetalles().stream()
                .map(d -> d.getPrecioCompra().multiply(BigDecimal.valueOf(d.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            compra.setTotal(total);
            
            Compras compraGuardada = comprasService.guardarCompra(compra);
            
            // Guardar detalles
            for (DetallesComprasDTO detalleDTO : compraDTO.getDetalles()) {
                DetallesCompras detalle = new DetallesCompras();
                detalle.setCompra(compraGuardada);
                detalle.setCantidad(detalleDTO.getCantidad());
                detalle.setPrecioUnitario(detalleDTO.getPrecioCompra());
                detalle.setPrecioCompra(detalleDTO.getPrecioCompra());
                detalle.setPrecioVenta(detalleDTO.getPrecioVenta());
                detalle.setSubTotal(detalleDTO.getPrecioCompra().multiply(BigDecimal.valueOf(detalleDTO.getCantidad())));
                detalleCompraService.guardarDetalle(detalle, compraGuardada.getIdCompra(), detalleDTO.getIdProducto());
            }
            
            return ResponseEntity.status(HttpStatus.CREATED).body(compraGuardada);
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("Error al crear compra: " + e.getMessage());
        }
    }

    @PutMapping
    public ResponseEntity<?> actualizarCompra(@Valid @RequestBody ComprasDTO compraDTO) {
        try {
            if (compraDTO.getIdCompra() == null || compraDTO.getIdCompra() <= 0) {
                return ResponseEntity.badRequest().body("ID de compra inválido");
            }
            
            Optional<Compras> compraExistente = comprasService.buscarCompra(compraDTO.getIdCompra());
            if (compraExistente.isEmpty() || compraExistente.get().getEstado() == 0) {
                return ResponseEntity.notFound().build();
            }
            
            // Validar proveedor
            Optional<Proveedores> proveedor = proveedoresService.buscarId(compraDTO.getIdProveedor());
            if (proveedor.isEmpty() || proveedor.get().getEstado() == 0) {
                return ResponseEntity.badRequest().body("Proveedor no encontrado o inactivo");
            }
            
            // Validar método de pago
            Optional<MetodosPago> metodoPago = metodosPagoService.obtenerMetodoPago(compraDTO.getIdMetodoPago());
            if (metodoPago.isEmpty() || metodoPago.get().getEstado() == 0) {
                return ResponseEntity.badRequest().body("Método de pago no encontrado o inactivo");
            }
            
            // Validar detalles
            for (DetallesComprasDTO detalle : compraDTO.getDetalles()) {
                if (detalle.getIdProducto() == null) {
                    return ResponseEntity.badRequest().body("Producto inválido en uno de los detalles");
                }
                if (detalle.getCantidad() == null || detalle.getCantidad() <= 0) {
                    return ResponseEntity.badRequest().body("Cantidad inválida en uno de los detalles");
                }
                if (detalle.getPrecioCompra() == null || detalle.getPrecioCompra().compareTo(BigDecimal.ZERO) <= 0) {
                    return ResponseEntity.badRequest().body("Precio de compra inválido en uno de los detalles");
                }
                if (detalle.getPrecioVenta() == null || detalle.getPrecioVenta().compareTo(BigDecimal.ZERO) <= 0) {
                    return ResponseEntity.badRequest().body("Precio de venta inválido en uno de los detalles");
                }
            }
            
            // Actualizar compra
            Compras compra = compraExistente.get();
            compra.setProveedor(proveedor.get());
            compra.setMetodoPago(metodoPago.get());
            compra.setDescripcion(compraDTO.getDescripcion());
            
            // Calcular nuevo total
            BigDecimal total = compraDTO.getDetalles().stream()
                .map(d -> d.getPrecioCompra().multiply(BigDecimal.valueOf(d.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            compra.setTotal(total);
            
            Compras compraActualizada = comprasService.editarCompra(compra);
            
            // Eliminar detalles antiguos (lógica mejorada)
            List<DetallesCompras> detallesExistentes = detalleCompraService.buscarPorIdCompra(compra.getIdCompra());
            for (DetallesCompras detalle : detallesExistentes) {
                detalleCompraService.eliminarDetalle(detalle.getIdDetalleCompra());
            }
            
            // Crear nuevos detalles
            for (DetallesComprasDTO detalleDTO : compraDTO.getDetalles()) {
                DetallesCompras detalle = new DetallesCompras();
                detalle.setCompra(compraActualizada);
                detalle.setCantidad(detalleDTO.getCantidad());
                detalle.setPrecioUnitario(detalleDTO.getPrecioCompra());
                detalle.setPrecioCompra(detalleDTO.getPrecioCompra());
                detalle.setPrecioVenta(detalleDTO.getPrecioVenta());
                detalle.setSubTotal(detalleDTO.getPrecioCompra().multiply(BigDecimal.valueOf(detalleDTO.getCantidad())));
                detalleCompraService.guardarDetalle(detalle, compraActualizada.getIdCompra(), detalleDTO.getIdProducto());
            }
            
            return ResponseEntity.ok(compraActualizada);
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("Error al actualizar compra: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarCompra(@PathVariable Integer id) {
        try {
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest().body("ID de compra inválido");
            }
            
            Optional<Compras> compra = comprasService.buscarCompra(id);
            if (compra.isEmpty() || compra.get().getEstado() == 0) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Compra no encontrada o ya eliminada");
            }
            
            comprasService.eliminarCompra(id);
            return ResponseEntity.ok("Compra eliminada correctamente");
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("Error al eliminar compra: " + e.getMessage());
        }
    }
}