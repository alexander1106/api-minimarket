package com.gadbacorp.api.controller.ventas;

import java.math.BigDecimal;
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

import com.gadbacorp.api.entity.inventario.AjusteInventario;
import com.gadbacorp.api.entity.inventario.InventarioProducto;
import com.gadbacorp.api.entity.ventas.DetallesDevolucion;
import com.gadbacorp.api.entity.ventas.DetallesVentas;
import com.gadbacorp.api.entity.ventas.Devoluciones;
import com.gadbacorp.api.entity.ventas.DevolucionesDTO;
import com.gadbacorp.api.entity.ventas.Ventas;
import com.gadbacorp.api.repository.inventario.AjusteInventarioRepository;
import com.gadbacorp.api.repository.inventario.InventarioProductoRepository;
import com.gadbacorp.api.repository.ventas.VentasRepository;
import com.gadbacorp.api.service.ventas.IDetallesDevoluciones;
import com.gadbacorp.api.service.ventas.IDetallesVentasService;
import com.gadbacorp.api.service.ventas.IDevolucionesService;

import io.swagger.v3.oas.annotations.tags.Tag;
@Tag(name = "Ventas - Devoluciones")
@RestController
@RequestMapping("/api/minimarket")
public class DevolucionesController {
    @org.springframework.beans.factory.annotation.Autowired
    private com.gadbacorp.api.service.jpa.inventario.StockService stockService;


    @Autowired
    private IDevolucionesService devolucionesService;

    @Autowired
    private VentasRepository ventasRepository;

    @Autowired
    private IDetallesVentasService detallesVentasService;

    @Autowired
    private AjusteInventarioRepository ajusteInventarioRepository;

    @Autowired
    private InventarioProductoRepository inventarioProductoRepository;

    @Autowired
    private IDetallesDevoluciones detallesDevolucionesService;

    @GetMapping("/devoluciones")
    public List<Devoluciones> listarTodos() {
        return devolucionesService.listarDevoluciones();
    }

    @GetMapping("/devoluciones/{id}")
    public Optional<Devoluciones> buscarDevolucion(@PathVariable Integer id) {
        return devolucionesService.buscarDevolucion(id);
    }

    @PostMapping("/devoluciones")
    @org.springframework.transaction.annotation.Transactional
    public ResponseEntity<?> guardarDevolucion(@RequestBody DevolucionesDTO dto) {
        Ventas venta = ventasRepository.findById(dto.getId_venta()).orElse(null);
        if (venta == null) {
            return ResponseEntity.badRequest().body("Venta no encontrada con ID: " + dto.getId_venta());
        }

        List<DetallesVentas> detalles = detallesVentasService.buscarPorIdVenta(dto.getId_venta());

        Devoluciones devolucion = new Devoluciones();
        devolucion.setFechaDevolucion(dto.getFechaDevolucion());
        devolucion.setMontoDevuelto(dto.getMontoDevuelto());
        devolucion.setMotivoDevolucion(dto.getMotivoDevolucion());
        devolucion.setObservaciones(dto.getObservaciones());
        devolucion.setVentas(venta);

        venta.setEstado_venta("DEVUELTA");
        ventasRepository.save(venta);

        Devoluciones devolucionGuardada = devolucionesService.guardarDevoluciones(devolucion);

        for (DetallesVentas detalle : detalles) {
            DetallesDevolucion nuevoDetalleDev = new DetallesDevolucion();
            nuevoDetalleDev.setDevoluciones(devolucionGuardada);
            nuevoDetalleDev.setProductos(detalle.getProductos());
            nuevoDetalleDev.setCantidad(detalle.cantidadEnBase());
            detallesDevolucionesService.guardarDetallesDevolucion(nuevoDetalleDev);
        }

        for (DetallesVentas detalle : detalles) {
            Integer productoId = detalle.getProductos().getIdproducto();
            BigDecimal cantidadVendida = detalle.cantidadEnBase();
            stockService.mover(com.gadbacorp.api.service.jpa.inventario.StockService.sucursalDe(venta), detalle.getProductos(), cantidadVendida,
                com.gadbacorp.api.entity.inventario.MovimientoStock.DEVOLUCION, "Devolucion de venta #" + venta.getIdVenta());
        }

        return ResponseEntity.ok(devolucionGuardada);
    }

