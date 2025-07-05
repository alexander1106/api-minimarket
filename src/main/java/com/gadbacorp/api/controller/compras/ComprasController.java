package com.gadbacorp.api.controller.compras;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.gadbacorp.api.entity.compras.Compras;
import com.gadbacorp.api.entity.compras.ComprasDTO;
import com.gadbacorp.api.entity.compras.Proveedores;
import com.gadbacorp.api.entity.ventas.MetodosPago;
import com.gadbacorp.api.service.compras.IComprasService;
import com.gadbacorp.api.service.compras.IProveedoresService;
import com.gadbacorp.api.service.ventas.IMetodosPagoService;

import jakarta.validation.Valid;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
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

    // Listar todas las compras activas
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

    // Obtener una compra por ID
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
            
            return ResponseEntity.ok(compra.get());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("Error al obtener compra: " + e.getMessage());
        }
    }

    // Obtener compras por proveedor
    @GetMapping("/proveedor/{proveedorId}")
    public ResponseEntity<?> obtenerComprasPorProveedor(@PathVariable Integer proveedorId) {
        try {
            if (proveedorId == null || proveedorId <= 0) {
                return ResponseEntity.badRequest().body("ID de proveedor inválido");
            }
            
            // Verificar si el proveedor existe
            Optional<Proveedores> proveedor = proveedoresService.buscarId(proveedorId);
            if (proveedor.isEmpty() || proveedor.get().getEstado() == 0) {
                return ResponseEntity.badRequest().body("Proveedor no encontrado o inactivo");
            }
            
            List<Compras> compras = comprasService.obtenerComprasPorProveedor(proveedorId);
            if (compras.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body("No hay compras registradas para este proveedor");
            }
            
            return ResponseEntity.ok(compras);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("Error al obtener compras por proveedor: " + e.getMessage());
        }
    }

    // Crear una nueva compra
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
            
            if (compraDTO.getTotal() == null || compraDTO.getTotal().compareTo(BigDecimal.ZERO) <= 0) {
                return ResponseEntity.badRequest().body("El total debe ser mayor a cero");
            }
            
            if (compraDTO.getPrecioCompra() == null || compraDTO.getPrecioCompra().compareTo(BigDecimal.ZERO) <= 0) {
                return ResponseEntity.badRequest().body("El precio de compra debe ser mayor a cero");
            }
            
            if (compraDTO.getPrecioVenta() == null || compraDTO.getPrecioVenta().compareTo(BigDecimal.ZERO) <= 0) {
                return ResponseEntity.badRequest().body("El precio de venta debe ser mayor a cero");
            }
            
            if (compraDTO.getFechaCompra() == null) {
                return ResponseEntity.badRequest().body("La fecha de compra es obligatoria");
            }
            
            // Validar que la fecha no sea futura
            if (compraDTO.getFechaCompra().isAfter(LocalDateTime.now())) {
                return ResponseEntity.badRequest().body("La fecha de compra no puede ser futura");
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
            
            // Mapear DTO a entidad
            Compras compra = new Compras();
            compra.setProveedor(proveedor.get());
            compra.setMetodoPago(metodoPago.get());
            compra.setTotal(compraDTO.getTotal());
            compra.setPrecioCompra(compraDTO.getPrecioCompra());
            compra.setPrecioVenta(compraDTO.getPrecioVenta());
            compra.setDescripcion(compraDTO.getDescripcion());
            compra.setFechaCompra(compraDTO.getFechaCompra());
            compra.setEstado(1); // Estado activo por defecto
            
            // Guardar compra
            Compras nuevaCompra = comprasService.guardarCompra(compra);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaCompra);
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("Error al crear compra: " + e.getMessage());
        }
    }

    // Actualizar una compra existente
    @PutMapping
    public ResponseEntity<?> actualizarCompra(@Valid @RequestBody ComprasDTO compraDTO) {
        try {
            // Validar que el ID de la compra viene en el cuerpo
            if (compraDTO.getIdCompra() == null || compraDTO.getIdCompra() <= 0) {
                return ResponseEntity.badRequest().body("El ID de la compra es obligatorio y debe ser válido");
            }
            
            // Validar que existe la compra
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
            
            // Validar total
            if (compraDTO.getTotal() == null || compraDTO.getTotal().compareTo(BigDecimal.ZERO) <= 0) {
                return ResponseEntity.badRequest().body("El total debe ser mayor a cero");
            }
            
            // Validar precios
            if (compraDTO.getPrecioCompra() == null || compraDTO.getPrecioCompra().compareTo(BigDecimal.ZERO) <= 0) {
                return ResponseEntity.badRequest().body("El precio de compra debe ser mayor a cero");
            }
            
            if (compraDTO.getPrecioVenta() == null || compraDTO.getPrecioVenta().compareTo(BigDecimal.ZERO) <= 0) {
                return ResponseEntity.badRequest().body("El precio de venta debe ser mayor a cero");
            }
            
            // Validar fecha
            if (compraDTO.getFechaCompra() == null) {
                return ResponseEntity.badRequest().body("La fecha de compra es obligatoria");
            }
            
            if (compraDTO.getFechaCompra().isAfter(LocalDateTime.now())) {
                return ResponseEntity.badRequest().body("La fecha de compra no puede ser futura");
            }
            
            // Actualizar entidad
            Compras compra = compraExistente.get();
            compra.setProveedor(proveedor.get());
            compra.setMetodoPago(metodoPago.get());
            compra.setTotal(compraDTO.getTotal());
            compra.setPrecioCompra(compraDTO.getPrecioCompra());
            compra.setPrecioVenta(compraDTO.getPrecioVenta());
            compra.setDescripcion(compraDTO.getDescripcion());
            compra.setFechaCompra(compraDTO.getFechaCompra());
            compra.setEstado(compraDTO.getEstado() != null ? compraDTO.getEstado() : 1);
            
            // Guardar cambios
            Compras compraActualizada = comprasService.editarCompra(compra);
            return ResponseEntity.ok(compraActualizada);
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("Error al actualizar compra: " + e.getMessage());
        }
    }

    // Eliminar (lógicamente) una compra
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