package com.gadbacorp.api.controller.ventas;

import java.time.LocalDate;
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
import com.gadbacorp.api.entity.ventas.Cotizaciones;
import com.gadbacorp.api.entity.ventas.DetallesCotizaciones;
import com.gadbacorp.api.entity.ventas.DetallesCotizacionesDTO;
import com.gadbacorp.api.entity.ventas.DetallesVentas;
import com.gadbacorp.api.entity.ventas.Ventas;
import com.gadbacorp.api.repository.inventario.InventarioRepository;
import com.gadbacorp.api.repository.inventario.ProductosRepository;
import com.gadbacorp.api.repository.ventas.CotizacionesRepository;
import com.gadbacorp.api.repository.ventas.DetallesVentasRepository;
import com.gadbacorp.api.repository.ventas.VentasRepository;
import com.gadbacorp.api.service.ventas.IDetallesCotizacionesService;

@RestController
@RequestMapping("/api/minimarket")
public class DetallesCotizacionesController {
    @Autowired
    private IDetallesCotizacionesService detallesCotizacionesService;
    
    @Autowired
    private ProductosRepository productosRepository;

    @Autowired
    private CotizacionesRepository cotizacionesRepository;
    
    @Autowired
    private InventarioRepository inventarioRepository;

    @Autowired
    private VentasRepository ventasRepository;

    @Autowired
    private DetallesVentasRepository detallesVentasRepository;
    
    @GetMapping("/detalles-cotizaciones")
    public List<DetallesCotizaciones> listarCotizacioneses() {
        return detallesCotizacionesService.listarDetallesCotizacioneses();
    }

    @GetMapping("/detalles-cotizacion/{id}")
    public Optional<DetallesCotizaciones> buscarVenta(@PathVariable Integer id) {
        return detallesCotizacionesService.buscarDetallesCotizaciones(id);
    }