    @DeleteMapping("/devoluciones/{id}")
    @org.springframework.transaction.annotation.Transactional
    public ResponseEntity<?> eliminarDevolucion(@PathVariable Integer id) {
        Optional<Devoluciones> optionalDevolucion = devolucionesService.buscarDevolucion(id);
        if (optionalDevolucion.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Devoluciones devolucion = optionalDevolucion.get();
        Ventas venta = devolucion.getVentas();
        if (venta == null) {
            return ResponseEntity.badRequest().body("Devolución no está asociada a una venta válida.");
        }

        venta.setEstado_venta("FINALIZADA");
        ventasRepository.save(venta);

        List<DetallesDevolucion> detallesDevolucion = detallesDevolucionesService.buscarPorIdDevolucion(id);
        for (DetallesDevolucion detalleDev : detallesDevolucion) {
            Integer productoId = detalleDev.getProductos().getIdproducto();
            BigDecimal cantidadDevuelta = detalleDev.getCantidad();

            stockService.mover(com.gadbacorp.api.service.jpa.inventario.StockService.sucursalDe(venta), detalleDev.getProductos(), cantidadDevuelta.negate(),
                com.gadbacorp.api.entity.inventario.MovimientoStock.DEVOLUCION, "Devolucion eliminada, venta #" + venta.getIdVenta());

            detallesDevolucionesService.eliminarDetallesCotizaciones(detalleDev.getIdDetallesDevoluciones());
        }

        devolucionesService.elimmanrDevolucion(id);
        return ResponseEntity.ok("Devolución eliminada correctamente y cambios revertidos.");
    }

    @PutMapping("/devoluciones")
    @org.springframework.transaction.annotation.Transactional
    public ResponseEntity<?> actualizarDevolucion(@RequestBody DevolucionesDTO dto) {
        if (dto.getId_devolucion() == null) {
            return ResponseEntity.badRequest().body("Debe proporcionar el ID de la devolución.");
        }

        Optional<Devoluciones> optionalDevolucion = devolucionesService.buscarDevolucion(dto.getId_devolucion());
        if (optionalDevolucion.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Devoluciones devolucion = optionalDevolucion.get();
        Ventas ventaAnterior = devolucion.getVentas();

        if (dto.getId_venta() != null && (ventaAnterior == null || !ventaAnterior.getIdVenta().equals(dto.getId_venta()))) {
            if (ventaAnterior != null) {
                List<DetallesVentas> detallesAnteriores = detallesVentasService.buscarPorIdVenta(ventaAnterior.getIdVenta());
                for (DetallesVentas detalle : detallesAnteriores) {
                    Integer productoId = detalle.getProductos().getIdproducto();
                    BigDecimal cantidad = detalle.cantidadEnBase();
                    stockService.mover(com.gadbacorp.api.service.jpa.inventario.StockService.sucursalDe(ventaAnterior), detalle.getProductos(), cantidad.negate(),
                com.gadbacorp.api.entity.inventario.MovimientoStock.DEVOLUCION, "Cambio de venta en devolucion");
                }
            }

            Optional<Ventas> nuevaVentaOpt = ventasRepository.findById(dto.getId_venta());
            if (nuevaVentaOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Venta no encontrada con ID: " + dto.getId_venta());
            }

            Ventas nuevaVenta = nuevaVentaOpt.get();
            List<DetallesVentas> nuevosDetalles = detallesVentasService.buscarPorIdVenta(nuevaVenta.getIdVenta());
            for (DetallesVentas detalle : nuevosDetalles) {
                Integer productoId = detalle.getProductos().getIdproducto();
                BigDecimal cantidad = detalle.cantidadEnBase();
                stockService.mover(com.gadbacorp.api.service.jpa.inventario.StockService.sucursalDe(nuevaVenta), detalle.getProductos(), cantidad,
                com.gadbacorp.api.entity.inventario.MovimientoStock.DEVOLUCION, "Cambio de venta en devolucion");
            }

            devolucion.setVentas(nuevaVenta);
        }

        devolucion.setFechaDevolucion(dto.getFechaDevolucion());
        devolucion.setMontoDevuelto(dto.getMontoDevuelto());
        devolucion.setMotivoDevolucion(dto.getMotivoDevolucion());
        devolucion.setObservaciones(dto.getObservaciones());

        Devoluciones actualizada = devolucionesService.guardarDevoluciones(devolucion);
        return ResponseEntity.ok(actualizada);
    }
}
