package com.gadbacorp.api.config;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import com.gadbacorp.api.entity.administrable.Empresas;
import com.gadbacorp.api.entity.administrable.Sucursales;
import com.gadbacorp.api.entity.caja.AperturaCaja;
import com.gadbacorp.api.entity.caja.ArqueoCajaDTO;
import com.gadbacorp.api.entity.caja.Caja;
import com.gadbacorp.api.entity.caja.TransferenciasEntreCajas;
import com.gadbacorp.api.entity.compras.Compras;
import com.gadbacorp.api.entity.compras.DetallesCompras;
import com.gadbacorp.api.entity.compras.DetallesDevolucionCompra;
import com.gadbacorp.api.entity.compras.DevolucionesCompra;
import com.gadbacorp.api.entity.compras.Proveedores;
import com.gadbacorp.api.entity.delivery.Delivery;
import com.gadbacorp.api.entity.delivery.DeliveryDTO;
import com.gadbacorp.api.entity.delivery.EstadoMovimiento;
import com.gadbacorp.api.entity.delivery.Vehiculo;
import com.gadbacorp.api.entity.empleados.Usuarios;
import com.gadbacorp.api.entity.inventario.Inventario;
import com.gadbacorp.api.entity.inventario.InventarioProducto;
import com.gadbacorp.api.entity.inventario.MovimientoStock;
import com.gadbacorp.api.entity.inventario.ProductoPresentacion;
import com.gadbacorp.api.entity.inventario.Productos;
import com.gadbacorp.api.entity.inventario.Promociones;
import com.gadbacorp.api.entity.inventario.TrasladoInventarioProductoDTO;
import com.gadbacorp.api.entity.ventas.Clientes;
import com.gadbacorp.api.entity.ventas.Cotizaciones;
import com.gadbacorp.api.entity.ventas.DetallesCotizaciones;
import com.gadbacorp.api.entity.ventas.DetallesDevolucion;
import com.gadbacorp.api.entity.ventas.DetallesVentas;
import com.gadbacorp.api.entity.ventas.DetallesVentasDTO;
import com.gadbacorp.api.entity.ventas.Devoluciones;
import com.gadbacorp.api.entity.ventas.MetodosPago;
import com.gadbacorp.api.entity.ventas.Ventas;
import com.gadbacorp.api.entity.ventas.VentaCompletaDTO;
import com.gadbacorp.api.repository.caja.AperturaCajaRepository;
import com.gadbacorp.api.repository.caja.CajaRepository;
import com.gadbacorp.api.repository.caja.TransferenciaEntreCajasRepository;
import com.gadbacorp.api.repository.compras.DetallesDevolucionCompraRepository;
import com.gadbacorp.api.repository.compras.DevolucionesCompraRepository;
import com.gadbacorp.api.repository.compras.ProveedoresRepository;
import com.gadbacorp.api.repository.delivery.DeliveryRepository;
import com.gadbacorp.api.repository.delivery.EstadoMovimientoRepository;
import com.gadbacorp.api.repository.delivery.VehiculoRepository;
import com.gadbacorp.api.repository.empleados.UsuarioRepository;
import com.gadbacorp.api.repository.inventario.CategoriasRepository;
import com.gadbacorp.api.repository.inventario.InventarioProductoRepository;
import com.gadbacorp.api.repository.inventario.InventarioRepository;
import com.gadbacorp.api.repository.inventario.ProductoPresentacionRepository;
import com.gadbacorp.api.repository.inventario.ProductosRepository;
import com.gadbacorp.api.repository.inventario.PromocionesRepository;
import com.gadbacorp.api.repository.inventario.TipoProductoRepository;
import com.gadbacorp.api.repository.inventario.UnidadDeMedidaRepository;
import com.gadbacorp.api.repository.ventas.ClientesRepository;
import com.gadbacorp.api.repository.ventas.CotizacionesRepository;
import com.gadbacorp.api.repository.ventas.DetallesCotizacionesRepository;
import com.gadbacorp.api.repository.ventas.DetallesDevolucionesRepository;
import com.gadbacorp.api.repository.ventas.DetallesVentasRepository;
import com.gadbacorp.api.repository.ventas.DevolucionesRepository;
import com.gadbacorp.api.repository.ventas.MetodosPagoRepository;
import com.gadbacorp.api.repository.ventas.VentasRepository;
import com.gadbacorp.api.service.compras.IComprasService;
import com.gadbacorp.api.service.compras.IDetallesComprasService;
import com.gadbacorp.api.service.jpa.cajas.CajaMovimientoService;
import com.gadbacorp.api.service.jpa.cajas.CierreCajaService;
import com.gadbacorp.api.service.jpa.inventario.StockService;
import com.gadbacorp.api.service.jpa.inventario.TrasladoInventarioProductoService;
import com.gadbacorp.api.service.jpa.ventas.NumeracionService;
import com.gadbacorp.api.service.jpa.ventas.RegistroVentaService;

/**
 * Amplia los datos de demostracion para recorrer TODO el flujo del sistema con datos: mas productos, compras con lotes,
 * cajas abiertas y cerradas con sus movimientos y arqueo, ventas pagadas en efectivo y Yape, envios (delivery),
 * devoluciones de venta y de compra, traslados entre inventarios, cotizaciones y promociones.
 * Usa los mismos servicios que la API. Corre una sola vez (marca: el producto FRU-001) y despues de {@link DatosDemoRunner}.
 */
