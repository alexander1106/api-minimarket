package com.gadbacorp.api.config;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
import com.gadbacorp.api.entity.compras.Compras;
import com.gadbacorp.api.entity.compras.DetallesCompras;
import com.gadbacorp.api.entity.compras.Proveedores;
import com.gadbacorp.api.entity.inventario.AjusteInventario;
import com.gadbacorp.api.entity.inventario.Almacenes;
import com.gadbacorp.api.entity.inventario.Inventario;
import com.gadbacorp.api.entity.inventario.InventarioProducto;
import com.gadbacorp.api.entity.inventario.MovimientoStock;
import com.gadbacorp.api.entity.inventario.ProductoPresentacion;
import com.gadbacorp.api.entity.inventario.Productos;
import com.gadbacorp.api.entity.ventas.Clientes;
import com.gadbacorp.api.entity.ventas.Cotizaciones;
import com.gadbacorp.api.entity.ventas.DetallesCotizaciones;
import com.gadbacorp.api.entity.ventas.DetallesVentasDTO;
import com.gadbacorp.api.entity.ventas.MetodosPago;
import com.gadbacorp.api.entity.ventas.VentaCompletaDTO;
import com.gadbacorp.api.repository.administrable.SucursalesRepository;
import com.gadbacorp.api.repository.caja.CajaRepository;
import com.gadbacorp.api.repository.ventas.MetodoPagoCatalogoRepository;
import com.gadbacorp.api.entity.caja.Caja;
import com.gadbacorp.api.entity.empleados.Usuarios;
import com.gadbacorp.api.repository.compras.ProveedoresRepository;
import com.gadbacorp.api.repository.empleados.UsuarioRepository;
import com.gadbacorp.api.repository.inventario.AjusteInventarioRepository;
import com.gadbacorp.api.repository.inventario.AlmacenesRepository;
import com.gadbacorp.api.repository.inventario.CategoriasRepository;
import com.gadbacorp.api.repository.inventario.InventarioProductoRepository;
import com.gadbacorp.api.repository.inventario.InventarioRepository;
import com.gadbacorp.api.repository.inventario.ProductoPresentacionRepository;
import com.gadbacorp.api.repository.inventario.ProductosRepository;
import com.gadbacorp.api.repository.inventario.TipoProductoRepository;
import com.gadbacorp.api.repository.inventario.UnidadDeMedidaRepository;
import com.gadbacorp.api.repository.ventas.ClientesRepository;
import com.gadbacorp.api.repository.ventas.CotizacionesRepository;
import com.gadbacorp.api.repository.ventas.DetallesCotizacionesRepository;
import com.gadbacorp.api.repository.ventas.MetodosPagoRepository;
import com.gadbacorp.api.service.compras.IComprasService;
import com.gadbacorp.api.service.compras.IDetallesComprasService;
import com.gadbacorp.api.service.jpa.inventario.ConversionUnidadesService;
import com.gadbacorp.api.service.jpa.inventario.KardexService;
import com.gadbacorp.api.service.jpa.ventas.NumeracionService;
import com.gadbacorp.api.service.jpa.ventas.RegistroVentaService;

/**
 * Datos de demostracion para la empresa de prueba (usuario "empresa"): almacenes, productos con presentaciones,
 * proveedores, clientes, una compra con lotes, ventas, una cotizacion y un ajuste. Pasan por los mismos servicios que la API,
 * asi que el stock, el kardex, los lotes y el IGV quedan coherentes.
 * Solo corre si esa empresa aun no tiene productos. Desactivar con APP_SEED_DEMO=false (obligatorio en produccion).
 */
