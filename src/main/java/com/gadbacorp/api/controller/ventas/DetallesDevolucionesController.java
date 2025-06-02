package com.gadbacorp.api.controller.ventas;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gadbacorp.api.entity.inventario.Inventario;
import com.gadbacorp.api.entity.inventario.Productos;
import com.gadbacorp.api.entity.ventas.DetallesCotizaciones;
import com.gadbacorp.api.entity.ventas.DetallesDevolucion;
import com.gadbacorp.api.entity.ventas.DetallesDevolucionDTO;
import com.gadbacorp.api.entity.ventas.Devoluciones;
import com.gadbacorp.api.entity.ventas.Ventas;
import com.gadbacorp.api.repository.inventario.InventarioRepository;
import com.gadbacorp.api.repository.inventario.ProductosRepository;
import com.gadbacorp.api.repository.ventas.DevolucionesRepository;
import com.gadbacorp.api.repository.ventas.VentasRepository;
import com.gadbacorp.api.service.ventas.IDetallesDevoluciones;

@RestController
@RequestMapping("/api/minimarket")
public class DetallesDevolucionesController {
    
    @Autowired
    private IDetallesDevoluciones detallesDevoluciones;
    
    @Autowired
    private DevolucionesRepository devolucionesRepository;

    @Autowired
    private InventarioRepository inventarioRepository;

    @Autowired
    private ProductosRepository productosRepository;

    @GetMapping("/detalles-devoluciones")
    public List<DetallesDevolucion> listarDevoluciones() {
        return detallesDevoluciones.listarDetallesDevoluciones();
    }

    @GetMapping("/detalles-devoluciones/{id}")
    public Optional<DetallesDevolucion> buscarDetallesDevoluciones(@PathVariable Integer id) {
        return detallesDevoluciones.buscarDetalleDevolucion(id);
    }

    @PostMapping("/detalles-devoluciones")
    public ResponseEntity<?> guardarDetallesDevolucion(@RequestBody DetallesDevolucionDTO dto) {
        Productos producto = productosRepository.findById(dto.getId_producto()).orElse(null);
        if (producto == null) {
            return ResponseEntity.badRequest().body("Producto no encontrado con ID: " + dto.getId_producto());
        }

        Devoluciones devolucion = devolucionesRepository.findById(dto.getId_devolucion()).orElse(null);
        if (devolucion == null) {
            return ResponseEntity.badRequest().body("Devolucion no encontrada con ID: " + dto.getId_devolucion());
        }

        Optional<Inventario> inventarioOpt = inventarioRepository.findByProductoIdproducto(dto.getId_producto());
        if (inventarioOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Inventario no encontrado para el producto ID: " + dto.getId_producto());
        }

        Inventario inventario = inventarioOpt.get();
        inventario.setStock(inventario.getStock() + dto.getCantidad()); // Aumentar el stock por la devolución
        inventarioRepository.save(inventario);

        // Guardar detalle de devolución
        DetallesDevolucion detallesDevolucion = new DetallesDevolucion();
        detallesDevolucion.setCantidad(dto.getCantidad());
        detallesDevolucion.setPecioUnitario(dto.getPecioUnitario());
        detallesDevolucion.setSubTotal(dto.getSubTotal());
        detallesDevolucion.setProductos(producto);
        detallesDevolucion.setDevoluciones(devolucion);

        return ResponseEntity.ok(detallesDevoluciones.guardarDetallesDevolucion(detallesDevolucion));
    }


    @DeleteMapping("/detalles-devoluciones/{id}")
    public String eliminarDevoluciones(@PathVariable Integer id){
        detallesDevoluciones.eliminarDetallesCotizaciones(id);
        return "El detalle de la devolucion a sido eliminada con exito";
    } 

}
