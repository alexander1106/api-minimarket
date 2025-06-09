package com.gadbacorp.api.controller.ventas;

import java.time.LocalDateTime;
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
import com.gadbacorp.api.entity.ventas.DetallesVentas;
import com.gadbacorp.api.entity.ventas.Ventas;
import com.gadbacorp.api.entity.ventas.VentasDTO;
import com.gadbacorp.api.repository.inventario.AjusteInventarioRepository;
import com.gadbacorp.api.repository.inventario.InventarioProductoRepository;
import com.gadbacorp.api.repository.ventas.ClientesRepository;
import com.gadbacorp.api.repository.ventas.CotizacionesRepository;
import com.gadbacorp.api.repository.ventas.VentasRepository;
import com.gadbacorp.api.service.ventas.IDetallesVentasService;
import com.gadbacorp.api.service.ventas.IVentasService;

@RestController
@RequestMapping("/api/minimarket")
@CrossOrigin("*")
public class VentasController {

    @Autowired
    private IVentasService ventasService;

    @Autowired
    private VentasRepository ventasRepository;
@Autowired
private CotizacionesRepository cotizacionesRepository;
    @Autowired
    private ClientesRepository clientesRepository;

    @Autowired
    private IDetallesVentasService detallesVentasService;

    @Autowired
    private InventarioProductoRepository inventarioProductoRepository;

    @Autowired
    private AjusteInventarioRepository ajusteInventarioRepository;

    @GetMapping("/ventas")
    public List<Ventas> buscarTodos() {
        return ventasService.listarVentaas();
    }

    @GetMapping("/ventas/{id}")
    public Optional<Ventas> buscarVenta(@PathVariable Integer id) {
        return ventasService.buscarVenta(id);
    }

    @PostMapping("/ventas")
    public ResponseEntity<?> guardarVenta(@RequestBody VentasDTO dto) {
        Clientes cliente = clientesRepository.findById(dto.getId_cliente()).orElse(null);
        if (cliente == null) {
            return ResponseEntity.badRequest().body("Cliente no encontrado con ID: " + dto.getId_cliente());
        }
        Cotizaciones cotizaciones = cotizacionesRepository.findById(dto.getId_cotizacion()).orElse(null);
       

        Ventas venta = new Ventas();
        venta.setTotal_venta(dto.getTotal_venta());
        venta.setTipo_comprobante(dto.getTipo_comprobante());
        venta.setNro_comrprobante(dto.getNro_comrprobante());
        venta.setEstado_venta(dto.getEstado_venta());
        venta.setFecha_venta(dto.getFecha_venta());
        venta.setEstado(dto.getEstado());
        venta.setCliente(cliente);
        venta.setCotizaciones(cotizaciones);

        return ResponseEntity.ok(ventasService.guardarVenta(venta));
    }

    @PutMapping("/ventas")
    public ResponseEntity<?> modificar(@RequestBody VentasDTO dto) {
        if (dto.getIdVenta() == null) {
            return ResponseEntity.badRequest().body("Id no existe");
        }
        
        Ventas venta = new Ventas();
        venta.setIdVenta(dto.getIdVenta());
        venta.setEstado(dto.getEstado());
        venta.setTotal_venta(dto.getTotal_venta());
        venta.setTipo_comprobante(dto.getTipo_comprobante());
        venta.setNro_comrprobante(dto.getNro_comrprobante());
        venta.setFecha_venta(dto.getFecha_venta());
        venta.setEstado_venta(dto.getEstado_venta());
        venta.setCliente(new Clientes(dto.getId_cliente()));

        return ResponseEntity.ok(ventasService.editarVenta(venta));
    }

    @DeleteMapping("/ventas/{id}")
    public ResponseEntity<?> eliminarVenta(@PathVariable Integer id) {
        Optional<Ventas> ventaOpt = ventasRepository.findById(id);
        if (ventaOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Ventas venta = ventaOpt.get();
        venta.setEstado_venta("Eliminada");
        List<DetallesVentas> detalles = detallesVentasService.buscarPorIdVenta(id);

        for (DetallesVentas detalle : detalles) {
            Optional<InventarioProducto> inventarioOpt = inventarioProductoRepository
                    .findFirstByProducto_Idproducto(detalle.getProductos().getIdproducto());

            if (inventarioOpt.isPresent()) {
                InventarioProducto inventario = inventarioOpt.get();
                inventario.setStockactual(inventario.getStockactual() + detalle.getCantidad());
                inventarioProductoRepository.save(inventario);

                AjusteInventario ajuste = new AjusteInventario();
                ajuste.setCantidad(detalle.getCantidad());
                ajuste.setDescripcion("AJUSTE POR ELIMINACIÓN DE VENTA");
                ajuste.setFechaAjuste(LocalDateTime.now());
                ajuste.setInventarioProducto(inventario);
                ajusteInventarioRepository.save(ajuste);
            }

            detallesVentasService.eliminarDetallesVentas(detalle.getIdDetallesVenta());
        }

        ventasService.eliminarVenta(id);
        return ResponseEntity.ok().body("Venta eliminada y stock restaurado correctamente.");
    }
}
