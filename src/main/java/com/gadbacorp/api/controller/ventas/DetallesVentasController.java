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
import com.gadbacorp.api.entity.ventas.DetallesVentas;
import com.gadbacorp.api.entity.ventas.DetallesVentasDTO;
import com.gadbacorp.api.entity.ventas.Ventas;
import com.gadbacorp.api.repository.inventario.AjusteInventarioRepository;
import com.gadbacorp.api.repository.inventario.InventarioProductoRepository;
import com.gadbacorp.api.repository.inventario.ProductosRepository;
import com.gadbacorp.api.repository.ventas.VentasRepository;
import com.gadbacorp.api.service.ventas.IDetallesVentasService;

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
    private AjusteInventarioRepository ajusteInventarioRepository;

    @Autowired
    private InventarioProductoRepository inventarioProductoRepository;

    @GetMapping("/detalles-ventas")
    public List<DetallesVentas> buscarTodos() {
        return detallesVentasService.listDetallesVentas();
    }

    @GetMapping("/detalles-venta/{id}")
    public Optional<DetallesVentas> buscarVenta(@PathVariable Integer id) {
        return detallesVentasService.buscarDetallesVentas(id);
    }

    @PostMapping("/detalles-venta")
    public ResponseEntity<?> guardarDetallesVentas(@RequestBody DetallesVentasDTO dto) {
        Productos producto = productosRepository.findById(dto.getId_producto()).orElse(null);
        if (producto == null) {
            return ResponseEntity.badRequest().body("Producto no encontrado con ID: " + dto.getId_producto());
        }

        Ventas venta = ventasRepository.findById(dto.getId_venta()).orElse(null);
        if (venta == null) {
            return ResponseEntity.badRequest().body("Venta no encontrada con ID: " + dto.getId_venta());
        }

        Optional<InventarioProducto> inventarioOpt = inventarioProductoRepository.findFirstByProducto_Idproducto(dto.getId_producto());
        if (inventarioOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("InventarioProducto no encontrado para el producto ID: " + dto.getId_producto());
        }

        InventarioProducto inventario = inventarioOpt.get();
        if (inventario.getStockactual() < dto.getCantidad()) {
            return ResponseEntity.badRequest().body("Stock insuficiente. Disponible: " + inventario.getStockactual());
        }

        inventario.setStockactual(inventario.getStockactual() - dto.getCantidad());
        inventarioProductoRepository.save(inventario);

        AjusteInventario ajuste = new AjusteInventario();
        ajuste.setCantidad(dto.getCantidad());
        ajuste.setDescripcion("VENTA");
        ajuste.setFechaAjuste(LocalDateTime.now());
        ajuste.setInventarioProducto(inventario);
        ajusteInventarioRepository.save(ajuste);

        DetallesVentas detallesVentas = new DetallesVentas();
        detallesVentas.setPecioUnitario(dto.getPecioUnitario());
        detallesVentas.setFechaVenta(dto.getFechaVenta());
        detallesVentas.setCantidad(dto.getCantidad());
        detallesVentas.setSubTotal(dto.getSubTotal());
        detallesVentas.setProductos(producto);
        detallesVentas.setVentas(venta);

        return ResponseEntity.ok(detallesVentasService.guardarDetallesVentas(detallesVentas));
    }

    @PutMapping("/detalles-venta")
    public ResponseEntity<?> actualizarDetalleVenta(@RequestBody DetallesVentasDTO dto) {
        Optional<DetallesVentas> detalleExistenteOpt = detallesVentasService.buscarDetallesVentas(dto.getIdDetallesVenta());
        if (detalleExistenteOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        DetallesVentas detalleExistente = detalleExistenteOpt.get();

        Integer idProductoAnterior = detalleExistente.getProductos().getIdproducto();
        Integer idProductoNuevo = dto.getId_producto();

        if (!idProductoAnterior.equals(idProductoNuevo)) {
            InventarioProducto inventarioAnterior = inventarioProductoRepository
                    .findFirstByProducto_Idproducto(idProductoAnterior)
                    .orElse(null);

            if (inventarioAnterior != null) {
                inventarioAnterior.setStockactual(inventarioAnterior.getStockactual() + detalleExistente.getCantidad());
                inventarioProductoRepository.save(inventarioAnterior);

                AjusteInventario ajusteAnterior = new AjusteInventario();
                ajusteAnterior.setCantidad(detalleExistente.getCantidad());
                ajusteAnterior.setDescripcion("RESTAURACIÓN POR CAMBIO DE PRODUCTO");
                ajusteAnterior.setFechaAjuste(LocalDateTime.now());
                ajusteAnterior.setInventarioProducto(inventarioAnterior);
                ajusteInventarioRepository.save(ajusteAnterior);
            }

            InventarioProducto inventarioNuevo = inventarioProductoRepository
                    .findFirstByProducto_Idproducto(idProductoNuevo)
                    .orElse(null);

            if (inventarioNuevo == null || inventarioNuevo.getStockactual() < dto.getCantidad()) {
                return ResponseEntity.badRequest().body("Stock insuficiente para el nuevo producto.");
            }

            inventarioNuevo.setStockactual(inventarioNuevo.getStockactual() - dto.getCantidad());
            inventarioProductoRepository.save(inventarioNuevo);

            AjusteInventario ajusteNuevo = new AjusteInventario();
            ajusteNuevo.setCantidad(dto.getCantidad());
            ajusteNuevo.setDescripcion("AJUSTE POR EDICIÓN CON NUEVO PRODUCTO");
            ajusteNuevo.setFechaAjuste(LocalDateTime.now());
            ajusteNuevo.setInventarioProducto(inventarioNuevo);
            ajusteInventarioRepository.save(ajusteNuevo);

        } else {
            InventarioProducto inventario = inventarioProductoRepository
                    .findFirstByProducto_Idproducto(idProductoNuevo)
                    .orElse(null);

            int cantidadOriginal = detalleExistente.getCantidad();
            int diferenciaCantidad = dto.getCantidad() - cantidadOriginal;

            if (inventario == null || inventario.getStockactual() < diferenciaCantidad) {
                return ResponseEntity.badRequest().body("Stock insuficiente para la modificación.");
            }

            inventario.setStockactual(inventario.getStockactual() - diferenciaCantidad);
            inventarioProductoRepository.save(inventario);

            AjusteInventario ajuste = new AjusteInventario();
            ajuste.setCantidad(Math.abs(diferenciaCantidad));
            ajuste.setDescripcion("AJUSTE POR CAMBIO DE CANTIDAD");
            ajuste.setFechaAjuste(LocalDateTime.now());
            ajuste.setInventarioProducto(inventario);
            ajusteInventarioRepository.save(ajuste);
        }

        Productos producto = productosRepository.findById(dto.getId_producto()).orElse(null);
        if (producto == null) {
            return ResponseEntity.badRequest().body("Producto no encontrado.");
        }
        detalleExistente.setProductos(producto);

        detalleExistente.setCantidad(dto.getCantidad());
        detalleExistente.setSubTotal(dto.getSubTotal());
        detalleExistente.setFechaVenta(dto.getFechaVenta());
        detalleExistente.setPecioUnitario(dto.getPecioUnitario());

        return ResponseEntity.ok(detallesVentasService.guardarDetallesVentas(detalleExistente));
    }

    @DeleteMapping("/detalles-venta/{id}")
    public ResponseEntity<?> eliminarDetalleVenta(@PathVariable Integer id) {
        Optional<DetallesVentas> detalleOpt = detallesVentasService.buscarDetallesVentas(id);
        if (detalleOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        DetallesVentas detalle = detalleOpt.get();
        InventarioProducto inventario = inventarioProductoRepository
                .findFirstByProducto_Idproducto(detalle.getProductos().getIdproducto())
                .orElse(null);

        if (inventario == null) {
            return ResponseEntity.badRequest().body("Inventario no encontrado para producto ID: " + detalle.getProductos().getIdproducto());
        }

        inventario.setStockactual(inventario.getStockactual() + detalle.getCantidad());
        inventarioProductoRepository.save(inventario);

        AjusteInventario ajuste = new AjusteInventario();
        ajuste.setCantidad(detalle.getCantidad());
        ajuste.setDescripcion("AJUSTE POR ELIMINACIÓN DE VENTA");
        ajuste.setFechaAjuste(LocalDateTime.now());
        ajuste.setInventarioProducto(inventario);
        ajusteInventarioRepository.save(ajuste);

        detallesVentasService.eliminarDetallesVentas(id);
        return ResponseEntity.ok().body("Detalle de venta eliminado y stock actualizado.");
    }
}