@Component
@Order(30)
public class DatosDemoRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DatosDemoRunner.class);

    private final boolean habilitado;
    private final String empresaUsername;
    private final PlatformTransactionManager tx;
    private final UsuarioRepository usuarios;
    private final SucursalesRepository sucursales;
    private final AlmacenesRepository almacenes;
    private final InventarioRepository inventarios;
    private final InventarioProductoRepository stocks;
    private final ProductosRepository productos;
    private final ProductoPresentacionRepository presentaciones;
    private final CategoriasRepository categorias;
    private final TipoProductoRepository tipos;
    private final UnidadDeMedidaRepository unidades;
    private final ProveedoresRepository proveedores;
    private final ClientesRepository clientes;
    private final MetodosPagoRepository metodosPago;
    private final IComprasService compras;
    private final IDetallesComprasService detallesCompras;
    private final RegistroVentaService registroVenta;
    private final CotizacionesRepository cotizaciones;
    private final DetallesCotizacionesRepository detallesCotizaciones;
    private final NumeracionService numeracion;
    private final AjusteInventarioRepository ajustes;
    private final KardexService kardex;
    private final CajaRepository cajas;
    private final MetodoPagoCatalogoRepository catalogoMetodos;

    public DatosDemoRunner(@Value("${app.seed.enabled:true}") boolean semilla,
                           @Value("${app.seed.demo:true}") boolean demo,
                           @Value("${app.seed.empresa-username:empresa}") String empresaUsername,
                           PlatformTransactionManager tx, UsuarioRepository usuarios, SucursalesRepository sucursales,
                           AlmacenesRepository almacenes, InventarioRepository inventarios,
                           InventarioProductoRepository stocks, ProductosRepository productos,
                           ProductoPresentacionRepository presentaciones, CategoriasRepository categorias,
                           TipoProductoRepository tipos, UnidadDeMedidaRepository unidades,
                           ProveedoresRepository proveedores, ClientesRepository clientes,
                           MetodosPagoRepository metodosPago, IComprasService compras,
                           IDetallesComprasService detallesCompras, RegistroVentaService registroVenta,
                           CotizacionesRepository cotizaciones, DetallesCotizacionesRepository detallesCotizaciones,
                           NumeracionService numeracion, AjusteInventarioRepository ajustes, KardexService kardex,
                           CajaRepository cajas, MetodoPagoCatalogoRepository catalogoMetodos) {
        this.cajas = cajas;
        this.catalogoMetodos = catalogoMetodos;
        this.habilitado = semilla && demo;
        this.empresaUsername = empresaUsername;
        this.tx = tx;
        this.usuarios = usuarios;
        this.sucursales = sucursales;
        this.almacenes = almacenes;
        this.inventarios = inventarios;
        this.stocks = stocks;
        this.productos = productos;
        this.presentaciones = presentaciones;
        this.categorias = categorias;
        this.tipos = tipos;
        this.unidades = unidades;
        this.proveedores = proveedores;
        this.clientes = clientes;
        this.metodosPago = metodosPago;
        this.compras = compras;
        this.detallesCompras = detallesCompras;
        this.registroVenta = registroVenta;
        this.cotizaciones = cotizaciones;
        this.detallesCotizaciones = detallesCotizaciones;
        this.numeracion = numeracion;
        this.ajustes = ajustes;
        this.kardex = kardex;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!habilitado) {
            return;
        }
        for (Usuarios usuario : candidatos(usuarios)) {
            try {
                new TransactionTemplate(tx).executeWithoutResult(s -> cargar(usuario));
            } catch (RuntimeException e) {
                log.warn("Seed demo: no se pudieron cargar los datos de {} ({}). La aplicacion arranca igual.",
                        usuario.getUsername(), e.getMessage());
            }
        }
    }

    /** Un usuario EMPRESA o ADMIN por sucursal: cada empresa de la plataforma recibe sus propios datos de demostracion. */
    static List<Usuarios> candidatos(UsuarioRepository usuarios) {
        Map<Integer, Usuarios> porSucursal = new LinkedHashMap<>();
        for (Usuarios u : usuarios.findAll()) {
            String rol = u.getRol() == null ? "" : u.getRol().getNombre();
            if (u.getSucursal() != null && ("EMPRESA".equals(rol) || "ADMIN".equals(rol))) {
                porSucursal.putIfAbsent(u.getSucursal().getIdSucursal(), u);
            }
        }
        return new ArrayList<>(porSucursal.values());
    }

    /** Una sucursal nueva puede no tener metodos de pago ni caja: se activan Efectivo, Yape y Tarjeta del catalogo y se crea Caja 1. */
    private void asegurarBase(Sucursales sucursal) {
        List<MetodosPago> existentes = metodosPago.findBySucursalIdSucursal(sucursal.getIdSucursal());
        for (String nombre : List.of("Efectivo", "Yape", "Tarjeta")) {
            if (existentes.stream().noneMatch(m -> nombre.equalsIgnoreCase(m.getNombre()))) {
                catalogoMetodos.findByNombreIgnoreCase(nombre).ifPresent(c -> {
                    MetodosPago m = new MetodosPago();
                    m.setNombre(c.getNombre());
                    m.setCatalogo(c);
                    m.setDetalle(Boolean.TRUE.equals(c.getRequiereDetalle()) ? "999 000 111" : null);
                    m.setSucursal(sucursal);
                    m.setEstado(1);
                    metodosPago.save(m);
                });
            }
        }
        boolean tieneCaja = cajas.findAll().stream().anyMatch(c -> c.getSucursales() != null
                && c.getSucursales().getIdSucursal().equals(sucursal.getIdSucursal()));
        if (!tieneCaja) {
            Caja caja = new Caja();
            caja.setNombreCaja("Caja 1");
            caja.setSaldoActual(0.0);
            caja.setEstado(1);
            caja.setEstadoCaja("LIBRE");
            caja.setSucursales(sucursal);
            cajas.save(caja);
        }
    }

    private void cargar(Usuarios usuario) {
        if (usuario.getSucursal() == null) {
            return;
        }
        Sucursales sucursal = usuario.getSucursal();
        Empresas empresa = sucursal.getEmpresa();
        boolean yaTieneProductos = productos.findAll().stream()
                .anyMatch(p -> p.getEmpresa() != null && empresa != null
                        && empresa.getIdempresa().equals(p.getEmpresa().getIdempresa()));
        if (yaTieneProductos) {
            return;
        }

        asegurarBase(sucursal);
        Inventario inventario = crearAlmacenes(sucursal);
        Map<String, Productos> catalogo = crearProductos(empresa);
        Proveedores proveedor = crearProveedores(empresa);
        List<Clientes> clientesDemo = crearClientes(sucursal);
        MetodosPago efectivo = metodosPago.findBySucursalIdSucursal(sucursal.getIdSucursal()).stream()
                .filter(m -> "Efectivo".equalsIgnoreCase(m.getNombre())).findFirst().orElse(null);
        if (efectivo == null) {
            log.warn("Seed demo: la sucursal no tiene el metodo de pago Efectivo; se omiten compras y ventas.");
            return;
        }
        registrarCompra(sucursal, proveedor, efectivo, catalogo);
        registrarVentas(sucursal, efectivo, clientesDemo, catalogo);
        registrarCotizacion(clientesDemo.get(1), catalogo);
        registrarAjuste(sucursal, catalogo.get("Galletas Soda"));
        log.info("Seed demo: {} productos, 1 compra, 3 ventas, 1 cotizacion cargados para la sucursal {}.",
                catalogo.size(), sucursal.getNombreSucursal());
    }

    private Inventario crearAlmacenes(Sucursales sucursal) {
        Almacenes principal = nuevoAlmacen("Almacén Principal", sucursal);
        Inventario general = nuevoInventario("Inventario General", principal);
        Almacenes tienda = nuevoAlmacen("Tienda (anaqueles)", sucursal);
        nuevoInventario("Anaqueles de venta", tienda);
        return general;
    }

    private Almacenes nuevoAlmacen(String nombre, Sucursales sucursal) {
        Almacenes a = new Almacenes();
        a.setNombre(nombre);
        a.setSucursal(sucursal);
        a.setEstado(1);
        return almacenes.save(a);
    }

    private Inventario nuevoInventario(String nombre, Almacenes almacen) {
        Inventario i = new Inventario();
        i.setNombre(nombre);
        i.setAlmacen(almacen);
        i.setEstado(1);
        return inventarios.save(i);
    }

    /** {nombre, codigo, codigo de barras, marca, categoria, tipo, unidad, costo, venta, mayor, stock minimo, presentacion, factor, p.venta, p.compra} */
    private static final Object[][] PRODUCTOS = {
            {"Arroz extra", "ARR-001", "7750001000011", "Costeño", "Abarrotes", "No perecible", "KG", "3.20", "4.20", "3.90", "100", "Saco 50 kg", "50", "200.00", "160.00"},
            {"Azúcar rubia", "AZU-001", "7750001000028", "Cartavio", "Abarrotes", "No perecible", "KG", "3.00", "3.90", "3.60", "100", "Saco 50 kg", "50", "185.00", "150.00"},
            {"Aceite vegetal", "ACE-001", "7750001000035", "Primor", "Abarrotes", "No perecible", "LT", "8.50", "10.90", "10.00", "24", "Caja x12", "12", "125.00", "100.00"},
            {"Leche evaporada", "LEC-001", "7750001000042", "Gloria", "Lácteos", "Perecible", "UND", "3.40", "4.20", "3.90", "48", "Caja x24", "24", "95.00", "80.00"},
            {"Yogurt 1 L", "YOG-001", "7750001000059", "Gloria", "Lácteos", "Perecible", "UND", "5.80", "7.50", "6.90", "12", "Paquete x6", "6", "42.00", "33.00"},
            {"Gaseosa 1.5 L", "BEB-001", "7750001000066", "Inca Kola", "Bebidas", "No perecible", "UND", "5.50", "7.00", "6.50", "12", "Paquete x6", "6", "40.00", "30.00"},
            {"Agua sin gas 625 ml", "BEB-002", "7750001000073", "San Luis", "Bebidas", "No perecible", "UND", "1.00", "1.50", "1.30", "30", "Paquete x15", "15", "20.00", "14.00"},
            {"Galletas Soda", "SNK-001", "7750001000080", "Field", "Snacks", "No perecible", "UND", "0.80", "1.20", "1.00", "24", "Paquete x12", "12", "13.00", "8.50"},
            {"Detergente 1 kg", "LIM-001", "7750001000097", "Ariel", "Limpieza", "No perecible", "UND", "9.00", "11.90", "10.90", "6", null, null, null, null},
            {"Lejía 1 L", "LIM-002", "7750001000103", "Clorox", "Limpieza", "No perecible", "UND", "3.00", "4.50", "4.00", "6", null, null, null, null},
    };

    private Map<String, Productos> crearProductos(Empresas empresa) {
        Map<String, Productos> creados = new LinkedHashMap<>();
        for (Object[] d : PRODUCTOS) {
            Productos p = new Productos();
            p.setNombre((String) d[0]);
            p.setCodigo((String) d[1]);
            p.setCodigoBarras((String) d[2]);
            p.setMarca((String) d[3]);
            p.setCategoria(categorias.findByNombreIgnoreCase((String) d[4]).orElse(null));
            p.setTipoProducto(tipos.findAll().stream().filter(t -> d[5].equals(t.getNombre())).findFirst().orElse(null));
            p.setUnidadMedida(unidades.findAll().stream().filter(u -> d[6].equals(u.getCodigo())).findFirst().orElse(null));
            p.setCostoCompra(new BigDecimal((String) d[7]));
            p.setCostoVenta(new BigDecimal((String) d[8]));
            p.setCostoMayor(new BigDecimal((String) d[9]));
            p.setStockMinimo(new BigDecimal((String) d[10]));
            p.setAfectoIgv(true);
            p.setEstado(1);
            p.setEmpresa(empresa);
            p = productos.save(p);
            if (d[11] != null) {
                ProductoPresentacion pr = new ProductoPresentacion();
                pr.setProducto(p);
                pr.setNombre((String) d[11]);
                pr.setFactor(new BigDecimal((String) d[12]));
                pr.setPrecioVenta(new BigDecimal((String) d[13]));
                pr.setPrecioCompra(new BigDecimal((String) d[14]));
                pr.setEstado(1);
                presentaciones.save(pr);
            }
            creados.put(p.getNombre(), p);
        }
        return creados;
    }

    private Integer idPresentacion(Productos p) {
        return presentaciones.findAll().stream()
                .filter(x -> x.getProducto() != null && x.getProducto().getIdproducto().equals(p.getIdproducto()))
                .map(ProductoPresentacion::getIdPresentacion).findFirst().orElse(null);
    }

    private Proveedores crearProveedores(Empresas empresa) {
        Proveedores a = nuevoProveedor("Distribuidora Andina SAC", "20512345678", "Av. Industrial 120", "042 521 000", empresa);
        nuevoProveedor("Lácteos del Oriente EIRL", "20498765432", "Jr. Comercio 455", "042 522 111", empresa);
        return a;
    }

    private Proveedores nuevoProveedor(String nombre, String ruc, String direccion, String telefono, Empresas empresa) {
        Proveedores p = new Proveedores();
        p.setNombre(nombre);
        p.setRuc(ruc);
        p.setDireccion(direccion);
        p.setTelefono(telefono);
        p.setGmail(nombre.split(" ")[0].toLowerCase().replaceAll("[^a-z]", "") + "@proveedor.demo");
        p.setEstado(1);
        p.setEmpresa(empresa);
        return proveedores.save(p);
    }

    private List<Clientes> crearClientes(Sucursales sucursal) {
        List<Clientes> lista = new ArrayList<>();
        lista.add(nuevoCliente("Cliente varios", "DNI", "00000000", sucursal));
        lista.add(nuevoCliente("Ana Torres Ruiz", "DNI", "45871236", sucursal));
        lista.add(nuevoCliente("Bodega Don Lucho SAC", "RUC", "20601234567", sucursal));
        return lista;
    }

    private Clientes nuevoCliente(String nombre, String tipoDocumento, String documento, Sucursales sucursal) {
        Clientes c = new Clientes();
        c.setNombre(nombre);
        c.setTipoDocumento(tipoDocumento);
        c.setDocumento(documento);
        c.setSucursal(sucursal);
        return clientes.save(c);
    }

    private void registrarCompra(Sucursales sucursal, Proveedores proveedor, MetodosPago pago, Map<String, Productos> p) {
        Compras compra = new Compras();
        compra.setProveedor(proveedor);
        compra.setMetodoPago(pago);
        compra.setSucursal(sucursal);
        compra.setFechaCompra(LocalDateTime.now());
        compra.setDescripcion("Compra inicial de mercadería");
        compra.setEstado(1);
        compra.setTotal(BigDecimal.ZERO);
        compra = compras.guardarCompra(compra);

        LocalDate hoy = LocalDate.now();
        // {producto, cantidad (en presentacion si tiene), precio compra, precio venta, lote, vence}
        Object[][] lineas = {
                {"Arroz extra", "4", "160.00", "200.00", null, null},
                {"Azúcar rubia", "2", "150.00", "185.00", null, null},
                {"Aceite vegetal", "5", "100.00", "125.00", null, null},
                {"Leche evaporada", "3", "80.00", "95.00", "LECHE-A", hoy.plusDays(25)},
                {"Yogurt 1 L", "4", "33.00", "42.00", "YOG-A", hoy.plusDays(12)},
                {"Gaseosa 1.5 L", "6", "30.00", "40.00", null, null},
                {"Agua sin gas 625 ml", "4", "14.00", "20.00", null, null},
                {"Galletas Soda", "5", "8.50", "13.00", "GAL-A", hoy.plusDays(120)},
                {"Detergente 1 kg", "20", "9.00", "11.90", null, null},
                {"Lejía 1 L", "15", "3.00", "4.50", null, null},
        };
        for (Object[] l : lineas) {
            Productos producto = p.get((String) l[0]);
            DetallesCompras d = new DetallesCompras();
            d.setCantidad(new BigDecimal((String) l[1]));
            d.setIdPresentacionSolicitada(idPresentacion(producto));
            d.setPrecioUnitario(new BigDecimal((String) l[2]));
            d.setPrecioCompra(new BigDecimal((String) l[2]));
            d.setPrecioVenta(new BigDecimal((String) l[3]));
            d.setLote((String) l[4]);
            d.setFechaVencimiento((LocalDate) l[5]);
            detallesCompras.guardarDetalle(d, compra.getIdCompra(), producto.getIdproducto());
        }
    }

    private void registrarVentas(Sucursales sucursal, MetodosPago pago, List<Clientes> c, Map<String, Productos> p) {
        // venta 1: kilos sueltos de arroz + una gaseosa
        venta(sucursal, pago, c.get(0), "BOLETA", new Object[][] {
                {p.get("Arroz extra"), "2.5", null, 4.20},
                {p.get("Gaseosa 1.5 L"), "1", null, 7.00}});
        // venta 2: un saco entero + leche suelta, con factura
        venta(sucursal, pago, c.get(2), "FACTURA", new Object[][] {
                {p.get("Arroz extra"), "1", idPresentacion(p.get("Arroz extra")), 200.00},
                {p.get("Leche evaporada"), "6", null, 4.20}});
        // venta 3: galletas y agua
        venta(sucursal, pago, c.get(0), "BOLETA", new Object[][] {
                {p.get("Galletas Soda"), "3", null, 1.20},
                {p.get("Agua sin gas 625 ml"), "2", null, 1.50}});
    }

    private void venta(Sucursales sucursal, MetodosPago pago, Clientes cliente, String comprobante, Object[][] lineas) {
        List<DetallesVentasDTO> detalles = new ArrayList<>();
        double total = 0;
        for (Object[] l : lineas) {
            Productos producto = (Productos) l[0];
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
        VentaCompletaDTO dto = new VentaCompletaDTO();
        dto.setIdSucursal(sucursal.getIdSucursal());
        dto.setId_cliente(cliente.getIdCliente());
        dto.setId_metodo_pago(pago.getIdMetodoPago());
        dto.setTipo_comprobante(comprobante);
        dto.setTotal_venta(Math.round(total * 100.0) / 100.0);
        dto.setEstado(1);
        dto.setEstadoPago("REALIZADO");
        dto.setMontoPagado(dto.getTotal_venta());
        dto.setDetalles(detalles);
        registroVenta.registrar(dto);
    }

    private void registrarCotizacion(Clientes cliente, Map<String, Productos> p) {
        Productos azucar = p.get("Azúcar rubia");
        Cotizaciones c = new Cotizaciones();
        c.setCliente(cliente);
        c.setFechaCotizacion(LocalDate.now());
        c.setFechaVencimiento(LocalDate.now().plusDays(7));
        c.setNumeroCotizacion(numeracion.siguienteCotizacion());
        c.setEstadoCotizacion("PENDIENTE");
        c.setEstado(1);
        c.setTotalCotizacion(39.0);
        c = cotizaciones.save(c);

        DetallesCotizaciones d = new DetallesCotizaciones();
        d.setCotizaciones(c);
        d.setProductos(azucar);
        d.setCantidad(new BigDecimal("10"));
        d.setCantidadBase(new BigDecimal("10.000"));
        d.setPrecioUnitario(3.9);
        d.setSubTotal(39.0);
        d.setFechaCotizacion(LocalDate.now());
        d.setEstado(1);
        detallesCotizaciones.save(d);
    }

    /** Una merma de 2 unidades de galletas, como la registraria el almacenero. */
    private void registrarAjuste(Sucursales sucursal, Productos producto) {
        InventarioProducto fila = stocks.findAllByProducto_Idproducto(producto.getIdproducto()).stream()
                .findFirst().orElse(null);
        if (fila == null) {
            return;
        }
        BigDecimal merma = new BigDecimal("-2");
        ConversionUnidadesService.validarFraccion(producto, merma);
        fila.setStockactual(fila.stockOCero().add(merma));
        stocks.save(fila);
        AjusteInventario ajuste = new AjusteInventario();
        ajuste.setInventarioProducto(fila);
        ajuste.setCantidad(merma);
        ajuste.setDescripcion("Merma: paquetes rotos");
        ajuste.setFechaAjuste(LocalDateTime.now());
        ajustes.save(ajuste);
        kardex.registrar(producto, sucursal.getIdSucursal(), merma, MovimientoStock.AJUSTE, "Merma: paquetes rotos");
    }
}
