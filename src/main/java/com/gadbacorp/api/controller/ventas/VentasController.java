package com.gadbacorp.api.controller.ventas;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

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
import com.gadbacorp.api.entity.inventario.Almacenes;
import com.gadbacorp.api.entity.inventario.Inventario;
import com.gadbacorp.api.entity.inventario.InventarioProducto;
import com.gadbacorp.api.entity.inventario.Productos;
import com.gadbacorp.api.entity.ventas.Clientes;
import com.gadbacorp.api.entity.ventas.Cotizaciones;
import com.gadbacorp.api.entity.ventas.DetallesVentas;
import com.gadbacorp.api.entity.ventas.DetallesVentasDTO;
import com.gadbacorp.api.entity.ventas.MetodosPago;
import com.gadbacorp.api.entity.ventas.Pagos;
import com.gadbacorp.api.entity.ventas.VentaCompletaDTO;
import com.gadbacorp.api.entity.ventas.Ventas;
import com.gadbacorp.api.entity.ventas.VentasDTO;
import com.gadbacorp.api.repository.inventario.AjusteInventarioRepository;
import com.gadbacorp.api.repository.inventario.AlmacenesRepository;
import com.gadbacorp.api.repository.inventario.InventarioProductoRepository;
import com.gadbacorp.api.repository.inventario.InventarioRepository;
import com.gadbacorp.api.repository.inventario.ProductosRepository;
import com.gadbacorp.api.repository.ventas.*;
import com.gadbacorp.api.service.ventas.IDetallesVentasService;
import com.gadbacorp.api.service.ventas.IVentasService;

import com.gadbacorp.api.messaging.VentaCreadaEvent;
import com.gadbacorp.api.messaging.VentaEventStream;
import com.gadbacorp.api.service.jpa.ventas.RegistroVentaService;

import io.swagger.v3.oas.annotations.tags.Tag;
@Tag(name = "Ventas - Ventas")
@RestController
@RequestMapping("/api/minimarket")
public class VentasController {
    @org.springframework.beans.factory.annotation.Autowired
    private com.gadbacorp.api.repository.administrable.SucursalesRepository sucursalesRepository;

    @org.springframework.beans.factory.annotation.Autowired
    private com.gadbacorp.api.service.jpa.inventario.StockService stockService;

    @Autowired
    private  PagosRepository pagosRepository;

    @Autowired
    private IVentasService ventasService;

    @Autowired
    private RegistroVentaService registroVentaService;

    @Autowired
    private com.gadbacorp.api.service.jpa.ventas.VentaPdfService ventaPdfService;

    @Autowired
    private VentaEventStream ventaEventStream;

    @Autowired
    private VentasRepository ventasRepository;

    @Autowired
    private ClientesRepository clientesRepository;
@Autowired
private InventarioRepository inventarioRepository;

    @Autowired
    private MetodosPagoRepository metodosPagoRepo;

    @Autowired
    private ProductosRepository productoRepo;

    @Autowired
    private IDetallesVentasService detallesVentasService;

    @Autowired
    private InventarioProductoRepository inventarioProductoRepository;

    @Autowired
    private DetallesVentasRepository detallesVentasRepository;
    @Autowired
    private AjusteInventarioRepository ajusteInventarioRepository;
@Autowired
private AlmacenesRepository almacenRepository;
    VentasController(PagosRepository pagosRepository) {
        this.pagosRepository = pagosRepository;
    }

    @GetMapping("/ventas")
    public List<Ventas> buscarTodos() {
        return ventasService.listarVentaas();
    }

