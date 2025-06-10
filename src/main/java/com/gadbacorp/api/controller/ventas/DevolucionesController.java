package com.gadbacorp.api.controller.ventas;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
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

@RestController
@RequestMapping("/api/minimarket")
@CrossOrigin("*")
public class DevolucionesController {

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
            nuevoDetalleDev.setCantidad(detalle.getCantidad());
            nuevoDetalleDev.setPecioUnitario(detalle.getPecioUnitario());
            detallesDevolucionesService.guardarDetallesDevolucion(nuevoDetalleDev);
        }

        for (DetallesVentas detalle : detalles) {
            Integer productoId = detalle.getProductos().getIdproducto();
            Integer cantidadVendida = detalle.getCantidad();
            List<InventarioProducto> inventarioList = inventarioProductoRepository.findAllByProducto_Idproducto(productoId);

            for (InventarioProducto inventario : inventarioList) {
                inventario.setStockactual(inventario.getStockactual() + cantidadVendida);
                inventarioProductoRepository.save(inventario);

                AjusteInventario ajuste = new AjusteInventario();
                ajuste.setCantidad(cantidadVendida);
                ajuste.setDescripcion("DEVOLUCIÓN");
                ajuste.setFechaAjuste(java.time.LocalDateTime.now());
                ajuste.setInventarioProducto(inventario);
                ajusteInventarioRepository.save(ajuste);
            }
        }

        return ResponseEntity.ok(devolucionGuardada);
    }

    @DeleteMapping("/devoluciones/{id}")
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
            Integer cantidadDevuelta = detalleDev.getCantidad();

            List<InventarioProducto> inventarioList = inventarioProductoRepository.findAllByProducto_Idproducto(productoId);
            for (InventarioProducto inventario : inventarioList) {
                inventario.setStockactual(inventario.getStockactual() - cantidadDevuelta);
                inventarioProductoRepository.save(inventario);

                AjusteInventario ajuste = new AjusteInventario();
                ajuste.setCantidad(-cantidadDevuelta);
                ajuste.setDescripcion("REVERTIR ELIMINACIÓN DEVOLUCIÓN");
                ajuste.setFechaAjuste(java.time.LocalDateTime.now());
                ajuste.setInventarioProducto(inventario);
                ajusteInventarioRepository.save(ajuste);
            }

            detallesDevolucionesService.eliminarDetallesCotizaciones(detalleDev.getIdDetallesDevoluciones());
        }

        devolucionesService.elimmanrDevolucion(id);
        return ResponseEntity.ok("Devolución eliminada correctamente y cambios revertidos.");
    }

    @PutMapping("/devoluciones")
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
                    Integer cantidad = detalle.getCantidad();
                    List<InventarioProducto> inventarios = inventarioProductoRepository.findAllByProducto_Idproducto(productoId);

                    for (InventarioProducto inventario : inventarios) {
                        inventario.setStockactual(inventario.getStockactual() - cantidad);
                        inventarioProductoRepository.save(inventario);

                        AjusteInventario ajuste = new AjusteInventario();
                        ajuste.setCantidad(-cantidad);
                        ajuste.setDescripcion("REVERTIR DEVOLUCIÓN POR CAMBIO DE VENTA");
                        ajuste.setFechaAjuste(java.time.LocalDateTime.now());
                        ajuste.setInventarioProducto(inventario);
                        ajusteInventarioRepository.save(ajuste);
                    }
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
                Integer cantidad = detalle.getCantidad();
                List<InventarioProducto> inventarios = inventarioProductoRepository.findAllByProducto_Idproducto(productoId);

                for (InventarioProducto inventario : inventarios) {
                    inventario.setStockactual(inventario.getStockactual() + cantidad);
                    inventarioProductoRepository.save(inventario);

                    AjusteInventario ajuste = new AjusteInventario();
                    ajuste.setCantidad(cantidad);
                    ajuste.setDescripcion("AJUSTE POR NUEVA VENTA EN DEVOLUCIÓN");
                    ajuste.setFechaAjuste(java.time.LocalDateTime.now());
                    ajuste.setInventarioProducto(inventario);
                    ajusteInventarioRepository.save(ajuste);
                }
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
