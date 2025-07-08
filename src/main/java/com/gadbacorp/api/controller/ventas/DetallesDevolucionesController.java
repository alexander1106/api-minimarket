package com.gadbacorp.api.controller.ventas;

import java.time.LocalDateTime;
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
import com.gadbacorp.api.entity.inventario.Productos;
import com.gadbacorp.api.entity.ventas.DetallesDevolucion;
import com.gadbacorp.api.entity.ventas.DetallesDevolucionDTO;
import com.gadbacorp.api.repository.inventario.AjusteInventarioRepository;
import com.gadbacorp.api.repository.inventario.InventarioProductoRepository;
import com.gadbacorp.api.repository.inventario.ProductosRepository;
import com.gadbacorp.api.repository.ventas.DevolucionesRepository;
import com.gadbacorp.api.service.ventas.IDetallesDevoluciones;

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
    private InventarioProductoRepository inventarioProductoRepository;

    @Autowired
    private AjusteInventarioRepository ajusteInventarioRepository;

    @GetMapping("/detalles-devoluciones")
    public List<DetallesDevolucion> listarDevoluciones() {
        return detallesDevoluciones.listarDetallesDevoluciones();
    }

    @GetMapping("/detalles-devoluciones/{id}")
    public Optional<DetallesDevolucion> buscarDetalle(@PathVariable Integer id) {
        return detallesDevoluciones.buscarDetalleDevolucion(id);
    }

    @PostMapping("/detalles-devoluciones")
    public ResponseEntity<?> guardarDetalle(@RequestBody DetallesDevolucionDTO dto) {
        var productoOpt = productosRepository.findById(dto.getId_producto());
        if (productoOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Producto no encontrado con ID: " + dto.getId_producto());
        }

        var devolucionOpt = devolucionesRepository.findById(dto.getId_devolucion());
        if (devolucionOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Devolución no encontrada con ID: " + dto.getId_devolucion());
        }

        var inventarioOpt = inventarioProductoRepository.findFirstByProducto_Idproducto(dto.getId_producto());
        if (inventarioOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Inventario no encontrado para producto ID: " + dto.getId_producto());
        }

        InventarioProducto inventario = inventarioOpt.get();
        inventario.setStockactual(inventario.getStockactual() + dto.getCantidad());
        inventarioProductoRepository.save(inventario);

        AjusteInventario ajuste = new AjusteInventario();
        ajuste.setCantidad(dto.getCantidad());
        ajuste.setDescripcion("DEVOLUCIÓN");
        ajuste.setFechaAjuste(LocalDateTime.now());
        ajuste.setInventarioProducto(inventario);
        ajusteInventarioRepository.save(ajuste);

        DetallesDevolucion detalle = new DetallesDevolucion();
        detalle.setCantidad(dto.getCantidad());
        detalle.setPecioUnitario(dto.getPecioUnitario());
        detalle.setSubTotal(dto.getSubTotal());
        detalle.setProductos(productoOpt.get());
        detalle.setDevoluciones(devolucionOpt.get());

        return ResponseEntity.ok(detallesDevoluciones.guardarDetallesDevolucion(detalle));
    }

    @PutMapping("/detalles-devoluciones")
    public ResponseEntity<?> actualizarDetalle(@RequestBody DetallesDevolucionDTO dto) {
        Optional<DetallesDevolucion> detalleOpt = detallesDevoluciones.buscarDetalleDevolucion(dto.getIdDetallesDevoluciones());
        if (detalleOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        DetallesDevolucion detalleExistente = detalleOpt.get();
        Integer productoAnteriorId = detalleExistente.getProductos().getIdproducto();

        InventarioProducto inventarioAnterior = inventarioProductoRepository.findFirstByProducto_Idproducto(productoAnteriorId)
                .orElse(null);
        if (inventarioAnterior == null) {
            return ResponseEntity.badRequest().body("Inventario no encontrado para producto anterior ID: " + productoAnteriorId);
        }

        Productos nuevoProducto = productosRepository.findById(dto.getId_producto()).orElse(null);
        if (nuevoProducto == null) {
            return ResponseEntity.badRequest().body("Producto nuevo no encontrado con ID: " + dto.getId_producto());
        }

        InventarioProducto inventarioNuevo = inventarioProductoRepository.findFirstByProducto_Idproducto(dto.getId_producto())
                .orElse(null);
        if (inventarioNuevo == null) {
            return ResponseEntity.badRequest().body("Inventario no encontrado para producto nuevo ID: " + dto.getId_producto());
        }

        if (productoAnteriorId.equals(dto.getId_producto())) {
            int diferencia = dto.getCantidad() - detalleExistente.getCantidad();

            if (diferencia == 0) {
                detalleExistente.setPecioUnitario(dto.getPecioUnitario());
                detalleExistente.setSubTotal(dto.getSubTotal());
                return ResponseEntity.ok(detallesDevoluciones.guardarDetallesDevolucion(detalleExistente));
            }

            int nuevoStock = inventarioNuevo.getStockactual() + diferencia;
            if (nuevoStock < 0) {
                return ResponseEntity.badRequest().body("No se puede actualizar: el stock resultante sería negativo.");
            }

            inventarioNuevo.setStockactual(nuevoStock);
            inventarioProductoRepository.save(inventarioNuevo);

            AjusteInventario ajuste = new AjusteInventario();
            ajuste.setCantidad(Math.abs(diferencia));
            ajuste.setDescripcion("AJUSTE POR EDICIÓN DE DEVOLUCIÓN");
            ajuste.setFechaAjuste(LocalDateTime.now());
            ajuste.setInventarioProducto(inventarioNuevo);
            ajusteInventarioRepository.save(ajuste);
        } else {
            if (dto.getCantidad() == detalleExistente.getCantidad()) {
                detalleExistente.setProductos(nuevoProducto);
                detalleExistente.setPecioUnitario(dto.getPecioUnitario());
                detalleExistente.setSubTotal(dto.getSubTotal());
                return ResponseEntity.ok(detallesDevoluciones.guardarDetallesDevolucion(detalleExistente));
            }

            int nuevoStockNuevo = inventarioNuevo.getStockactual() + dto.getCantidad();
            int nuevoStockAnterior = inventarioAnterior.getStockactual() - detalleExistente.getCantidad();

            if (nuevoStockAnterior < 0) {
                return ResponseEntity.badRequest().body("No se puede revertir: el stock del producto anterior quedaría negativo.");
            }

            inventarioAnterior.setStockactual(nuevoStockAnterior);
            inventarioNuevo.setStockactual(nuevoStockNuevo);

            inventarioProductoRepository.save(inventarioAnterior);
            inventarioProductoRepository.save(inventarioNuevo);

            AjusteInventario ajusteAnulacion = new AjusteInventario();
            ajusteAnulacion.setCantidad(detalleExistente.getCantidad());
            ajusteAnulacion.setDescripcion("AJUSTE POR ANULACIÓN DE DETALLE ANTERIOR");
            ajusteAnulacion.setFechaAjuste(LocalDateTime.now());
            ajusteAnulacion.setInventarioProducto(inventarioAnterior);
            ajusteInventarioRepository.save(ajusteAnulacion);

            AjusteInventario ajusteNuevo = new AjusteInventario();
            ajusteNuevo.setCantidad(dto.getCantidad());
            ajusteNuevo.setDescripcion("AJUSTE POR NUEVO DETALLE DE DEVOLUCIÓN");
            ajusteNuevo.setFechaAjuste(LocalDateTime.now());
            ajusteNuevo.setInventarioProducto(inventarioNuevo);
            ajusteInventarioRepository.save(ajusteNuevo);
        }

        
        detalleExistente.setProductos(nuevoProducto);
        detalleExistente.setCantidad(dto.getCantidad());
        detalleExistente.setPecioUnitario(dto.getPecioUnitario());
        detalleExistente.setSubTotal(dto.getSubTotal());

        return ResponseEntity.ok(detallesDevoluciones.guardarDetallesDevolucion(detalleExistente));
    }


    @DeleteMapping("/detalles-devoluciones/{id}")
    public ResponseEntity<?> eliminarDetalle(@PathVariable Integer id) {
        Optional<DetallesDevolucion> detalleOpt = detallesDevoluciones.buscarDetalleDevolucion(id);
        if (detalleOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        DetallesDevolucion detalle = detalleOpt.get();
        InventarioProducto inventario = inventarioProductoRepository.findFirstByProducto_Idproducto(detalle.getProductos().getIdproducto())
                .orElse(null);

        if (inventario == null) {
            return ResponseEntity.badRequest().body("Inventario no encontrado para producto ID: " + detalle.getProductos().getIdproducto());
        }

        inventario.setStockactual(inventario.getStockactual() - detalle.getCantidad());
        inventarioProductoRepository.save(inventario);

        AjusteInventario ajuste = new AjusteInventario();
        ajuste.setCantidad(detalle.getCantidad());
        ajuste.setDescripcion("AJUSTE POR ELIMINACIÓN DE DEVOLUCIÓN");
        ajuste.setFechaAjuste(LocalDateTime.now());
        ajuste.setInventarioProducto(inventario);
        ajusteInventarioRepository.save(ajuste);

        detallesDevoluciones.eliminarDetallesCotizaciones(id);
        return ResponseEntity.ok().body("Detalle de devolución eliminado y stock actualizado.");
    }
}
