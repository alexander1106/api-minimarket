package com.gadbacorp.api.controller.compras;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.gadbacorp.api.entity.administrable.Sucursales;
import com.gadbacorp.api.entity.compras.*;
import com.gadbacorp.api.entity.ventas.MetodosPago;
import com.gadbacorp.api.service.administrable.ISucursalesService;
import com.gadbacorp.api.service.compras.*;
import com.gadbacorp.api.service.ventas.IMetodosPagoService;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import com.gadbacorp.api.excepciones.OperacionNoPermitidaException;
import com.gadbacorp.api.excepciones.StockInsuficienteException;

import io.swagger.v3.oas.annotations.tags.Tag;
@Tag(name = "Compras - Compras")
@RestController
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

    @Autowired
    private ISucursalesService sucursalesService;

    @Autowired
    private com.gadbacorp.api.service.inventario.IProductosService productosService;

    @Autowired
    private com.gadbacorp.api.service.jpa.inventario.ConversionUnidadesService conversionUnidadesService;

    /** Valida producto, presentación y cantidad (decimales según la unidad) antes de guardar nada. */
    private void validarCantidadDeLinea(DetallesComprasDTO detalle) {
        com.gadbacorp.api.entity.inventario.Productos producto = productosService.buscarId(detalle.getIdProducto())
            .filter(p -> p.getEstado() != null && p.getEstado() == 1)
            .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado o inactivo (id " + detalle.getIdProducto() + ")"));
        conversionUnidadesService.convertir(producto, detalle.getIdPresentacion(), detalle.getCantidad());
    }

    /** Revierte la transaccion (no queda una compra a medias) y responde con el codigo que corresponde al error. */
    private ResponseEntity<?> fallo(Exception e, String contexto) {
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        if (e instanceof OperacionNoPermitidaException || e instanceof StockInsuficienteException) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
        if (e instanceof IllegalArgumentException) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.internalServerError().body(contexto + e.getMessage());
    }

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
    @Transactional
    public ResponseEntity<?> crearCompra(@Valid @RequestBody ComprasDTO compraDTO) {
        try {
            if (compraDTO.getIdProveedor() == null) {
                return ResponseEntity.badRequest().body("El proveedor es obligatorio");
            }
            
            if (compraDTO.getIdMetodoPago() == null) {
                return ResponseEntity.badRequest().body("El método de pago es obligatorio");
            }
            
            if (compraDTO.getDetalles() == null || compraDTO.getDetalles().isEmpty()) {
                return ResponseEntity.badRequest().body("Debe incluir al menos un producto");
            }
            
            Optional<Proveedores> proveedor = proveedoresService.buscarId(compraDTO.getIdProveedor());
            if (proveedor.isEmpty() || proveedor.get().getEstado() == 0) {
                return ResponseEntity.badRequest().body("Proveedor no encontrado o inactivo");
            }

            Optional<Sucursales> sucursal = sucursalesService.buscarId(compraDTO.getIdSucursal());
            if (sucursal.isEmpty()) {
                return ResponseEntity.badRequest().body("Sucursal no encontrada");
            }
            
            Optional<MetodosPago> metodoPago = metodosPagoService.obtenerMetodoPago(compraDTO.getIdMetodoPago());
            if (metodoPago.isEmpty() || metodoPago.get().getEstado() == 0) {
                return ResponseEntity.badRequest().body("Método de pago no encontrado o inactivo");
            }
            
            for (DetallesComprasDTO detalle : compraDTO.getDetalles()) {
                if (detalle.getIdProducto() == null) {
                    return ResponseEntity.badRequest().body("Producto inválido en uno de los detalles");
                }
                if (detalle.getCantidad() == null || detalle.getCantidad().signum() <= 0) {
                    return ResponseEntity.badRequest().body("Cantidad inválida en uno de los detalles");
                }
                if (detalle.getPrecioCompra() == null || detalle.getPrecioCompra().compareTo(BigDecimal.ZERO) <= 0) {
                    return ResponseEntity.badRequest().body("Precio de compra inválido en uno de los detalles");
                }
                if (detalle.getPrecioVenta() == null || detalle.getPrecioVenta().compareTo(BigDecimal.ZERO) <= 0) {
                    return ResponseEntity.badRequest().body("Precio de venta inválido en uno de los detalles");
                }
                validarCantidadDeLinea(detalle);
            }
            
            Compras compra = new Compras();
            compra.setProveedor(proveedor.get());
            compra.setMetodoPago(metodoPago.get());
            compra.setSucursal(sucursal.get());
            compra.setFechaCompra(LocalDateTime.now());
            compra.setDescripcion(compraDTO.getDescripcion());
            compra.setEstado(1);
            
            BigDecimal total = compraDTO.getDetalles().stream()
                .map(d -> d.getPrecioCompra().multiply(d.getCantidad()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            compra.setTotal(total);
            
            Compras compraGuardada = comprasService.guardarCompra(compra);
            
            for (DetallesComprasDTO detalleDTO : compraDTO.getDetalles()) {
                DetallesCompras detalle = new DetallesCompras();
                detalle.setCompra(compraGuardada);
                detalle.setCantidad(detalleDTO.getCantidad());
                detalle.setIdPresentacionSolicitada(detalleDTO.getIdPresentacion());
                detalle.setLote(detalleDTO.getLote());
                detalle.setFechaVencimiento(detalleDTO.getFechaVencimiento());
                detalle.setPrecioUnitario(detalleDTO.getPrecioCompra());
                detalle.setPrecioCompra(detalleDTO.getPrecioCompra());
                detalle.setPrecioVenta(detalleDTO.getPrecioVenta());
                detalle.setSubTotal(detalleDTO.getPrecioCompra().multiply(detalleDTO.getCantidad()));
                detalleCompraService.guardarDetalle(detalle, compraGuardada.getIdCompra(), detalleDTO.getIdProducto());
            }
            
            return ResponseEntity.status(HttpStatus.CREATED).body(compraGuardada);
            
        } catch (Exception e) {
            return fallo(e, "Error al crear compra: ");
        }
    }

    @PutMapping
    @Transactional
    public ResponseEntity<?> actualizarCompra(@Valid @RequestBody ComprasDTO compraDTO) {
        try {
            if (compraDTO.getIdCompra() == null || compraDTO.getIdCompra() <= 0) {
                return ResponseEntity.badRequest().body("ID de compra inválido");
            }
            
            Optional<Compras> compraExistente = comprasService.buscarCompra(compraDTO.getIdCompra());
            if (compraExistente.isEmpty() || compraExistente.get().getEstado() == 0) {
                return ResponseEntity.notFound().build();
            }
            
            Optional<Proveedores> proveedor = proveedoresService.buscarId(compraDTO.getIdProveedor());
            if (proveedor.isEmpty() || proveedor.get().getEstado() == 0) {
                return ResponseEntity.badRequest().body("Proveedor no encontrado o inactivo");
            }
            
            Optional<MetodosPago> metodoPago = metodosPagoService.obtenerMetodoPago(compraDTO.getIdMetodoPago());
            if (metodoPago.isEmpty() || metodoPago.get().getEstado() == 0) {
                return ResponseEntity.badRequest().body("Método de pago no encontrado o inactivo");
            }
            
            for (DetallesComprasDTO detalle : compraDTO.getDetalles()) {
                if (detalle.getIdProducto() == null) {
                    return ResponseEntity.badRequest().body("Producto inválido en uno de los detalles");
                }
                if (detalle.getCantidad() == null || detalle.getCantidad().signum() <= 0) {
                    return ResponseEntity.badRequest().body("Cantidad inválida en uno de los detalles");
                }
                if (detalle.getPrecioCompra() == null || detalle.getPrecioCompra().compareTo(BigDecimal.ZERO) <= 0) {
                    return ResponseEntity.badRequest().body("Precio de compra inválido en uno de los detalles");
                }
                if (detalle.getPrecioVenta() == null || detalle.getPrecioVenta().compareTo(BigDecimal.ZERO) <= 0) {
                    return ResponseEntity.badRequest().body("Precio de venta inválido en uno de los detalles");
                }
                validarCantidadDeLinea(detalle);
            }
            
            Compras compra = compraExistente.get();

            // Quitar las lineas antiguas primero: devuelve su stock a la sucursal original
            for (DetallesCompras detalle : detalleCompraService.buscarPorIdCompra(compra.getIdCompra())) {
                detalleCompraService.eliminarDetalle(detalle.getIdDetalleCompra());
            }

            compra.setProveedor(proveedor.get());
            compra.setMetodoPago(metodoPago.get());
            compra.setDescripcion(compraDTO.getDescripcion());
            if (compraDTO.getIdSucursal() != null) {
                compra.setSucursal(sucursalesService.buscarId(compraDTO.getIdSucursal())
                    .orElseThrow(() -> new IllegalArgumentException("Sucursal no encontrada")));
            }

            BigDecimal total = compraDTO.getDetalles().stream()
                .map(d -> d.getPrecioCompra().multiply(d.getCantidad()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            compra.setTotal(total);

            Compras compraActualizada = comprasService.editarCompra(compra);

            for (DetallesComprasDTO detalleDTO : compraDTO.getDetalles()) {
                DetallesCompras detalle = new DetallesCompras();
                detalle.setCompra(compraActualizada);
                detalle.setCantidad(detalleDTO.getCantidad());
                detalle.setIdPresentacionSolicitada(detalleDTO.getIdPresentacion());
                detalle.setLote(detalleDTO.getLote());
                detalle.setFechaVencimiento(detalleDTO.getFechaVencimiento());
                detalle.setPrecioUnitario(detalleDTO.getPrecioCompra());
                detalle.setPrecioCompra(detalleDTO.getPrecioCompra());
                detalle.setPrecioVenta(detalleDTO.getPrecioVenta());
                detalle.setSubTotal(detalleDTO.getPrecioCompra().multiply(detalleDTO.getCantidad()));
                detalleCompraService.guardarDetalle(detalle, compraActualizada.getIdCompra(), detalleDTO.getIdProducto());
            }
            
            return ResponseEntity.ok(compraActualizada);
            
        } catch (Exception e) {
            return fallo(e, "Error al actualizar compra: ");
        }
    }

    @GetMapping("/productos/{idProducto}/precios")
    public ResponseEntity<?> obtenerPreciosProducto(@PathVariable Integer idProducto) {
        try {
            DetallesComprasDTO detalleDTO = comprasService.obtenerPreciosProducto(idProducto);
            if (detalleDTO == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(detalleDTO);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("Error al obtener precios: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Transactional
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
            
            // Anular una compra devuelve el stock que sumo (falla si ya se vendio y no alcanza)
            for (DetallesCompras detalle : detalleCompraService.buscarPorIdCompra(id)) {
                detalleCompraService.eliminarDetalle(detalle.getIdDetalleCompra());
            }
            // Baja lógica por estado: borrar la fila dejaría las líneas apuntando a una compra eliminada
            Compras anulada = compra.get();
            anulada.setEstado(0);
            comprasService.editarCompra(anulada);
            return ResponseEntity.ok("Compra eliminada correctamente");
            
        } catch (Exception e) {
            return fallo(e, "Error al eliminar compra: ");
        }
    }
} 