    @GetMapping("/ventas/{id}")
    public Optional<Ventas> buscarVenta(@PathVariable Integer id) {
        return ventasService.buscarVenta(id);
    }

@PostMapping("/ventas")
public ResponseEntity<Map<String, Object>> guardarVentaCompleta(@RequestBody VentaCompletaDTO dto) {
    Map<String, Object> respuesta = new HashMap<>();
    try {
        Ventas venta = registroVentaService.registrar(dto);
        respuesta.put("status", 200);
        respuesta.put("Detalle", "Venta registrada correctamente.");
        respuesta.put("id_venta", venta.getIdVenta());
        return ResponseEntity.ok(respuesta);
    } catch (IllegalArgumentException e) {
        respuesta.put("status", 400);
        respuesta.put("Detalle", e.getMessage());
        return ResponseEntity.badRequest().body(respuesta);
    } catch (com.gadbacorp.api.excepciones.OperacionNoPermitidaException e) {
        respuesta.put("status", 409);
        respuesta.put("Detalle", e.getMessage());
        return ResponseEntity.status(409).body(respuesta);
    } catch (java.util.NoSuchElementException e) {
        respuesta.put("status", 404);
        respuesta.put("Detalle", e.getMessage());
        return ResponseEntity.status(404).body(respuesta);
    } catch (Exception e) {
        respuesta.put("status", 500);
        respuesta.put("Detalle", "Error inesperado al registrar la venta.");
        return ResponseEntity.status(500).body(respuesta);
    }
}

@GetMapping(value = "/ventas/{id}/pdf", produces = org.springframework.http.MediaType.APPLICATION_PDF_VALUE)
public ResponseEntity<byte[]> pdfVenta(@PathVariable Integer id) {
    return ventasRepository.findById(id)
            .map(venta -> {
                byte[] pdf = ventaPdfService.generar(venta, detallesVentasService.buscarPorIdVenta(id));
                return ResponseEntity.ok()
                        .header("Content-Disposition", "inline; filename=\"venta-" + id + ".pdf\"")
                        .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
                        .body(pdf);
            })
            .orElseGet(() -> ResponseEntity.notFound().build());
}

/** Ventas en tiempo real (Server-Sent Events, WebFlux/Reactor). */
@GetMapping(value = "/ventas/eventos", produces = org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE)
public reactor.core.publisher.Flux<VentaCreadaEvent> eventosVentas() {
    // cada empresa recibe solo las ventas de sus propias sucursales
    Integer empresa = com.gadbacorp.api.tenant.TenantService.empresaActual();
    if (empresa == null) {
        return ventaEventStream.flux();
    }
    java.util.Set<Integer> sucursales = sucursalesRepository.findByEmpresaIdempresa(empresa).stream()
            .map(com.gadbacorp.api.entity.administrable.Sucursales::getIdSucursal)
            .collect(java.util.stream.Collectors.toSet());
    return ventaEventStream.flux().filter(e -> sucursales.contains(e.idSucursal()));
}

@PutMapping("/ventas")
public ResponseEntity<Map<String, Object>> modificar(@RequestBody VentasDTO dto) {
    Map<String, Object> respuesta = new HashMap<>();

    if (dto.getIdVenta() == null) {
        respuesta.put("status", 400);
        respuesta.put("Detalle", "El id de la venta es obligatorio.");
        return ResponseEntity.badRequest().body(respuesta);
    }

    Optional<Ventas> ventaOpt = ventasRepository.findById(dto.getIdVenta());
    if (ventaOpt.isEmpty()) {
        respuesta.put("status", 404);
        respuesta.put("Detalle", "La venta con el id proporcionado no existe.");
        return ResponseEntity.status(404).body(respuesta);
    }

    try {
        Ventas venta = ventaOpt.get();

        venta.setEstado(dto.getEstado());
        venta.setTotal_venta(dto.getTotal_venta());
        venta.setTipo_comprobante(dto.getTipo_comprobante());
        venta.setNro_comrprobante(dto.getNro_comrprobante());
        venta.setFecha_venta(dto.getFecha_venta());
        venta.setEstado_venta(dto.getEstado_venta());

        if (dto.getId_cliente() != null) {
            Clientes cliente = clientesRepository.findById(dto.getId_cliente())
                    .orElseThrow(() -> new IllegalArgumentException("El cliente especificado no existe."));
            venta.setCliente(cliente);
        }

        ventasRepository.save(venta);

        respuesta.put("status", 200);
        respuesta.put("Detalle", "La venta se actualizó correctamente.");
        return ResponseEntity.ok(respuesta);

    } catch (IllegalArgumentException e) {
        respuesta.put("status", 400);
        respuesta.put("Detalle", e.getMessage());
        return ResponseEntity.badRequest().body(respuesta);
    } catch (Exception e) {
        respuesta.put("status", 500);
        respuesta.put("Detalle", "Error inesperado al actualizar la venta.");
        return ResponseEntity.status(500).body(respuesta);
    }
}

@DeleteMapping("/ventas/{id}")
@org.springframework.transaction.annotation.Transactional
public ResponseEntity<Map<String, Object>> eliminarVenta(@PathVariable Integer id) {
    Map<String, Object> respuesta = new HashMap<>();

    Optional<Ventas> ventaOpt = ventasRepository.findById(id);
    if (ventaOpt.isEmpty()) {
        respuesta.put("status", 404);
        respuesta.put("Detalle", "La venta no existe.");
        return ResponseEntity.status(404).body(respuesta);
    }

    Ventas venta = ventaOpt.get();
    venta.setEstado_venta("Eliminada");
    ventasRepository.save(venta);

    List<DetallesVentas> detalles = detallesVentasService.buscarPorIdVenta(id);

    for (DetallesVentas detalle : detalles) {
        stockService.mover(com.gadbacorp.api.service.jpa.inventario.StockService.sucursalDe(venta), detalle.getProductos(), detalle.cantidadEnBase(),
                com.gadbacorp.api.entity.inventario.MovimientoStock.ANULACION_VENTA, "Venta " + venta.getNro_comrprobante() + " anulada");

        detallesVentasService.eliminarDetallesVentas(detalle.getIdDetallesVenta());
    }

    // Baja logica por estado (no se borra la fila: los pagos y otras tablas siguen apuntando a la venta)
    venta.setEstado(0);
    ventasRepository.save(venta);

    respuesta.put("status", 200);
    respuesta.put("Detalle", "Venta eliminada y stock restaurado correctamente.");
    return ResponseEntity.ok(respuesta);
}

    @GetMapping("/ventas/sucursal/{idSucursal}")
public ResponseEntity<List<Ventas>> obtenerVentasPorSucursal(@PathVariable Integer idSucursal) {
    List<Ventas> ventas = ventasRepository.findByCliente_Sucursal_IdSucursal(idSucursal);
    if (ventas.isEmpty()) {
        return ResponseEntity.noContent().build();
    }
    return ResponseEntity.ok(ventas);
}

}
