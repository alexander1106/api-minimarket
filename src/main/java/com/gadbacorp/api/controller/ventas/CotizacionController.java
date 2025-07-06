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
import com.gadbacorp.api.entity.ventas.Clientes;
import com.gadbacorp.api.entity.ventas.Cotizaciones;
import com.gadbacorp.api.entity.ventas.CotizacionesDTO;
import com.gadbacorp.api.entity.ventas.DetallesCotizaciones;
import com.gadbacorp.api.repository.inventario.AjusteInventarioRepository;
import com.gadbacorp.api.repository.inventario.InventarioProductoRepository;
import com.gadbacorp.api.repository.ventas.ClientesRepository;
import com.gadbacorp.api.service.ventas.ICotizacionesService;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/minimarket")
public class CotizacionController {
    
    @Autowired
    private ICotizacionesService cotizacionesService;

    @Autowired
    private ClientesRepository clientesRepository;

    @Autowired
    private AjusteInventarioRepository ajusteInventarioRepository;

    @Autowired
    private InventarioProductoRepository inventarioProductoRepository;
    
    @GetMapping("/cotizaciones")
    public List<Cotizaciones> buscarTodos() {
        return cotizacionesService.listarCotizacioneses();
    }
    
    @GetMapping("/cotizaciones/{id}")
  public List<Cotizaciones> listarCotizacionesPendientesPorSucursal(@PathVariable Integer idSucursal) {
    return cotizacionesService.listarCotizacionesPendientesPorSucursal(idSucursal);
}
    @PostMapping("/cotizaciones")
    public ResponseEntity<?> guardarCotizacion(@RequestBody CotizacionesDTO dto) {
        Clientes cliente = clientesRepository.findById(dto.getId_cliente()).orElse(null);
        if (cliente == null) {
            return ResponseEntity.badRequest().body("Cliente no encontrado con ID: " + dto.getId_cliente());
        }
        Cotizaciones cotizaciones = new Cotizaciones();
        cotizaciones.setFechaCotizacion(dto.getFechaCotizacion());
        cotizaciones.setEstadoCotizacion(dto.getEstadoCotizacion());
        cotizaciones.setNumeroCotizacion(dto.getNumeroCotizacion());
        cotizaciones.setTotalCotizacion(dto.getTotalCotizacion());
        cotizaciones.setFechaVencimiento(dto.getFechaVencimiento());

        cotizaciones.setCliente(cliente); // Primero asigna el cliente
        return ResponseEntity.ok(cotizacionesService.guardarCotizacion(cotizaciones));
    }

    
    @PutMapping("/cotizaciones")
    public ResponseEntity<?> actualizarCotizacion(@RequestBody CotizacionesDTO dto) {
        if (dto.getIdCotizaciones() == null) {
            return ResponseEntity.badRequest().body("Debe proporcionar el ID de la cotización.");
        }

        Optional<Cotizaciones> optionalCotizacion = cotizacionesService.buscarCotizacion(dto.getIdCotizaciones());
        if (optionalCotizacion.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Cotizaciones cotizacion = optionalCotizacion.get();

        // Actualizar campos principales
        cotizacion.setFechaCotizacion(dto.getFechaCotizacion());
        cotizacion.setEstadoCotizacion(dto.getEstadoCotizacion());
        cotizacion.setNumeroCotizacion(dto.getNumeroCotizacion());
        cotizacion.setTotalCotizacion(dto.getTotalCotizacion());
        // Actualizar cliente si corresponde
        if (dto.getId_cliente() != null && 
            (cotizacion.getCliente() == null || !cotizacion.getCliente().getIdCliente().equals(dto.getId_cliente()))) {
            
            Optional<Clientes> clienteOpt = clientesRepository.findById(dto.getId_cliente());
            if (clienteOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Cliente no encontrado con ID: " + dto.getId_cliente());
            }
            cotizacion.setCliente(clienteOpt.get());
        }

        Cotizaciones actualizada = cotizacionesService.guardarCotizacion(cotizacion);
        return ResponseEntity.ok(actualizada);
    }

    @DeleteMapping("/cotizaciones/{id}")
    public ResponseEntity<?> eliminarCotizacion(@PathVariable Integer id) {
        Optional<Cotizaciones> optionalCotizacion = cotizacionesService.buscarCotizacion(id);
        if (optionalCotizacion.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Cotizaciones cotizacion = optionalCotizacion.get();
        List<DetallesCotizaciones> detalles = cotizacionesService.buscarDetallesPorCotizacion(id); // Este método debe existir

        for (DetallesCotizaciones detalle : detalles) {
            Integer productoId = detalle.getProductos().getIdproducto();
            Integer cantidad = detalle.getCantidad();

            List<InventarioProducto> inventarios = inventarioProductoRepository.findAllByProducto_Idproducto(productoId);
            for (InventarioProducto inventario : inventarios) {
                inventario.setStockactual(inventario.getStockactual() + cantidad); // Se revierte la salida del stock
                inventarioProductoRepository.save(inventario);

                AjusteInventario ajuste = new AjusteInventario();
                ajuste.setCantidad(cantidad);
                ajuste.setDescripcion("REVERTIR ELIMINACIÓN COTIZACIÓN");
                ajuste.setFechaAjuste(java.time.LocalDateTime.now());
                ajuste.setInventarioProducto(inventario);
                ajusteInventarioRepository.save(ajuste);
            }

            // Opcional: eliminar el detalle
            cotizacionesService.eliminarDetalleCotizacion(detalle.getIdDetallesCotizaciones());
        }

        cotizacionesService.eliminarCotizaciones(id);
        return ResponseEntity.ok("Cotización eliminada correctamente y stock revertido.");
    }
@GetMapping("/cotizaciones/sucursal/{idSucursal}")
public ResponseEntity<List<Cotizaciones>> listarPorSucursal(@PathVariable Integer idSucursal) {
    List<Cotizaciones> cotizaciones = cotizacionesService.findByClienteSucursal(idSucursal);
    return ResponseEntity.ok(cotizaciones);
}

}
