package com.gadbacorp.api.controller.ventas;

import org.springframework.transaction.annotation.Transactional;
import com.gadbacorp.api.entity.inventario.MovimientoStock;
import com.gadbacorp.api.service.jpa.inventario.StockService;
import java.util.List;
import java.util.Optional;

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

import com.gadbacorp.api.entity.inventario.Productos;
import com.gadbacorp.api.entity.ventas.DetallesVentas;
import com.gadbacorp.api.service.jpa.inventario.ConversionUnidadesService;
import com.gadbacorp.api.entity.ventas.DetallesVentasDTO;
import com.gadbacorp.api.entity.ventas.Ventas;
import com.gadbacorp.api.repository.inventario.ProductosRepository;
import com.gadbacorp.api.repository.ventas.VentasRepository;
import com.gadbacorp.api.service.ventas.IDetallesVentasService;

import io.swagger.v3.oas.annotations.tags.Tag;
@Tag(name = "Ventas - Detalles de ventas")
@RestController
@RequestMapping("/api/minimarket")
public class DetallesVentasController {

    @Autowired
    private IDetallesVentasService detallesVentasService;

    @Autowired
    private VentasRepository ventasRepository;

    @Autowired
    private ProductosRepository productosRepository;


    @Autowired
    private StockService stockService;

    @Autowired
    private ConversionUnidadesService conversionUnidadesService;

    @GetMapping("/detalles-ventas")
    public List<DetallesVentas> buscarTodos() {
        return detallesVentasService.listDetallesVentas();
    }

    @GetMapping("/detalles-ventas/{id}")
    public Optional<DetallesVentas> buscarVenta(@PathVariable Integer id) {
        return detallesVentasService.buscarDetallesVentas(id);
    }
    @GetMapping("/detalles-ventas/venta/{idVenta}")
public ResponseEntity<?> buscarDetallesPorIdVenta(@PathVariable Integer idVenta) {
    List<DetallesVentas> detalles = detallesVentasService.buscarPorIdVenta(idVenta);
    if (detalles.isEmpty()) {
        return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(detalles);
}


    @PostMapping("/detalles-ventas")
    @Transactional
    public ResponseEntity<?> guardarDetallesVentas(@RequestBody DetallesVentasDTO dto) {
        Productos producto = productosRepository.findById(dto.getId_producto()).orElse(null);
        if (producto == null) {
            return ResponseEntity.badRequest().body("Producto no encontrado con ID: " + dto.getId_producto());
        }
        Ventas venta = ventasRepository.findById(dto.getId_venta()).orElse(null);
        if (venta == null) {
            return ResponseEntity.badRequest().body("Venta no encontrada con ID: " + dto.getId_venta());
        }

        // la cantidad puede venir en una presentacion (2 sacos) o en unidad base (2.5 kg): el stock se mueve en base
        ConversionUnidadesService.Conversion conversion =
                conversionUnidadesService.convertir(producto, dto.getIdPresentacion(), dto.getCantidad());
        stockService.descontar(StockService.sucursalDe(venta), producto.getIdproducto(), conversion.cantidadBase(),
                MovimientoStock.VENTA, "Venta #" + venta.getIdVenta() + " (linea agregada)");

        DetallesVentas detallesVentas = new DetallesVentas();
        detallesVentas.setPrecioUnitario(dto.getPecioUnitario());
        detallesVentas.setFechaVenta(dto.getFechaVenta());
        detallesVentas.setCantidad(dto.getCantidad());
        detallesVentas.setPresentacion(conversion.presentacion());
        detallesVentas.setCantidadBase(conversion.cantidadBase());
        detallesVentas.setSubTotal(dto.getSubTotal());
        detallesVentas.setProductos(producto);
        detallesVentas.setVentas(venta);

        return ResponseEntity.ok(detallesVentasService.guardarDetallesVentas(detallesVentas));
    }

    @PutMapping("/detalles-ventas")
    @Transactional
    public ResponseEntity<?> actualizarDetalleVenta(@RequestBody DetallesVentasDTO dto) {
        Optional<DetallesVentas> detalleExistenteOpt = detallesVentasService.buscarDetallesVentas(dto.getIdDetallesVenta());
        if (detalleExistenteOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        DetallesVentas detalleExistente = detalleExistenteOpt.get();

        Productos productoNuevo = productosRepository.findById(dto.getId_producto()).orElse(null);
        if (productoNuevo == null) {
            return ResponseEntity.badRequest().body("Producto no encontrado.");
        }
        ConversionUnidadesService.Conversion conversion =
                conversionUnidadesService.convertir(productoNuevo, dto.getIdPresentacion(), dto.getCantidad());

        // Se devuelve lo vendido antes y se descuenta lo nuevo, en la sucursal de la venta; si falta stock nada cambia
        Integer idSucursal = StockService.sucursalDe(detalleExistente.getVentas());
        Productos productoAnterior = detalleExistente.getProductos();
        if (productoAnterior.getIdproducto().equals(productoNuevo.getIdproducto())) {
            stockService.mover(idSucursal, productoNuevo, detalleExistente.cantidadEnBase().subtract(conversion.cantidadBase()),
                    MovimientoStock.VENTA, "Venta #" + detalleExistente.getVentas().getIdVenta() + " (linea editada)");
        } else {
            stockService.mover(idSucursal, productoAnterior, detalleExistente.cantidadEnBase(),
                    MovimientoStock.ANULACION_VENTA, "Venta #" + detalleExistente.getVentas().getIdVenta() + " (cambio de producto)");
            stockService.descontar(idSucursal, productoNuevo.getIdproducto(), conversion.cantidadBase(),
                    MovimientoStock.VENTA, "Venta #" + detalleExistente.getVentas().getIdVenta() + " (cambio de producto)");
        }

        detalleExistente.setProductos(productoNuevo);
        detalleExistente.setCantidad(dto.getCantidad());
        detalleExistente.setPresentacion(conversion.presentacion());
        detalleExistente.setCantidadBase(conversion.cantidadBase());
        detalleExistente.setSubTotal(dto.getSubTotal());
        detalleExistente.setFechaVenta(dto.getFechaVenta());
        detalleExistente.setPrecioUnitario(dto.getPecioUnitario());

        return ResponseEntity.ok(detallesVentasService.guardarDetallesVentas(detalleExistente));
    }

    @DeleteMapping("/detalles-ventas/{id}")
    @Transactional
    public ResponseEntity<?> eliminarDetalleVenta(@PathVariable Integer id) {
        Optional<DetallesVentas> detalleOpt = detallesVentasService.buscarDetallesVentas(id);
        if (detalleOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        DetallesVentas detalle = detalleOpt.get();
        stockService.mover(StockService.sucursalDe(detalle.getVentas()), detalle.getProductos(), detalle.cantidadEnBase(),
                MovimientoStock.ANULACION_VENTA, "Venta #" + detalle.getVentas().getIdVenta() + " (linea eliminada)");

        detallesVentasService.eliminarDetallesVentas(id);
        return ResponseEntity.ok().body("Detalle de venta eliminado y stock actualizado.");
    }
}
