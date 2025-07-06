package com.gadbacorp.api.controller.ventas;

import java.time.LocalDate;
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

import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/api/minimarket")
@CrossOrigin("*")
public class VentasController {
    @Autowired
    private  PagosRepository pagosRepository;

    @Autowired
    private IVentasService ventasService;

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

@Transactional
@PostMapping("/ventas")
public void guardarVentaCompleta(@RequestBody VentaCompletaDTO dto) {
    // Validaciones mínimas
    if (dto.getId_cliente() == null) {
        throw new IllegalArgumentException("El id_cliente es obligatorio");
    }
    if (dto.getId_metodo_pago() == null) {
        throw new IllegalArgumentException("El id_metodo_pago es obligatorio");
    }
    if (dto.getIdSucursal() == null) {
        throw new IllegalArgumentException("La sucursal es obligatoria");
    }

    Clientes cliente = clientesRepository.findById(dto.getId_cliente())
            .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));

    MetodosPago metodoPago = metodosPagoRepo.findById(dto.getId_metodo_pago())
            .orElseThrow(() -> new IllegalArgumentException("Método de pago no encontrado"));

    // Crear venta
    Ventas venta = new Ventas();
    venta.setTotal_venta(dto.getTotal_venta());
    venta.setTipo_comprobante(dto.getTipo_comprobante());
    venta.setFecha_venta(LocalDate.now());
    venta.setEstado_venta("PAGADA");
    venta.setEstado(dto.getEstado());
    venta.setCliente(cliente);

    // Generar comprobante correlativo
    String tipoComprobante = dto.getTipo_comprobante();
    String prefijo = tipoComprobante.equalsIgnoreCase("FACTURA") ? "F" : "B";
   List<String> lista = ventasRepository.findUltimoComprobantePorTipo(tipoComprobante);
String ultimoNro = (lista == null || lista.isEmpty()) ? null : lista.get(0);

int nuevoNumero = 1;
if (ultimoNro != null && !ultimoNro.isEmpty()) {
    String[] partes = ultimoNro.split("-");
    if (partes.length == 2) {
        nuevoNumero = Integer.parseInt(partes[1]) + 1;
    }
}
String numeroFormateado = String.format("%s-%06d", prefijo, nuevoNumero);
venta.setNro_comrprobante(numeroFormateado);
    // Guardar venta inicial
    venta = ventasRepository.save(venta);


    ventasRepository.save(venta);

    // Preparar descuento de stock
    Integer idSucursal = dto.getIdSucursal();

    for (DetallesVentasDTO detalleDTO : dto.getDetalles()) {
        if (detalleDTO.getId_producto() == null) {
            throw new IllegalArgumentException("El id_producto en detalle es obligatorio");
        }

        Productos producto = productoRepo.findById(detalleDTO.getId_producto())
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

        // Crear detalle
        DetallesVentas detalle = new DetallesVentas();
        detalle.setVentas(venta);
        detalle.setProductos(producto);
        detalle.setFechaVenta(LocalDate.now());
        detalle.setPrecioUnitario(detalleDTO.getPecioUnitario());
        detalle.setCantidad(detalleDTO.getCantidad());
        detalle.setSubTotal(detalleDTO.getSubTotal());
        detalle.setEstado(detalleDTO.getEstado());

        detallesVentasRepository.save(detalle);

        // Descontar stock del producto en los inventarios de la sucursal
        int cantidadPorDescontar = detalleDTO.getCantidad();
        List<Almacenes> almacenes = almacenRepository.findBySucursalIdSucursal(idSucursal);

        boolean stockCubierto = false;

        for (Almacenes almacen : almacenes) {
            List<Inventario> inventarios = inventarioRepository.findByAlmacen_Idalmacen(almacen.getIdalmacen());

         for (Inventario inventario : inventarios) {
    Optional<InventarioProducto> inventarioProductoOpt = inventarioProductoRepository
        .findByProductoIdproductoAndInventarioIdinventario(
            detalleDTO.getId_producto(),
            inventario.getIdinventario()
        );

    if (inventarioProductoOpt.isPresent()) {
        InventarioProducto invProd = inventarioProductoOpt.get();
        int stockDisponible = invProd.getStockactual();

        if (stockDisponible >= cantidadPorDescontar) {
            invProd.setStockactual(stockDisponible - cantidadPorDescontar);
            inventarioProductoRepository.save(invProd);
            cantidadPorDescontar = 0;
            stockCubierto = true;
            break;
        } else if (stockDisponible > 0) {
            cantidadPorDescontar -= stockDisponible;
            invProd.setStockactual(0);
            inventarioProductoRepository.save(invProd);
        }
    } else {
        System.out.println("No se encontró InventarioProducto para producto " +
                           detalleDTO.getId_producto() + " en inventario " +
                           inventario.getIdinventario());
    }
}

            if (cantidadPorDescontar == 0) {
                break; // Salimos de almacenes
            }
        }

        if (cantidadPorDescontar > 0) {
            throw new RuntimeException("Stock insuficiente para producto ID " + detalleDTO.getId_producto());
        }
    }

    // Crear pago
    Pagos pago = new Pagos();
    pago.setVentas(venta);
    pago.setEstadoPago(dto.getEstadoPago());
    pago.setFechaPago(LocalDate.now());
    pago.setMontoPagado(dto.getMontoPagado());
    pago.setObservaciones(dto.getObservaciones());
    pago.setMetodosPago(metodoPago);

    pagosRepository.save(pago);
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

    @GetMapping("/ventas/sucursal/{idSucursal}")
public ResponseEntity<List<Ventas>> obtenerVentasPorSucursal(@PathVariable Integer idSucursal) {
    List<Ventas> ventas = ventasRepository.findByCliente_Sucursal_IdSucursal(idSucursal);
    if (ventas.isEmpty()) {
        return ResponseEntity.noContent().build();
    }
    return ResponseEntity.ok(ventas);
}

}
