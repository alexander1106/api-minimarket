package com.gadbacorp.api.controller.ventas;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import com.gadbacorp.api.entity.inventario.MovimientoStock;
import com.gadbacorp.api.service.jpa.inventario.StockService;
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
import com.gadbacorp.api.entity.ventas.DetallesDevolucion;
import com.gadbacorp.api.entity.ventas.DetallesDevolucionDTO;
import com.gadbacorp.api.repository.inventario.ProductosRepository;
import com.gadbacorp.api.repository.ventas.DevolucionesRepository;
import com.gadbacorp.api.service.ventas.IDetallesDevoluciones;

import io.swagger.v3.oas.annotations.tags.Tag;
@Tag(name = "Ventas - Detalles de devoluciones")
@RestController
@RequestMapping("/api/minimarket")
public class DetallesDevolucionesController {

    @Autowired
    private IDetallesDevoluciones detallesDevoluciones;

    @Autowired
    private DevolucionesRepository devolucionesRepository;

    @Autowired
    private ProductosRepository productosRepository;

    @Autowired
    private StockService stockService;


    @GetMapping("/detalles-devoluciones")
    public List<DetallesDevolucion> listarDevoluciones() {
        return detallesDevoluciones.listarDetallesDevoluciones();
    }

    @GetMapping("/detalles-devoluciones/{id}")
    public Optional<DetallesDevolucion> buscarDetalle(@PathVariable Integer id) {
        return detallesDevoluciones.buscarDetalleDevolucion(id);
    }

    @PostMapping("/detalles-devoluciones")
    @Transactional
    public ResponseEntity<?> guardarDetalle(@RequestBody DetallesDevolucionDTO dto) {
        var productoOpt = productosRepository.findById(dto.getId_producto());
        if (productoOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Producto no encontrado con ID: " + dto.getId_producto());
        }
        var devolucionOpt = devolucionesRepository.findById(dto.getId_devolucion());
        if (devolucionOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Devolución no encontrada con ID: " + dto.getId_devolucion());
        }

        // lo devuelto vuelve al stock de la sucursal donde se hizo la venta
        stockService.mover(sucursalDe(devolucionOpt.get()), productoOpt.get(), dto.getCantidad(),
                MovimientoStock.DEVOLUCION, "Devolucion #" + devolucionOpt.get().getIdDevolucion());

        DetallesDevolucion detalle = new DetallesDevolucion();
        detalle.setCantidad(dto.getCantidad());
        detalle.setPecioUnitario(dto.getPecioUnitario());
        detalle.setSubTotal(dto.getSubTotal());
        detalle.setProductos(productoOpt.get());
        detalle.setDevoluciones(devolucionOpt.get());

        return ResponseEntity.ok(detallesDevoluciones.guardarDetallesDevolucion(detalle));
    }

    @PutMapping("/detalles-devoluciones")
    @Transactional
    public ResponseEntity<?> actualizarDetalle(@RequestBody DetallesDevolucionDTO dto) {
        Optional<DetallesDevolucion> detalleOpt = detallesDevoluciones.buscarDetalleDevolucion(dto.getIdDetallesDevoluciones());
        if (detalleOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        DetallesDevolucion detalleExistente = detalleOpt.get();

        Productos nuevoProducto = productosRepository.findById(dto.getId_producto()).orElse(null);
        if (nuevoProducto == null) {
            return ResponseEntity.badRequest().body("Producto nuevo no encontrado con ID: " + dto.getId_producto());
        }

        Integer idSucursal = sucursalDe(detalleExistente.getDevoluciones());
        Productos anterior = detalleExistente.getProductos();
        if (anterior.getIdproducto().equals(nuevoProducto.getIdproducto())) {
            stockService.mover(idSucursal, nuevoProducto, dto.getCantidad().subtract(detalleExistente.getCantidad()),
                    MovimientoStock.DEVOLUCION, "Devolucion editada");
        } else {
            stockService.mover(idSucursal, anterior, detalleExistente.getCantidad().negate(),
                    MovimientoStock.DEVOLUCION, "Devolucion editada (cambio de producto)");
            stockService.mover(idSucursal, nuevoProducto, dto.getCantidad(),
                    MovimientoStock.DEVOLUCION, "Devolucion editada (cambio de producto)");
        }

        detalleExistente.setProductos(nuevoProducto);
        detalleExistente.setCantidad(dto.getCantidad());
        detalleExistente.setPecioUnitario(dto.getPecioUnitario());
        detalleExistente.setSubTotal(dto.getSubTotal());

        return ResponseEntity.ok(detallesDevoluciones.guardarDetallesDevolucion(detalleExistente));
    }

    @DeleteMapping("/detalles-devoluciones/{id}")
    @Transactional
    public ResponseEntity<?> eliminarDetalle(@PathVariable Integer id) {
        Optional<DetallesDevolucion> detalleOpt = detallesDevoluciones.buscarDetalleDevolucion(id);
        if (detalleOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        DetallesDevolucion detalle = detalleOpt.get();
        stockService.mover(sucursalDe(detalle.getDevoluciones()), detalle.getProductos(), detalle.getCantidad().negate(),
                MovimientoStock.DEVOLUCION, "Linea de devolucion eliminada");

        detallesDevoluciones.eliminarDetallesCotizaciones(id);
        return ResponseEntity.ok().body("Detalle de devolución eliminado y stock actualizado.");
    }

    private static Integer sucursalDe(com.gadbacorp.api.entity.ventas.Devoluciones devolucion) {
        return StockService.sucursalDe(devolucion.getVentas());
    }
}