@Component
@Order(40)
public class DatosDemoAmpliadoRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DatosDemoAmpliadoRunner.class);

    private final boolean habilitado;
    private final String empresaUsername;
    private final PlatformTransactionManager tx;
    private final UsuarioRepository usuarios;
    private final ProductosRepository productos;
    private final ProductoPresentacionRepository presentaciones;
    private final CategoriasRepository categorias;
    private final TipoProductoRepository tipos;
    private final UnidadDeMedidaRepository unidades;
    private final InventarioRepository inventarios;
    private final InventarioProductoRepository stocks;
    private final ProveedoresRepository proveedores;
    private final ClientesRepository clientes;
    private final MetodosPagoRepository metodosPago;
    private final IComprasService compras;
    private final IDetallesComprasService detallesCompras;
    private final RegistroVentaService registroVenta;
    private final VentasRepository ventas;
    private final DetallesVentasRepository detallesVentas;
    private final CajaRepository cajas;
    private final AperturaCajaRepository aperturas;
    private final CajaMovimientoService cajaMovimientos;
    private final CierreCajaService cierreCaja;
    private final TransferenciaEntreCajasRepository transferencias;
    private final VehiculoRepository vehiculos;
    private final EstadoMovimientoRepository estadosMovimiento;
    private final DeliveryRepository deliveries;
    private final DevolucionesRepository devoluciones;
    private final DetallesDevolucionesRepository detallesDevoluciones;
    private final DevolucionesCompraRepository devolucionesCompra;
    private final DetallesDevolucionCompraRepository detallesDevolucionCompra;
    private final TrasladoInventarioProductoService traslados;
    private final PromocionesRepository promociones;
    private final CotizacionesRepository cotizaciones;
    private final DetallesCotizacionesRepository detallesCotizaciones;
    private final NumeracionService numeracion;
    private final StockService stockService;

    public DatosDemoAmpliadoRunner(@Value("${app.seed.enabled:true}") boolean semilla,
                                   @Value("${app.seed.demo:true}") boolean demo,
                                   @Value("${app.seed.empresa-username:empresa}") String empresaUsername,
                                   PlatformTransactionManager tx, UsuarioRepository usuarios, ProductosRepository productos,
                                   ProductoPresentacionRepository presentaciones, CategoriasRepository categorias,
                                   TipoProductoRepository tipos, UnidadDeMedidaRepository unidades,
                                   InventarioRepository inventarios, InventarioProductoRepository stocks,
                                   ProveedoresRepository proveedores, ClientesRepository clientes,
                                   MetodosPagoRepository metodosPago, IComprasService compras,
                                   IDetallesComprasService detallesCompras, RegistroVentaService registroVenta,
                                   VentasRepository ventas, DetallesVentasRepository detallesVentas,
                                   CajaRepository cajas, AperturaCajaRepository aperturas,
                                   CajaMovimientoService cajaMovimientos, CierreCajaService cierreCaja,
                                   TransferenciaEntreCajasRepository transferencias, VehiculoRepository vehiculos,
                                   EstadoMovimientoRepository estadosMovimiento, DeliveryRepository deliveries,
                                   DevolucionesRepository devoluciones, DetallesDevolucionesRepository detallesDevoluciones,
                                   DevolucionesCompraRepository devolucionesCompra,
                                   DetallesDevolucionCompraRepository detallesDevolucionCompra,
                                   TrasladoInventarioProductoService traslados, PromocionesRepository promociones,
                                   CotizacionesRepository cotizaciones, DetallesCotizacionesRepository detallesCotizaciones,
                                   NumeracionService numeracion, StockService stockService) {
        this.habilitado = semilla && demo;
        this.empresaUsername = empresaUsername;
        this.tx = tx;
        this.usuarios = usuarios;
        this.productos = productos;
        this.presentaciones = presentaciones;
        this.categorias = categorias;
        this.tipos = tipos;
        this.unidades = unidades;
        this.inventarios = inventarios;
        this.stocks = stocks;
        this.proveedores = proveedores;
        this.clientes = clientes;
        this.metodosPago = metodosPago;
        this.compras = compras;
        this.detallesCompras = detallesCompras;
        this.registroVenta = registroVenta;
        this.ventas = ventas;
        this.detallesVentas = detallesVentas;
        this.cajas = cajas;
        this.aperturas = aperturas;
        this.cajaMovimientos = cajaMovimientos;
        this.cierreCaja = cierreCaja;
        this.transferencias = transferencias;
        this.vehiculos = vehiculos;
        this.estadosMovimiento = estadosMovimiento;
        this.deliveries = deliveries;
        this.devoluciones = devoluciones;
        this.detallesDevoluciones = detallesDevoluciones;
        this.devolucionesCompra = devolucionesCompra;
        this.detallesDevolucionCompra = detallesDevolucionCompra;
        this.traslados = traslados;
        this.promociones = promociones;
        this.cotizaciones = cotizaciones;
        this.detallesCotizaciones = detallesCotizaciones;
        this.numeracion = numeracion;
        this.stockService = stockService;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!habilitado) {
            return;
        }
        for (Usuarios usuario : DatosDemoRunner.candidatos(usuarios)) {
            try {
                new TransactionTemplate(tx).executeWithoutResult(s -> cargar(usuario));
            } catch (RuntimeException e) {
                log.warn("Seed demo ampliado: no se pudo completar para {} ({}). La aplicacion arranca igual.",
                        usuario.getUsername(), e.getMessage());
            }
        }
    }

    /** Empresa que se esta cargando: los codigos de producto se buscan solo dentro de ella. */
    private Integer empresaId;
    private Integer sucursalId;

    private Productos porCodigo(String codigo) {
        return productos.findByCodigoIgnoreCaseAndEmpresa_Idempresa(codigo, empresaId).orElse(null);
    }

    private void cargar(Usuarios usuario) {
        if (usuario.getSucursal() == null || usuario.getSucursal().getEmpresa() == null) {
            return;
        }
        empresaId = usuario.getSucursal().getEmpresa().getIdempresa();
        sucursalId = usuario.getSucursal().getIdSucursal();
        // necesita los datos base de DatosDemoRunner y no repetirse
        if (porCodigo("ARR-001") == null || porCodigo("FRU-001") != null) {
            return;
        }
        Sucursales sucursal = usuario.getSucursal();
        Empresas empresa = sucursal.getEmpresa();
        MetodosPago efectivo = metodo(sucursal, "Efectivo");
        MetodosPago yape = metodo(sucursal, "Yape");
        MetodosPago tarjeta = metodo(sucursal, "Tarjeta");
        if (efectivo == null || yape == null || tarjeta == null) {
            log.warn("Seed demo ampliado: faltan metodos de pago en la sucursal; se omite.");
            return;
        }
        List<Proveedores> provs = proveedores.findAll().stream()
                .filter(p -> p.getEmpresa() != null && empresaId.equals(p.getEmpresa().getIdempresa())).toList();
        Proveedores andina = provs.stream().filter(p -> p.getNombre().startsWith("Distribuidora")).findFirst().orElse(provs.get(0));
        Proveedores lacteos = provs.stream().filter(p -> p.getNombre().startsWith("Lácteos")).findFirst().orElse(andina);
        List<Clientes> cl = clientes.findAll().stream()
                .filter(c -> c.getSucursal() != null && sucursalId.equals(c.getSucursal().getIdSucursal())).toList();
        Clientes varios = cl.stream().filter(c -> "Cliente varios".equals(c.getNombre())).findFirst().orElse(cl.get(0));
        Clientes ana = cl.stream().filter(c -> c.getNombre().startsWith("Ana")).findFirst().orElse(varios);
        Clientes bodega = cl.stream().filter(c -> c.getNombre().startsWith("Bodega")).findFirst().orElse(varios);

        crearProductos(empresa);
        comprarPerecibles(sucursal, lacteos, efectivo);
        comprarAbarrotes(sucursal, andina, efectivo);
        java.util.Optional.ofNullable(porCodigo("LIM-001")).ifPresent(p -> {   // detergente: queda bajo el minimo
            p.setStockMinimo(new BigDecimal("25"));
            productos.save(p);
        });

        trasladarAAnaqueles();
        crearVehiculosYEstados();
        cajas(sucursal, usuario, efectivo, yape, tarjeta, varios, ana, bodega);
        devolucionDeVenta(sucursal);
        devolucionACompra(sucursal);
        cotizaciones(ana, bodega);
        promociones();
        log.info("Seed demo ampliado: catalogo de {} productos con compras, cajas, ventas, envios, devoluciones, traslados y promociones.",
                productos.count());
    }

    private MetodosPago metodo(Sucursales sucursal, String nombre) {
        return metodosPago.findBySucursalIdSucursal(sucursal.getIdSucursal()).stream()
                .filter(m -> nombre.equalsIgnoreCase(m.getNombre())).findFirst().orElse(null);
    }

    /** {codigo, nombre, marca, categoria, tipo, unidad, costo, venta, mayor, minimo, presentacion, factor, p.venta, p.compra} */
    private static final Object[][] EXTRA = {
            {"FRU-001", "Plátano de seda", "", "Frutas y verduras", "Perecible", "KG", "1.80", "2.50", "2.20", "20", null, null, null, null},
            {"FRU-002", "Manzana delicia", "", "Frutas y verduras", "Perecible", "KG", "4.00", "5.50", "5.00", "15", null, null, null, null},
            {"FRU-003", "Naranja de jugo", "", "Frutas y verduras", "Perecible", "KG", "2.20", "3.00", "2.70", "20", null, null, null, null},
            {"VER-001", "Tomate", "", "Frutas y verduras", "Perecible", "KG", "2.50", "3.50", "3.10", "15", null, null, null, null},
            {"VER-002", "Cebolla roja", "", "Frutas y verduras", "Perecible", "KG", "2.00", "2.90", "2.60", "20", null, null, null, null},
            {"VER-003", "Papa blanca", "", "Frutas y verduras", "No perecible", "KG", "1.60", "2.20", "2.00", "30", null, null, null, null},
            {"CAR-001", "Pollo entero", "San Fernando", "Carnes y embutidos", "Perecible", "KG", "9.00", "11.50", "10.80", "15", null, null, null, null},
            {"EMB-001", "Jamón del país", "Braedt", "Carnes y embutidos", "Perecible", "KG", "22.00", "29.00", "27.00", "3", null, null, null, null},
            {"EMB-002", "Hot dog x8", "Otto Kunz", "Carnes y embutidos", "Perecible", "UND", "6.50", "8.90", "8.20", "6", null, null, null, null},
            {"PAN-001", "Pan francés", "", "Panadería", "Perecible", "UND", "0.20", "0.30", "0.25", "100", "Bolsa x50", "50", "14.00", "9.00"},
            {"PAN-002", "Pan de molde", "Bimbo", "Panadería", "Perecible", "UND", "4.50", "6.50", "6.00", "10", null, null, null, null},
            {"CUI-001", "Shampoo 400 ml", "Head & Shoulders", "Cuidado personal", "No perecible", "UND", "11.00", "15.90", "14.50", "6", "Caja x12", "12", "170.00", "130.00"},
            {"CUI-002", "Jabón de tocador x3", "Protex", "Cuidado personal", "No perecible", "UND", "5.50", "7.90", "7.20", "12", null, null, null, null},
            {"CUI-003", "Papel higiénico x4", "Suave", "Cuidado personal", "No perecible", "UND", "6.00", "8.50", "7.80", "12", "Bolsa x12", "12", "95.00", "72.00"},
            {"CUI-004", "Pasta dental 90 g", "Colgate", "Cuidado personal", "No perecible", "UND", "3.80", "5.50", "5.00", "12", null, null, null, null},
            {"ABA-001", "Fideos spaghetti 500 g", "Don Vittorio", "Abarrotes", "No perecible", "UND", "2.60", "3.50", "3.20", "24", "Caja x20", "20", "62.00", "50.00"},
            {"ABA-002", "Atún en lata", "Florida", "Abarrotes", "No perecible", "UND", "4.20", "5.50", "5.10", "24", "Caja x24", "24", "125.00", "100.00"},
            {"ABA-003", "Harina de trigo", "Blanca Flor", "Abarrotes", "No perecible", "KG", "2.80", "3.80", "3.50", "50", "Saco 50 kg", "50", "175.00", "138.00"},
            {"ABA-004", "Sal de mesa 1 kg", "Emsal", "Abarrotes", "No perecible", "UND", "1.20", "1.80", "1.60", "12", null, null, null, null},
            {"ABA-005", "Café instantáneo 50 g", "Nescafé", "Abarrotes", "No perecible", "UND", "12.00", "16.00", "14.80", "6", null, null, null, null},
            {"BEB-003", "Cerveza 620 ml", "Pilsen", "Bebidas", "No perecible", "UND", "4.20", "6.00", "5.50", "24", "Caja x12", "12", "68.00", "50.00"},
            {"BEB-004", "Jugo de naranja 1 L", "Frugos", "Bebidas", "Perecible", "UND", "3.80", "5.20", "4.80", "12", "Paquete x6", "6", "29.00", "22.00"},
    };

    private void crearProductos(Empresas empresa) {
        for (Object[] d : EXTRA) {
            Productos p = new Productos();
            p.setCodigo((String) d[0]);
            p.setNombre((String) d[1]);
            p.setMarca(((String) d[2]).isEmpty() ? null : (String) d[2]);
            p.setCodigoBarras("77500020" + String.format("%05d", Math.abs(((String) d[0]).hashCode()) % 100000));
            p.setCategoria(categorias.findByNombreIgnoreCase((String) d[3]).orElse(null));
            p.setTipoProducto(tipos.findAll().stream().filter(t -> d[4].equals(t.getNombre())).findFirst().orElse(null));
            p.setUnidadMedida(unidades.findAll().stream().filter(u -> d[5].equals(u.getCodigo())).findFirst().orElse(null));
            p.setCostoCompra(new BigDecimal((String) d[6]));
            p.setCostoVenta(new BigDecimal((String) d[7]));
            p.setCostoMayor(new BigDecimal((String) d[8]));
            p.setStockMinimo(new BigDecimal((String) d[9]));
            p.setAfectoIgv(!"VER-003".equals(d[0]) && !"FRU-001".equals(d[0]) && !"FRU-002".equals(d[0])
                    && !"FRU-003".equals(d[0]) && !"VER-001".equals(d[0]) && !"VER-002".equals(d[0]));   // frutas y verduras: inafectas
            p.setEstado(1);
            p.setEmpresa(empresa);
            p = productos.save(p);
            if (d[10] != null) {
                ProductoPresentacion pr = new ProductoPresentacion();
                pr.setProducto(p);
                pr.setNombre((String) d[10]);
                pr.setFactor(new BigDecimal((String) d[11]));
                pr.setPrecioVenta(new BigDecimal((String) d[12]));
                pr.setPrecioCompra(new BigDecimal((String) d[13]));
                pr.setEstado(1);
                presentaciones.save(pr);
            }
        }
    }

    private Integer idPresentacion(Productos p) {
        return presentaciones.findAll().stream()
                .filter(x -> x.getProducto() != null && x.getProducto().getIdproducto().equals(p.getIdproducto()))
                .map(ProductoPresentacion::getIdPresentacion).findFirst().orElse(null);
    }

    private Compras nuevaCompra(Sucursales sucursal, Proveedores proveedor, MetodosPago pago, String descripcion) {
        Compras compra = new Compras();
        compra.setProveedor(proveedor);
        compra.setMetodoPago(pago);
        compra.setSucursal(sucursal);
        compra.setFechaCompra(LocalDateTime.now());
        compra.setDescripcion(descripcion);
        compra.setEstado(1);
        compra.setTotal(BigDecimal.ZERO);
        return compras.guardarCompra(compra);
    }

    private void linea(Compras compra, String codigo, String cantidad, String precioCompra, String precioVenta,
                       String lote, Integer diasVence) {
        Productos producto = porCodigo(codigo);
        DetallesCompras d = new DetallesCompras();
        d.setCantidad(new BigDecimal(cantidad));
        d.setIdPresentacionSolicitada(idPresentacion(producto));
        d.setPrecioUnitario(new BigDecimal(precioCompra));
        d.setPrecioCompra(new BigDecimal(precioCompra));
        d.setPrecioVenta(new BigDecimal(precioVenta));
        d.setLote(lote);
        d.setFechaVencimiento(diasVence == null ? null : LocalDate.now().plusDays(diasVence));
        detallesCompras.guardarDetalle(d, compra.getIdCompra(), producto.getIdproducto());
    }

    private void comprarPerecibles(Sucursales sucursal, Proveedores proveedor, MetodosPago pago) {
        Compras c = nuevaCompra(sucursal, proveedor, pago, "Compra de frescos y perecibles");
        linea(c, "FRU-001", "40", "1.80", "2.50", "PLA-01", 6);
        linea(c, "FRU-002", "30", "4.00", "5.50", "MAN-01", 14);
        linea(c, "FRU-003", "35", "2.20", "3.00", "NAR-01", 10);
        linea(c, "VER-001", "25", "2.50", "3.50", "TOM-01", 7);
        linea(c, "VER-002", "30", "2.00", "2.90", "CEB-01", 20);
        linea(c, "VER-003", "80", "1.60", "2.20", null, null);
        linea(c, "CAR-001", "30", "9.00", "11.50", "POL-01", 4);
        linea(c, "EMB-001", "6", "22.00", "29.00", "JAM-01", 18);
        linea(c, "EMB-002", "18", "6.50", "8.90", "HOT-01", 25);
        linea(c, "PAN-001", "3", "9.00", "14.00", "PAN-F1", 2);
        linea(c, "PAN-002", "20", "4.50", "6.50", "PMO-01", 6);
    }

    private void comprarAbarrotes(Sucursales sucursal, Proveedores proveedor, MetodosPago pago) {
        Compras c = nuevaCompra(sucursal, proveedor, pago, "Reposición de abarrotes, bebidas y cuidado personal");
        linea(c, "CUI-001", "2", "130.00", "170.00", null, null);
        linea(c, "CUI-002", "24", "5.50", "7.90", null, null);
        linea(c, "CUI-003", "3", "72.00", "95.00", null, null);
        linea(c, "CUI-004", "36", "3.80", "5.50", null, null);
        linea(c, "ABA-001", "3", "50.00", "62.00", null, null);
        linea(c, "ABA-002", "2", "100.00", "125.00", "ATU-01", 400);
        linea(c, "ABA-003", "2", "138.00", "175.00", null, null);
        linea(c, "ABA-004", "36", "1.20", "1.80", null, null);
        linea(c, "ABA-005", "12", "12.00", "16.00", "CAF-01", 300);
        linea(c, "BEB-003", "4", "50.00", "68.00", "CER-01", 180);
        linea(c, "BEB-004", "5", "22.00", "29.00", "JUG-01", 20);
    }

    private Inventario inventarioDeSucursal(String nombre) {
        return inventarios.findAll().stream()
                .filter(i -> nombre.equals(i.getNombre()) && i.getAlmacen() != null && i.getAlmacen().getSucursal() != null
                        && sucursalId.equals(i.getAlmacen().getSucursal().getIdSucursal()))
                .findFirst().orElse(null);
    }

    private void trasladarAAnaqueles() {
        Inventario general = inventarioDeSucursal("Inventario General");
        Inventario anaqueles = inventarioDeSucursal("Anaqueles de venta");
        if (general == null || anaqueles == null) {
            return;
        }
        for (String[] t : new String[][] {{"BEB-001", "12"}, {"BEB-002", "15"}, {"SNK-001", "12"}}) {
            Productos p = porCodigo(t[0]);
            InventarioProducto origen = stocks.findByProductoIdproductoAndInventarioIdinventario(p.getIdproducto(), general.getIdinventario()).orElse(null);
            if (origen == null) {
                continue;
            }
            InventarioProducto destino = stocks.findByProductoIdproductoAndInventarioIdinventario(p.getIdproducto(), anaqueles.getIdinventario())
                    .orElseGet(() -> {
                        InventarioProducto nuevo = new InventarioProducto();
                        nuevo.setProducto(p);
                        nuevo.setInventario(anaqueles);
                        nuevo.setStockactual(BigDecimal.ZERO);
                        nuevo.setFechaingreso(LocalDateTime.now());
                        return stocks.save(nuevo);
                    });
            TrasladoInventarioProductoDTO dto = new TrasladoInventarioProductoDTO();
            dto.setOrigenId(origen.getIdinventarioproducto());
            dto.setDestinoId(destino.getIdinventarioproducto());
            dto.setCantidad(new BigDecimal(t[1]));
            dto.setDescripcion("Surtido de anaqueles");
            traslados.crearTraslado(dto);
        }
    }

    private void crearVehiculosYEstados() {
        if (vehiculos.count() > 0 || estadosMovimiento.count() > 0) {
            return;   // tablas compartidas por todas las empresas
        }
        for (String[] v : new String[][] {{"ABC-123", "Honda CB 125", "Honda", "2022", "Rojo"},
                {"XYZ-789", "Hyundai H100", "Hyundai", "2020", "Blanco"}}) {
            Vehiculo ve = new Vehiculo();
            ve.setPlaca(v[0]);
            ve.setModelo(v[1]);
            ve.setMarca(v[2]);
            ve.setAnio(v[3]);
            ve.setColor(v[4]);
            ve.setObservaciones("Vehículo de reparto");
            vehiculos.save(ve);
        }
        for (String nombre : List.of("Pendiente", "En camino", "Entregado", "Cancelado")) {
            EstadoMovimiento e = new EstadoMovimiento();
            e.setNombre(nombre);
            estadosMovimiento.save(e);
        }
    }

    private AperturaCaja abrir(Caja caja, com.gadbacorp.api.entity.empleados.Usuarios usuario, double saldoInicial) {
        caja.setEstadoCaja("OCUPADA");
        cajas.save(caja);
        AperturaCaja a = new AperturaCaja();
        a.setFechaApertura(new Date());
        a.setEstadoCaja("ABIERTA");
        a.setSaldoInicial(saldoInicial);
        a.setSaldoEfectivo(0.0);
        a.setUsuarios(usuario);
        a.setCaja(caja);
        return aperturas.save(a);
    }

    private void cajas(Sucursales sucursal, com.gadbacorp.api.entity.empleados.Usuarios usuario, MetodosPago efectivo,
                       MetodosPago yape, MetodosPago tarjeta, Clientes varios, Clientes ana, Clientes bodega) {
        Caja caja1 = cajas.findAll().stream()
                .filter(c -> c.getSucursales() != null && c.getSucursales().getIdSucursal().equals(sucursal.getIdSucursal()))
                .findFirst().orElseThrow();
        Caja caja2 = new Caja();
        caja2.setNombreCaja("Caja 2");
        caja2.setSaldoActual(0.0);
        caja2.setEstado(1);
        caja2.setEstadoCaja("LIBRE");
        caja2.setSucursales(sucursal);
        caja2 = cajas.save(caja2);

        // Caja 2: un turno completo ya cerrado (historial), con ventas, un gasto y arqueo
        AperturaCaja turno = abrir(caja2, usuario, 80.0);
        venta(sucursal, efectivo, varios, "BOLETA", turno.getIdAperturaCaja(), null, new Object[][] {
                {"VER-003", "5", null, 2.20}, {"VER-002", "2", null, 2.90}});
        venta(sucursal, efectivo, ana, "BOLETA", turno.getIdAperturaCaja(), null, new Object[][] {
                {"ABA-004", "3", null, 1.80}, {"CUI-004", "2", null, 5.50}});
        cajaMovimientos.registrar(turno.getIdAperturaCaja(), efectivo, CajaMovimientoService.EGRESO, 15.0, "Compra de bolsas y cinta");
        turno = aperturas.findById(turno.getIdAperturaCaja()).orElseThrow();
        ArqueoCajaDTO arqueo = new ArqueoCajaDTO();
        arqueo.setId_apertura_caja(turno.getIdAperturaCaja());
        arqueo.setMontoContado(turno.getSaldoEfectivo());
        arqueo.setObservaciones("Cuadre sin diferencia");
        cierreCaja.registrarArqueo(arqueo);
        cierreCaja.cerrar(turno.getIdAperturaCaja());

        // Caja 1: turno abierto ahora, con ventas en efectivo, Yape y tarjeta, envios y un gasto
        AperturaCaja abierta = abrir(caja1, usuario, 100.0);
        Integer ap = abierta.getIdAperturaCaja();
        venta(sucursal, efectivo, varios, "BOLETA", ap, null, new Object[][] {
                {"PAN-001", "10", null, 0.30}, {"PAN-002", "2", null, 6.50}, {"FRU-001", "1.5", null, 2.50}});
        venta(sucursal, yape, ana, "BOLETA", ap, null, new Object[][] {
                {"CAR-001", "2.5", null, 11.50}, {"VER-001", "1", null, 3.50}});
        venta(sucursal, tarjeta, bodega, "FACTURA", ap, null, new Object[][] {
                {"BEB-003", "1", idPresentacion(porCodigo("BEB-003")), 68.00},
                {"ABA-002", "1", idPresentacion(porCodigo("ABA-002")), 125.00}});
        venta(sucursal, efectivo, bodega, "FACTURA", ap, null, new Object[][] {
                {"ABA-003", "1", idPresentacion(porCodigo("ABA-003")), 175.00},
                {"ABA-001", "1", idPresentacion(porCodigo("ABA-001")), 62.00}});
        // dos pedidos con envio a domicilio
        Integer v1 = venta(sucursal, yape, ana, "BOLETA", ap, envio("Av. Los Álamos 345, Tarapoto", "Luis Pérez", "4.00", "Dejar con el vigilante"),
                new Object[][] {{"CUI-003", "1", null, 8.50}, {"CUI-002", "2", null, 7.90}});
        Integer v2 = venta(sucursal, efectivo, varios, "BOLETA", ap, envio("Jr. San Martín 120, Morales", "Rosa Díaz", "3.00", "Llamar al llegar"),
                new Object[][] {{"EMB-002", "2", null, 8.90}, {"BEB-004", "2", null, 5.20}});
        actualizarEnvio(v1, "En camino");
        actualizarEnvio(v2, "Entregado");
        cajaMovimientos.registrar(ap, efectivo, CajaMovimientoService.EGRESO, 20.0, "Pago de movilidad del repartidor");

        TransferenciasEntreCajas t = new TransferenciasEntreCajas();
        t.setMonto(50.0);
        t.setFecha(new Date());
        t.setObservaciones("Cambio en billetes pequeños para la Caja 2");
        t.setCajaOrigen(caja1);
        t.setCajaDestino(caja2);
        transferencias.save(t);
    }

    private DeliveryDTO envio(String direccion, String encargado, String costo, String observaciones) {
        DeliveryDTO d = new DeliveryDTO();
        d.setDireccion(direccion);
        d.setFechaEnvio(new Date());
        d.setEncargado(encargado);
        d.setCostoEnvio(new BigDecimal(costo));
        d.setObservaciones(observaciones);
        d.setEstado(1);
        return d;
    }

    private void actualizarEnvio(Integer idVenta, String estado) {
        for (Delivery d : deliveries.findAll()) {
            if (d.getVenta() != null && d.getVenta().getIdVenta().equals(idVenta)) {
                d.setEstadoDelivery(estado);
                if ("Entregado".equals(estado)) {
                    d.setFechaEntrega(new Date());
                }
                deliveries.save(d);
            }
        }
    }

    /** Arma y registra una venta por el mismo camino que el punto de venta. Devuelve su id. */
    private Integer venta(Sucursales sucursal, MetodosPago pago, Clientes cliente, String comprobante, Integer idApertura,
                          DeliveryDTO envio, Object[][] lineas) {
        List<DetallesVentasDTO> detalles = new ArrayList<>();
        double total = 0;
        for (Object[] l : lineas) {
            Productos producto = porCodigo((String) l[0]);
            BigDecimal cantidad = new BigDecimal((String) l[1]);
            double precio = (Double) l[3];
            double subtotal = Math.round(cantidad.doubleValue() * precio * 100.0) / 100.0;
            DetallesVentasDTO d = new DetallesVentasDTO();
            d.setId_producto(producto.getIdproducto());
            d.setCantidad(cantidad);
            d.setIdPresentacion((Integer) l[2]);
            d.setPecioUnitario(precio);
            d.setSubTotal(subtotal);
            d.setEstado(1);
            detalles.add(d);
            total += subtotal;
        }
        total = Math.round(total * 100.0) / 100.0;
        VentaCompletaDTO dto = new VentaCompletaDTO();
        dto.setIdSucursal(sucursal.getIdSucursal());
        dto.setId_cliente(cliente.getIdCliente());
        dto.setId_metodo_pago(pago.getIdMetodoPago());
        dto.setTipo_comprobante(comprobante);
        dto.setTotal_venta(total);
        dto.setEstado(1);
        dto.setEstadoPago("REALIZADO");
        dto.setMontoPagado(total);
        dto.setId_apertura_caja(idApertura);
        dto.setDelivery(envio);
        dto.setDetalles(detalles);
        return registroVenta.registrar(dto).getIdVenta();
    }

    /** El cliente devuelve 1 paquete de galletas y 1 agua de la venta de galletas y agua; vuelven al stock. */
    private void devolucionDeVenta(Sucursales sucursal) {
        Ventas origen = ventas.findAll().stream()
                .filter(v -> v.getCliente() != null && v.getCliente().getSucursal() != null
                        && sucursalId.equals(v.getCliente().getSucursal().getIdSucursal()))
                .filter(v -> detallesVentas.findAll().stream().anyMatch(d -> d.getVentas() != null
                        && d.getVentas().getIdVenta().equals(v.getIdVenta())
                        && "SNK-001".equals(d.getProductos().getCodigo())))
                .findFirst().orElse(null);
        if (origen == null) {
            return;
        }
        Devoluciones dev = new Devoluciones();
        dev.setFechaDevolucion(LocalDate.now());
        dev.setMotivoDevolucion("Producto en mal estado");
        dev.setObservaciones("El cliente devolvió un paquete de galletas húmedo");
        dev.setVentas(origen);
        dev.setEstado(1);
        dev.setMontoDevuelto(1.20);
        dev = devoluciones.save(dev);

        Productos galletas = porCodigo("SNK-001");
        DetallesDevolucion d = new DetallesDevolucion();
        d.setDevoluciones(dev);
        d.setProductos(galletas);
        d.setCantidad(BigDecimal.ONE);
        d.setPecioUnitario(1.20);
        d.setSubTotal(1.20);
        d.setEstado(1);
        detallesDevoluciones.save(d);
        stockService.mover(sucursal.getIdSucursal(), galletas, BigDecimal.ONE, MovimientoStock.DEVOLUCION,
                "Devolucion de venta " + origen.getNro_comrprobante());
    }

    /** Se devuelve al proveedor 1 saco de la compra de abarrotes por empaque dañado: el stock baja. */
    private void devolucionACompra(Sucursales sucursal) {
        Compras compra = compras.listarCompras().stream()
                .filter(c -> c.getSucursal() != null && sucursalId.equals(c.getSucursal().getIdSucursal()))
                .filter(c -> c.getDescripcion() != null && c.getDescripcion().startsWith("Reposición"))
                .findFirst().orElse(null);
        if (compra == null) {
            return;
        }
        Productos harina = porCodigo("ABA-003");
        DevolucionesCompra dev = new DevolucionesCompra();
        dev.setCompra(compra);
        dev.setFechaDevolucion(LocalDateTime.now());
        dev.setMotivoDevolucion("Empaque dañado");
        dev.setObservaciones("Un saco de harina llegó roto");
        dev.setMontoDerivativo(new BigDecimal("138.00"));
        dev.setEstado(1);
        dev = devolucionesCompra.save(dev);

        DetallesDevolucionCompra d = new DetallesDevolucionCompra();
        d.setDevolucionCompra(dev);
        d.setProducto(harina);
        d.setCantidad(new BigDecimal("50"));
        d.setPrecioUnitario(new BigDecimal("2.76"));
        d.setSubTotal(new BigDecimal("138.00"));
        d.setEstado(1);
        detallesDevolucionCompra.save(d);
        stockService.descontar(sucursal.getIdSucursal(), harina.getIdproducto(), new BigDecimal("50"),
                MovimientoStock.DEVOLUCION, "Devolucion a proveedor, compra #" + compra.getIdCompra());
    }

    private void cotizaciones(Clientes ana, Clientes bodega) {
        cotizacion(bodega, "ENVIADA", new Object[][] {{"ABA-001", "10", 3.20}, {"ABA-002", "12", 5.10}});
        cotizacion(ana, "APROBADA", new Object[][] {{"CUI-001", "6", 14.50}});
    }

    private void cotizacion(Clientes cliente, String estado, Object[][] lineas) {
        Cotizaciones c = new Cotizaciones();
        c.setCliente(cliente);
        c.setFechaCotizacion(LocalDate.now());
        c.setFechaVencimiento(LocalDate.now().plusDays(7));
        c.setNumeroCotizacion(numeracion.siguienteCotizacion());
        c.setEstadoCotizacion(estado);
        c.setEstado(1);
        double total = 0;
        for (Object[] l : lineas) {
            total += new BigDecimal((String) l[1]).doubleValue() * (Double) l[2];
        }
        c.setTotalCotizacion(Math.round(total * 100.0) / 100.0);
        c = cotizaciones.save(c);
        for (Object[] l : lineas) {
            BigDecimal cantidad = new BigDecimal((String) l[1]);
            DetallesCotizaciones d = new DetallesCotizaciones();
            d.setCotizaciones(c);
            d.setProductos(porCodigo((String) l[0]));
            d.setCantidad(cantidad);
            d.setCantidadBase(cantidad.setScale(3));
            d.setPrecioUnitario((Double) l[2]);
            d.setSubTotal(Math.round(cantidad.doubleValue() * (Double) l[2] * 100.0) / 100.0);
            d.setFechaCotizacion(LocalDate.now());
            d.setEstado(1);
            detallesCotizaciones.save(d);
        }
    }

    private void promociones() {
        Promociones bebidas = new Promociones();
        bebidas.setNombre("10 % en bebidas de fin de semana");
        bebidas.setFechaInicio(LocalDateTime.now().minusDays(1));
        bebidas.setFechaFin(LocalDateTime.now().plusDays(14));
        bebidas.setTipoDescuento("PORCENTAJE");
        bebidas.setValor(new BigDecimal("10"));
        categorias.findByNombreIgnoreCase("Bebidas").ifPresent(c -> bebidas.getCategorias().add(c));
        promociones.save(bebidas);

        Promociones lacteos = new Promociones();
        lacteos.setNombre("S/ 0.50 menos en lácteos por vencer");
        lacteos.setFechaInicio(LocalDateTime.now());
        lacteos.setFechaFin(LocalDateTime.now().plusDays(10));
        lacteos.setTipoDescuento("MONTO");
        lacteos.setValor(new BigDecimal("0.50"));
        Map<String, Productos> lista = new LinkedHashMap<>();
        for (String codigo : List.of("YOG-001", "LEC-001")) {
            Productos p = porCodigo(codigo);
            if (p != null) {
                lista.put(codigo, p);
            }
        }
        lacteos.getProductos().addAll(lista.values());
        promociones.save(lacteos);
    }
}
