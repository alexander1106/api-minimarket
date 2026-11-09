package com.gadbacorp.api.controller.ventas;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import com.gadbacorp.api.entity.ventas.Clientes;
import com.gadbacorp.api.entity.ventas.Cotizaciones;
import com.gadbacorp.api.entity.ventas.CotizacionesDTO;
import com.gadbacorp.api.entity.ventas.DetallesCotizaciones;
import com.gadbacorp.api.repository.inventario.AjusteInventarioRepository;
import com.gadbacorp.api.repository.inventario.InventarioProductoRepository;
import com.gadbacorp.api.repository.ventas.ClientesRepository;
import com.gadbacorp.api.service.ventas.ICotizacionesService;

import io.swagger.v3.oas.annotations.tags.Tag;
@Tag(name = "Ventas - Cotizaciones")
@RestController
@RequestMapping("/api/minimarket")
public class CotizacionController {
    
    @Autowired
    private ICotizacionesService cotizacionesService;

    @Autowired
    private ClientesRepository clientesRepository;

    @Autowired
    private com.gadbacorp.api.service.jpa.ventas.NumeracionService numeracionService;

    @Autowired
    private AjusteInventarioRepository ajusteInventarioRepository;

    @Autowired
    private InventarioProductoRepository inventarioProductoRepository;
    
    @GetMapping("/cotizaciones")
    public List<Cotizaciones> buscarTodos() {
        return cotizacionesService.listarCotizacioneses();
    }
    
    
    @GetMapping("/cotizaciones/{id}")
  public ResponseEntity<Cotizaciones> buscarCotizacion(@PathVariable Integer id) {
    return cotizacionesService.buscarCotizacion(id)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
}
    
@PostMapping("/cotizaciones")
public ResponseEntity<Map<String, Object>> guardarCotizacion(@RequestBody CotizacionesDTO dto) {
    Map<String, Object> respuesta = new HashMap<>();

    Clientes cliente = clientesRepository.findById(dto.getId_cliente()).orElse(null);
    if (cliente == null) {
        respuesta.put("status", 400);
        respuesta.put("Detalle", "Cliente no encontrado con ID: " + dto.getId_cliente());
        return ResponseEntity.badRequest().body(respuesta);
    }

    Cotizaciones cotizacion = new Cotizaciones();
    cotizacion.setFechaCotizacion(dto.getFechaCotizacion());
    cotizacion.setEstadoCotizacion(dto.getEstadoCotizacion());
    // El numero lo genera el servidor (correlativo unico); se ignora el que envie el cliente
    cotizacion.setNumeroCotizacion(numeracionService.siguienteCotizacion());
    cotizacion.setTotalCotizacion(dto.getTotalCotizacion());
    cotizacion.setFechaVencimiento(dto.getFechaVencimiento());
    cotizacion.setCliente(cliente);

    Cotizaciones cotizacionGuardada = cotizacionesService.guardarCotizacion(cotizacion);

    respuesta.put("status", 200);
    respuesta.put("Detalle", "Cotización guardada correctamente.");
    respuesta.put("idCotizaciones", cotizacionGuardada.getIdCotizaciones());
    respuesta.put("numeroCotizacion", cotizacionGuardada.getNumeroCotizacion());
    return ResponseEntity.ok(respuesta);
}

@PutMapping("/cotizaciones")
public ResponseEntity<Map<String, Object>> actualizarCotizacion(@RequestBody CotizacionesDTO dto) {
    Map<String, Object> respuesta = new HashMap<>();

    if (dto.getIdCotizaciones() == null) {
        respuesta.put("status", 400);
        respuesta.put("Detalle", "Debe proporcionar el ID de la cotización.");
        return ResponseEntity.badRequest().body(respuesta);
    }

    Optional<Cotizaciones> optionalCotizacion = cotizacionesService.buscarCotizacion(dto.getIdCotizaciones());
    if (optionalCotizacion.isEmpty()) {
        respuesta.put("status", 404);
        respuesta.put("Detalle", "La cotización no existe.");
        return ResponseEntity.status(404).body(respuesta);
    }

    Cotizaciones cotizacion = optionalCotizacion.get();

    cotizacion.setFechaCotizacion(dto.getFechaCotizacion());
    cotizacion.setEstadoCotizacion(dto.getEstadoCotizacion());
    cotizacion.setTotalCotizacion(dto.getTotalCotizacion());

    if (dto.getId_cliente() != null &&
        (cotizacion.getCliente() == null || !cotizacion.getCliente().getIdCliente().equals(dto.getId_cliente()))) {

        Optional<Clientes> clienteOpt = clientesRepository.findById(dto.getId_cliente());
        if (clienteOpt.isEmpty()) {
            respuesta.put("status", 400);
            respuesta.put("Detalle", "Cliente no encontrado con ID: " + dto.getId_cliente());
            return ResponseEntity.badRequest().body(respuesta);
        }
        cotizacion.setCliente(clienteOpt.get());
    }

    cotizacionesService.guardarCotizacion(cotizacion);

    respuesta.put("status", 200);
    respuesta.put("Detalle", "Cotización actualizada correctamente.");
    return ResponseEntity.ok(respuesta);
}

@DeleteMapping("/cotizaciones/{id}")
public ResponseEntity<Map<String, Object>> eliminarCotizacion(@PathVariable Integer id) {
    Map<String, Object> respuesta = new HashMap<>();

    Optional<Cotizaciones> optionalCotizacion = cotizacionesService.buscarCotizacion(id);
    if (optionalCotizacion.isEmpty()) {
        respuesta.put("status", 404);
        respuesta.put("Detalle", "La cotización no existe.");
        return ResponseEntity.status(404).body(respuesta);
    }

    Cotizaciones cotizacion = optionalCotizacion.get();
    List<DetallesCotizaciones> detalles = cotizacionesService.buscarDetallesPorCotizacion(id);

    for (DetallesCotizaciones detalle : detalles) {
        cotizacionesService.eliminarDetalleCotizacion(detalle.getIdDetallesCotizaciones());
    }

    cotizacionesService.eliminarCotizaciones(id);

    respuesta.put("status", 200);
    respuesta.put("Detalle", "Cotización eliminada correctamente.");
    return ResponseEntity.ok(respuesta);
}

    @GetMapping("/cotizaciones/sucursal/{idSucursal}")
public ResponseEntity<List<Cotizaciones>> listarPorSucursal(@PathVariable Integer idSucursal) {
    List<Cotizaciones> cotizaciones = cotizacionesService.findByClienteSucursal(idSucursal);
    return ResponseEntity.ok(cotizaciones);
}

}