    @PostMapping("/detalles-cotizacion")
    public ResponseEntity<?> guardarDetallesCotizaciones(@RequestBody DetallesCotizacionesDTO dto) {
        Productos producto = productosRepository.findById(dto.getId_producto()).orElse(null);
        if (producto == null) {
            return ResponseEntity.badRequest().body("Producto no encontrado con ID: " + dto.getId_producto());
        }

        Cotizaciones cotizacion = cotizacionesRepository.findById(dto.getId_cotizacion()).orElse(null);
        if (cotizacion == null) {
            return ResponseEntity.badRequest().body("Cotización no encontrada con ID: " + dto.getId_cotizacion());
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

        DetallesCotizaciones detallesCotizaciones = new DetallesCotizaciones();
        detallesCotizaciones.setCantidad(dto.getCantidad());
        detallesCotizaciones.setFechaCotizacion(dto.getFechaCotizaciones());
        detallesCotizaciones.setPrecioUnitario(dto.getPrecioUnitario());
        detallesCotizaciones.setSubTotal(dto.getSubTotal());
        detallesCotizaciones.setProductos(producto);
        detallesCotizaciones.setCotizaciones(cotizacion);
        return ResponseEntity.ok(detallesCotizacionesService.guardarDetallesCotizaciones(detallesCotizaciones));
    }

    @PutMapping("/detalles-cotizaciones/{id}")
    public ResponseEntity<?> actualizarDetallesCotizaciones(@PathVariable Integer id, @RequestBody DetallesCotizacionesDTO dto) {
        Optional<DetallesCotizaciones> detallesOpt = detallesCotizacionesService.buscarDetallesCotizaciones(id);
        if (detallesOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        DetallesCotizaciones detalles = detallesOpt.get();
        Cotizaciones cotizacion = detalles.getCotizaciones();
        if (cotizacion == null) {
            return ResponseEntity.badRequest().body("Cotización no encontrada para el detalle con ID: " + id);
        }

        Productos producto = productosRepository.findById(dto.getId_producto()).orElse(null);
        if (producto == null) {
            return ResponseEntity.badRequest().body("Producto no encontrado con ID: " + dto.getId_producto());
        }

        Optional<Inventario> inventarioOpt = inventarioRepository.findByProductoIdproducto(dto.getId_producto());
        if (inventarioOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Inventario no encontrado para el producto ID: " + dto.getId_producto());
        }

        Inventario inventario = inventarioOpt.get();

        inventario.setStock(inventario.getStock() + detalles.getCantidad());

        if (inventario.getStock() < dto.getCantidad()) {
            return ResponseEntity.badRequest().body("Stock insuficiente. Disponible: " + inventario.getStock());
        }

        inventario.setStock(inventario.getStock() - dto.getCantidad());
        inventarioRepository.save(inventario);

        detalles.setCantidad(dto.getCantidad());
        detalles.setPrecioUnitario(dto.getPrecioUnitario());
        detalles.setSubTotal(dto.getSubTotal());
        detalles.setFechaCotizacion(dto.getFechaCotizaciones());
        detalles.setProductos(producto);

        return ResponseEntity.ok(detallesCotizacionesService.guardarDetallesCotizaciones(detalles));
    }


    @PostMapping("/convertir-cotizacion/{id}")
    public ResponseEntity<?> convertirCotizacion(@PathVariable Integer id) {
        Optional<Cotizaciones> cotOpt = cotizacionesRepository.findById(id);
        if (cotOpt.isEmpty()) return ResponseEntity.notFound().build();

        Cotizaciones cot = cotOpt.get();

        // 1. Crear nueva Venta
        Ventas venta = new Ventas();
        venta.setFecha_venta(LocalDate.now());
        venta.setCliente(cot.getCliente()); // Asumiendo que hay una relación
        venta.setTotal_venta(cot.getTotalCotizacion()); // O calcula el total
        venta.setCotizaciones(cot);
        venta.setNro_comrprobante(cot.getNumeroCotizacion());
        ventasRepository.save(venta);

        // 2. Copiar detalles de cotización a detalles de venta
        List<DetallesCotizaciones> detallesCot = detallesCotizacionesService.buscarPorCotizacion(cot.getIdCotizaciones());

        for (DetallesCotizaciones detCot : detallesCot) {
            DetallesVentas detVenta = new DetallesVentas();
            detVenta.setProductos(detCot.getProductos());
            detVenta.setCantidad(detCot.getCantidad());
            detVenta.setPecioUnitario(detCot.getPrecioUnitario());
            detVenta.setSubTotal(detCot.getSubTotal());
            detVenta.setVentas(venta);

            detallesVentasRepository.save(detVenta);

            // 3. Actualizar stock si corresponde
            Inventario inv = inventarioRepository.findByProductoIdproducto(detCot.getProductos().getIdproducto())
                                .orElse(null);
            if (inv != null) {
                inv.setStock(inv.getStock() - detCot.getCantidad());
                inventarioRepository.save(inv);
            }
        }
        cotizacionesRepository.save(cot);
        return ResponseEntity.ok("Cotización convertida en venta exitosamente.");
    }

    @DeleteMapping("/detalles-cotizaciones/{id}")
    public ResponseEntity<?> eliminarDetalleCotizacion(@PathVariable Integer id) {
        Optional<DetallesCotizaciones> detalleOpt = detallesCotizacionesService.buscarDetallesCotizaciones(id);
        if (detalleOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        DetallesCotizaciones detalle = detalleOpt.get();

        // Devolver el stock al inventario
        Productos producto = detalle.getProductos();
        Optional<Inventario> inventarioOpt = inventarioRepository.findByProductoIdproducto(producto.getIdproducto());

        if (inventarioOpt.isPresent()) {
            Inventario inventario = inventarioOpt.get();
            inventario.setStock(inventario.getStock() + detalle.getCantidad());
            inventarioRepository.save(inventario);
        }

        // Eliminar el detalle de cotización
        detallesCotizacionesService.eliminarDetallesCotizaciones(id);

        return ResponseEntity.ok("Detalle de cotización eliminado correctamente.");
    }

}
