package com.gadbacorp.api.controller.ventas;

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

import com.gadbacorp.api.entity.inventario.Inventario;
import com.gadbacorp.api.entity.inventario.Productos;
import com.gadbacorp.api.entity.ventas.DetallesVentas;
import com.gadbacorp.api.entity.ventas.DetallesVentasDTO;
import com.gadbacorp.api.entity.ventas.Ventas;
import com.gadbacorp.api.repository.inventario.InventarioRepository;
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
    private InventarioRepository inventarioRepository;

    @Autowired
    private ProductosRepository productosRepository;

    @GetMapping("/detalles-ventas")
    public List<DetallesVentas> buscarTodos() {
        return detallesVentasService.listDetallesVentas();
    }
       
    // Buscar venta por ID
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

        Optional<Inventario> inventarioOpt = inventarioRepository.findByProductoIdproducto(dto.getId_producto());
        if (inventarioOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Inventario no encontrado para el producto ID: " + dto.getId_producto());
        }

        Inventario inventario = inventarioOpt.get();
        if (inventario.getStock() < dto.getCantidad()) {
            return ResponseEntity.badRequest().body("Stock insuficiente. Disponible: " + inventario.getStock());
        }

        inventario.setStock(inventario.getStock() - dto.getCantidad());
        inventarioRepository.save(inventario);

        // Guardar detalle de venta
        DetallesVentas detallesVentas = new DetallesVentas();
        detallesVentas.setPecioUnitario(dto.getPecioUnitario());
        detallesVentas.setFechaVenta(dto.getFechaVenta());
        detallesVentas.setCantidad(dto.getCantidad());
        detallesVentas.setSubTotal(dto.getSubTotal());
        detallesVentas.setProductos(producto);
        detallesVentas.setVentas(venta);

        return ResponseEntity.ok(detallesVentasService.guardarDetallesVentas(detallesVentas));
    }

    @PutMapping("/detalles-venta/{id}")
    public ResponseEntity<?> actualizarDetallesVenta(@PathVariable Integer id, @RequestBody DetallesVentasDTO dto) {
        Optional<DetallesVentas> detallesOpt = detallesVentasService.buscarDetallesVentas(id);
        if (detallesOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        DetallesVentas detalles = detallesOpt.get();
        Ventas venta = detalles.getVentas();
        if (venta == null) {
            return ResponseEntity.badRequest().body("La venta asociada no fue encontrada.");
        }

        if ("Facturada".equalsIgnoreCase(venta.getEstado_venta())) {
            return ResponseEntity.status(403).body("No se puede actualizar porque la venta ya está facturada.");
        }

        // Producto anterior
        Productos productoAnterior = detalles.getProductos();
        Optional<Inventario> inventarioOpt = inventarioRepository.findByProductoIdproducto(productoAnterior.getIdproducto());
        if (inventarioOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Inventario no encontrado para el producto ID: " + productoAnterior.getIdproducto());
        }

        Inventario inventario = inventarioOpt.get();

        // Revertimos el stock anterior
        int stockRevertido = inventario.getStock() + detalles.getCantidad();

        // Verificamos si el nuevo producto tiene suficiente stock
        Optional<Inventario> nuevoInventarioOpt = inventarioRepository.findByProductoIdproducto(dto.getId_producto());
        if (nuevoInventarioOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Inventario no encontrado para el nuevo producto ID: " + dto.getId_producto());
        }

        Inventario nuevoInventario = nuevoInventarioOpt.get();

        // Si cambia el producto, se debe manejar el stock de ambos productos
        if (!productoAnterior.getIdproducto().equals(dto.getId_producto())) {
            // Revertimos stock del producto anterior
            inventario.setStock(stockRevertido);
            inventarioRepository.save(inventario);

            if (nuevoInventario.getStock() < dto.getCantidad()) {
                return ResponseEntity.badRequest().body("Stock insuficiente para el nuevo producto.");
            }

            nuevoInventario.setStock(nuevoInventario.getStock() - dto.getCantidad());
            inventarioRepository.save(nuevoInventario);
        } else {
            // Producto no cambió, solo ajustar por diferencia de cantidades
            int nuevaCantidad = dto.getCantidad();
            int diferencia = nuevaCantidad - detalles.getCantidad();

            if (inventario.getStock() < diferencia) {
                return ResponseEntity.badRequest().body("Stock insuficiente. Disponible: " + inventario.getStock());
            }

            inventario.setStock(inventario.getStock() - diferencia);
            inventarioRepository.save(inventario);
        }

        // Actualizamos los detalles
        Productos producto = productosRepository.findById(dto.getId_producto()).orElse(null);
        detalles.setPecioUnitario(dto.getPecioUnitario());
        detalles.setFechaVenta(dto.getFechaVenta());
        detalles.setCantidad(dto.getCantidad());
        detalles.setSubTotal(dto.getSubTotal());
        detalles.setProductos(producto);

        return ResponseEntity.ok(detallesVentasService.guardarDetallesVentas(detalles));
    }


    @DeleteMapping("/detalles-venta/{id}")
    public ResponseEntity<?> eliminarDetalleVenta(@PathVariable Integer id) {
        Optional<DetallesVentas> detalleOpt = detallesVentasService.buscarDetallesVentas(id);
        if (detalleOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        DetallesVentas detalle = detalleOpt.get();

        Ventas venta = detalle.getVentas();
        if (venta != null && "Facturada".equalsIgnoreCase(venta.getEstado_venta())) {
            return ResponseEntity.status(403).body("No se puede eliminar el detalle porque la venta ya está facturada.");
        }

        // Revertir el stock
        Productos producto = detalle.getProductos();
        Optional<Inventario> inventarioOpt = inventarioRepository.findByProductoIdproducto(producto.getIdproducto());

        if (inventarioOpt.isPresent()) {
            Inventario inventario = inventarioOpt.get();
            inventario.setStock(inventario.getStock() + detalle.getCantidad());
            inventarioRepository.save(inventario);
        }

        // Eliminar el detalle
        detallesVentasService.eliminarDetallesVentas(id);

        return ResponseEntity.ok("Detalle de venta eliminado correctamente.");
    }

}
