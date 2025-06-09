package com.gadbacorp.api.gadbacorp.controller.ventas;

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

import com.gadbacorp.api.gadbacorp.entity.inventario.Productos;
import com.gadbacorp.api.gadbacorp.entity.ventas.DetallesVentas;
import com.gadbacorp.api.gadbacorp.entity.ventas.DetallesVentasDTO;
import com.gadbacorp.api.gadbacorp.entity.ventas.Ventas;
import com.gadbacorp.api.gadbacorp.repository.inventario.ProductosRepository;
import com.gadbacorp.api.gadbacorp.repository.ventas.VentasRepository;
import com.gadbacorp.api.gadbacorp.service.ventas.IDetallesVentasService;

@RestController
@RequestMapping("/api/minimarket")
public class DetallesVentasController {
    @Autowired
    private IDetallesVentasService detallesVentasService;

    @Autowired
    private VentasRepository ventasRepository;

    @Autowired
    private ProductosRepository productosRepository;

    @GetMapping("/detalles-ventas")
    public List<DetallesVentas> buscarTodos() {
        return detallesVentasService.listDetallesVentas();
    }

    @PostMapping("/detalles-venta")
    public ResponseEntity<?> guardarDetallesVentas(@RequestBody DetallesVentasDTO dto) {
        Productos producto = productosRepository.findById(dto.getId_producto()).orElse(null);
        if (producto == null) {
            return ResponseEntity.badRequest().body("Producto no encontrado con ID: " + dto.getId_producto());
        }
          Ventas venta = ventasRepository.findById(dto.getId_venta()).orElse(null);
        if (venta == null) {
            return ResponseEntity.badRequest().body("Venta no encontrado con ID: " + dto.getId_producto());
        }
        DetallesVentas detallesVentas = new DetallesVentas();
        detallesVentas.setTipoComprobante(dto.getTipoComprobante());
        detallesVentas.setNumeroComprobante(dto.getNumeroComprobante());
        detallesVentas.setFechaVenta(dto.getFechaVenta());
        detallesVentas.setCantidad(dto.getCantidad());
        detallesVentas.setSubTotal(dto.getSubTotal());
        detallesVentas.setProductos(producto);
        detallesVentas.setVentas(venta);
        return ResponseEntity.ok(detallesVentasService.guardarDetallesVentas(detallesVentas));
    }


    @PutMapping("/detalles-venta")
    public ResponseEntity <?> modificar(@RequestBody DetallesVentasDTO dto) {
        if(dto.getIdDetallesVenta() == null){
            return ResponseEntity.badRequest().body("Id no existe");
        }

        
        DetallesVentas detallesVentas = new DetallesVentas();
        detallesVentas.setTipoComprobante(dto.getTipoComprobante());
        detallesVentas.setNumeroComprobante(dto.getNumeroComprobante());
        detallesVentas.setFechaVenta(dto.getFechaVenta());
        detallesVentas.setCantidad(dto.getCantidad());
        detallesVentas.setSubTotal(dto.getSubTotal());
        detallesVentas.setProductos(new Productos(dto.getId_producto()));
        detallesVentas.setVentas(new Ventas(dto.getId_venta()));
        return ResponseEntity.ok(detallesVentasService.editarDetallesVentas(detallesVentas));
    }
    
   
    // Eliminar venta
    @DeleteMapping("/detalles-venta/{id}")
    public String eliminarVenta(@PathVariable Integer id) {
        detallesVentasService.eliminarDetallesVentas(id);
        return "El detalle venta a sido eliminada con exito";
    }

    // Buscar venta por ID
    @GetMapping("/detalles-venta/{id}")
    public Optional<DetallesVentas> buscarVenta(@PathVariable Integer id) {
        return detallesVentasService.buscarDetallesVentas(id);
    }

}
