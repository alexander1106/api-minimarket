package com.gadbacorp.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.gadbacorp.api.entity.administrable.Sucursales;
import com.gadbacorp.api.entity.inventario.Almacenes;
import com.gadbacorp.api.entity.inventario.Inventario;
import com.gadbacorp.api.entity.inventario.InventarioProducto;
import com.gadbacorp.api.entity.inventario.Productos;
import com.gadbacorp.api.entity.inventario.UnidadDeMedida;
import com.gadbacorp.api.entity.ventas.Clientes;
import com.gadbacorp.api.repository.empleados.UsuarioRepository;
import com.gadbacorp.api.repository.inventario.AlmacenesRepository;
import com.gadbacorp.api.repository.inventario.InventarioProductoRepository;
import com.gadbacorp.api.repository.inventario.InventarioRepository;
import com.gadbacorp.api.repository.inventario.ProductosRepository;
import com.gadbacorp.api.repository.inventario.UnidadDeMedidaRepository;
import com.gadbacorp.api.repository.ventas.ClientesRepository;
import com.gadbacorp.api.repository.ventas.MetodosPagoRepository;

/**
 * Escenario real contra la base de pruebas: un saco de arroz son 50 kg. Se compra en sacos, se vende en sacos y
 * en kilos sueltos (con decimales) y el stock siempre se mueve en la unidad base (kg).
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PresentacionesIntegracionTest {

    @Autowired MockMvc mockMvc;
    @Autowired UsuarioRepository usuarios;
    @Autowired UnidadDeMedidaRepository unidades;
    @Autowired ProductosRepository productos;
    @Autowired AlmacenesRepository almacenes;
    @Autowired InventarioRepository inventarios;
    @Autowired InventarioProductoRepository stocks;
    @Autowired ClientesRepository clientes;
    @Autowired MetodosPagoRepository metodosPago;
    @Autowired com.gadbacorp.api.repository.compras.ProveedoresRepository proveedores;
    @Autowired com.gadbacorp.api.repository.administrable.EmpresasRepository empresas;
    @Autowired com.gadbacorp.api.repository.administrable.SucursalesRepository sucursalesRepo;

    private String bearer;
    private Productos arroz;
    private Productos galletas;
    private InventarioProducto stockArroz;
    private Integer idSucursal;
    private Integer idCliente;
    private Integer idMetodoPago;

    @BeforeEach
    void escenario() throws Exception {
        bearer = "Bearer " + token("empresa", "1234");
        Sucursales sucursal = usuarios.findByUsername("empresa").getSucursal();
        idSucursal = sucursal.getIdSucursal();

        arroz = producto("Arroz extra", unidad("KG"));
        galletas = producto("Galletas", unidad("UND"));

        Almacenes almacen = new Almacenes();
        almacen.setNombre("Almacen test");
        almacen.setSucursal(sucursal);
        almacen.setEstado(1);
        almacen = almacenes.save(almacen);
        Inventario inventario = new Inventario();
        inventario.setNombre("Inventario test");
        inventario.setAlmacen(almacen);
        inventario.setEstado(1);
        inventario = inventarios.save(inventario);

        stockArroz = stock(inventario, arroz, "200.000");
        stock(inventario, galletas, "100");

        Clientes cliente = new Clientes();
        cliente.setNombre("Cliente prueba");
        cliente.setTipoDocumento("DNI");
        cliente.setDocumento("12345678");
        cliente.setSucursal(sucursal);
        idCliente = clientes.save(cliente).getIdCliente();
        idMetodoPago = metodosPago.findAll().get(0).getIdMetodoPago();
    }

    private String token(String usuario, String clave) throws Exception {
        String r = mockMvc.perform(post("/api/minimarket/login").contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"" + usuario + "\",\"password\":\"" + clave + "\"}"))
                .andReturn().getResponse().getContentAsString();
        return r.replaceAll("(?s).*\"token\":\"([^\"]+)\".*", "$1");
    }

    private UnidadDeMedida unidad(String codigo) {
        return unidades.findAll().stream().filter(u -> codigo.equals(u.getCodigo())).findFirst().orElseThrow();
    }

    private Productos producto(String nombre, UnidadDeMedida unidad) {
        Productos p = new Productos();
        p.setNombre(nombre);
        p.setUnidadMedida(unidad);
        p.setEstado(1);
        p.setEmpresa(usuarios.findByUsername("empresa").getSucursal().getEmpresa());
        return productos.save(p);
    }

    private InventarioProducto stock(Inventario inv, Productos p, String cantidad) {
        InventarioProducto ip = new InventarioProducto();
        ip.setInventario(inv);
        ip.setProducto(p);
        ip.setStockactual(new BigDecimal(cantidad));
        return stocks.save(ip);
    }

    private BigDecimal stockActualArroz() {
        return stocks.findById(stockArroz.getIdinventarioproducto()).orElseThrow().getStockactual();
    }

    private String presentacionJson(String nombre, String factor) {
        return "{\"nombre\":\"" + nombre + "\",\"factor\":" + factor + ",\"precioVenta\":180.00,\"precioCompra\":150.00}";
    }

    private Integer crearPresentacion(Productos p, String nombre, String factor) throws Exception {
        String r = mockMvc.perform(post("/api/minimarket/productos/" + p.getIdproducto() + "/presentaciones")
                .header("Authorization", bearer).contentType(MediaType.APPLICATION_JSON).content(presentacionJson(nombre, factor)))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        return Integer.valueOf(r.replaceAll("(?s).*\"idPresentacion\":(\\d+).*", "$1"));
    }

    private String ventaJson(Integer idProducto, String cantidad, Integer idPresentacion, String precio, String subtotal) {
        return "{\"idSucursal\":" + idSucursal + ",\"id_cliente\":" + idCliente + ",\"id_metodo_pago\":" + idMetodoPago
                + ",\"tipo_comprobante\":\"BOLETA\",\"total_venta\":" + subtotal + ",\"estado\":1,\"estadoPago\":\"REALIZADO\","
                + "\"montoPagado\":" + subtotal + ",\"detalles\":[{\"id_producto\":" + idProducto + ",\"cantidad\":" + cantidad
                + (idPresentacion != null ? ",\"idPresentacion\":" + idPresentacion : "")
                + ",\"pecioUnitario\":" + precio + ",\"subTotal\":" + subtotal + ",\"estado\":1}]}";
    }

    @Test
    void crearListarEditarYEliminarUnaPresentacion() throws Exception {
        mockMvc.perform(post("/api/minimarket/productos/" + arroz.getIdproducto() + "/presentaciones")
                .header("Authorization", bearer).contentType(MediaType.APPLICATION_JSON)
                .content(presentacionJson("Saco 50 kg", "50")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Saco 50 kg"))
                .andExpect(jsonPath("$.factor").value(50))
                .andExpect(jsonPath("$.unidadBase").value("KG"))
                .andExpect(jsonPath("$.equivalencia").value("1 Saco 50 kg = 50 KG"));

        Integer id = crearPresentacion(arroz, "Medio saco", "25");
        mockMvc.perform(get("/api/minimarket/productos/" + arroz.getIdproducto() + "/presentaciones").header("Authorization", bearer))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nombre").value("Medio saco"))   // ordenadas por factor
                .andExpect(jsonPath("$[1].nombre").value("Saco 50 kg"));

        mockMvc.perform(put("/api/minimarket/presentaciones").header("Authorization", bearer)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"idPresentacion\":" + id + ",\"nombre\":\"Saco 25 kg\",\"factor\":25,\"precioVenta\":95.5}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Saco 25 kg"))
                .andExpect(jsonPath("$.precioVenta").value(95.5));

        mockMvc.perform(delete("/api/minimarket/presentaciones/" + id).header("Authorization", bearer))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/minimarket/productos/" + arroz.getIdproducto() + "/presentaciones").header("Authorization", bearer))
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void validacionesDeLaPresentacion() throws Exception {
        String url = "/api/minimarket/productos/" + arroz.getIdproducto() + "/presentaciones";
        mockMvc.perform(post(url).header("Authorization", bearer).contentType(MediaType.APPLICATION_JSON)
                .content("{\"nombre\":\" \",\"factor\":0}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.nombre").exists())
                .andExpect(jsonPath("$.errors.factor").exists());

        crearPresentacion(arroz, "Saco 50 kg", "50");
        mockMvc.perform(post(url).header("Authorization", bearer).contentType(MediaType.APPLICATION_JSON)
                .content(presentacionJson("saco 50 KG", "50")))
                .andExpect(status().isConflict());   // mismo nombre sin importar mayusculas

        // un producto de unidad entera no admite un factor con decimales
        mockMvc.perform(post("/api/minimarket/productos/" + galletas.getIdproducto() + "/presentaciones")
                .header("Authorization", bearer).contentType(MediaType.APPLICATION_JSON)
                .content(presentacionJson("Paquete raro", "0.5")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.factor").exists());

        mockMvc.perform(get("/api/minimarket/productos/99999/presentaciones").header("Authorization", bearer))
                .andExpect(status().isNotFound());
    }

    @Test
    void soloQuienTieneElModuloProductosEscribe() throws Exception {
        String cajero = "Bearer " + token("empresa", "1234"); // EMPRESA si puede
        mockMvc.perform(post("/api/minimarket/productos/" + arroz.getIdproducto() + "/presentaciones")
                .header("Authorization", cajero).contentType(MediaType.APPLICATION_JSON)
                .content(presentacionJson("Saco", "50"))).andExpect(status().isCreated());
        // SUPERADMIN no maneja datos operativos
        mockMvc.perform(get("/api/minimarket/productos/" + arroz.getIdproducto() + "/presentaciones")
                .header("Authorization", "Bearer " + token("alez", "1234"))).andExpect(status().isForbidden());
    }

    @Test
    void lasUnidadesSembradasIndicanSiAdmitenDecimales() throws Exception {
        mockMvc.perform(get("/api/minimarket/unidad_medida").header("Authorization", bearer))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.codigo=='KG')].permiteDecimales").value(true))
                .andExpect(jsonPath("$[?(@.codigo=='UND')].permiteDecimales").value(false));
    }

    @Test
    void lasUnidadesSoloLasModificaElSuperadminPeroTodosLasConsultan() throws Exception {
        String cuerpo = "{\"nombre\":\"Docena\",\"codigo\":\"DOC\",\"permiteDecimales\":false}";
        mockMvc.perform(post("/api/minimarket/unidad_medida").header("Authorization", bearer)
                .contentType(MediaType.APPLICATION_JSON).content(cuerpo)).andExpect(status().isForbidden());

        String superadmin = "Bearer " + token("alez", "1234");
        mockMvc.perform(get("/api/minimarket/unidad_medida").header("Authorization", superadmin))
                .andExpect(status().isOk());
        mockMvc.perform(post("/api/minimarket/unidad_medida").header("Authorization", superadmin)
                .contentType(MediaType.APPLICATION_JSON).content(cuerpo)).andExpect(status().is2xxSuccessful());

        for (String ruta : new String[] {"categorias", "tipoproducto"}) {
            mockMvc.perform(post("/api/minimarket/" + ruta).header("Authorization", bearer)
                    .contentType(MediaType.APPLICATION_JSON).content("{\"nombre\":\"X\"}")).andExpect(status().isForbidden());
            mockMvc.perform(get("/api/minimarket/" + ruta).header("Authorization", bearer)).andExpect(status().isOk());
            mockMvc.perform(post("/api/minimarket/" + ruta).header("Authorization", superadmin)
                    .contentType(MediaType.APPLICATION_JSON).content("{\"nombre\":\"X\"}")).andExpect(status().is2xxSuccessful());
        }
    }

    @Test
    void productoConCodigosMarcaYStockMinimoNoPierdeStockAlEditarse() throws Exception {
        // las categorias y tipos son catalogos de la plataforma ya sembrados: se toman del catalogo
        String cat = mockMvc.perform(get("/api/minimarket/categorias").header("Authorization", bearer))
                .andReturn().getResponse().getContentAsString();
        String tipo = mockMvc.perform(get("/api/minimarket/tipoproducto").header("Authorization", bearer))
                .andReturn().getResponse().getContentAsString();
        String idCat = cat.replaceAll("(?s)^\\[\\{\"idcategoria\":(\\d+).*", "$1");
        String idTipo = tipo.replaceAll("(?s)^\\[\\{\"idtipoproducto\":(\\d+).*", "$1");
        String base = "\"idcategoria\":" + idCat + ",\"idtipoproducto\":" + idTipo + ",\"idunidadmedida\":"
                + arroz.getUnidadMedida().getIdunidadmedida();

        String creado = mockMvc.perform(post("/api/minimarket/productos").header("Authorization", bearer)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nombre\":\"Arroz Costeno\",\"codigo\":\"ARR-001\",\"codigoBarras\":\"7750001\",\"marca\":\"Costeno\","
                        + "\"stockMinimo\":20,\"costoCompra\":3.2,\"costoVenta\":4.5," + base + "}"))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.afectoIgv").value(true))
                .andReturn().getResponse().getContentAsString();
        String id = creado.replaceAll("(?s).*\"idproducto\":(\\d+).*", "$1");

        // el mismo codigo o codigo de barras en otro producto se rechaza
        mockMvc.perform(post("/api/minimarket/productos").header("Authorization", bearer)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nombre\":\"Otro\",\"codigo\":\"arr-001\"," + base + "}")).andExpect(status().isConflict());
        mockMvc.perform(post("/api/minimarket/productos").header("Authorization", bearer)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nombre\":\"Otro\",\"codigoBarras\":\"7750001\"," + base + "}")).andExpect(status().isConflict());

        // editar sin enviar inventario conserva el stock del producto de las pruebas
        mockMvc.perform(put("/api/minimarket/productos").header("Authorization", bearer)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"idproducto\":" + arroz.getIdproducto() + ",\"nombre\":\"Arroz extra v2\",\"marca\":\"Nueva\"," + base + "}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.marca").value("Nueva"));
        assertEquals(0, stockActualArroz().compareTo(new BigDecimal("200")));
        mockMvc.perform(get("/api/minimarket/productos/" + id).header("Authorization", bearer))
                .andExpect(jsonPath("$.codigo").value("ARR-001")).andExpect(jsonPath("$.stockMinimo").value(20));
    }

    @Test
    void laCompraSumaStockLaAnulacionLoDevuelveYElStockBajoSeAvisa() throws Exception {
        com.gadbacorp.api.entity.compras.Proveedores prov = new com.gadbacorp.api.entity.compras.Proveedores();
        prov.setNombre("Proveedor stock");
        prov.setEstado(1);
        prov = proveedores.save(prov);
        Integer saco = crearPresentacion(arroz, "Saco 50 kg", "50");

        // 2 sacos de 50 kg: el stock pasa de 200 a 300 kg
        String creada = mockMvc.perform(post("/api/minimarket/compras").header("Authorization", bearer)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"idProveedor\":" + prov.getIdProveedor() + ",\"idMetodoPago\":" + idMetodoPago
                        + ",\"idSucursal\":" + idSucursal + ",\"detalles\":[{\"idProducto\":" + arroz.getIdproducto()
                        + ",\"cantidad\":2,\"idPresentacion\":" + saco + ",\"precioCompra\":150,\"precioVenta\":180}]}"))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        assertEquals(0, stockActualArroz().compareTo(new BigDecimal("300")));
        String idCompra = creada.replaceAll("(?s).*\"idCompra\":(\\d+).*", "$1");

        // editar la compra a 1 saco devuelve 50 kg
        mockMvc.perform(put("/api/minimarket/compras").header("Authorization", bearer)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"idCompra\":" + idCompra + ",\"idProveedor\":" + prov.getIdProveedor() + ",\"idMetodoPago\":" + idMetodoPago
                        + ",\"idSucursal\":" + idSucursal + ",\"detalles\":[{\"idProducto\":" + arroz.getIdproducto()
                        + ",\"cantidad\":1,\"idPresentacion\":" + saco + ",\"precioCompra\":150,\"precioVenta\":180}]}"))
                .andExpect(status().isOk());
        assertEquals(0, stockActualArroz().compareTo(new BigDecimal("250")));

        // anular la compra restituye el stock original
        mockMvc.perform(delete("/api/minimarket/compras/" + idCompra).header("Authorization", bearer))
                .andExpect(status().isOk());
        assertEquals(0, stockActualArroz().compareTo(new BigDecimal("200")));

        // sin inventario en la sucursal no se puede comprar un producto nuevo y no queda nada a medias
        mockMvc.perform(post("/api/minimarket/compras").header("Authorization", bearer)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"idProveedor\":" + prov.getIdProveedor() + ",\"idMetodoPago\":" + idMetodoPago
                        + ",\"idSucursal\":" + idSucursal + ",\"detalles\":[{\"idProducto\":" + galletas.getIdproducto()
                        + ",\"cantidad\":1.5,\"precioCompra\":2,\"precioVenta\":3}]}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void stockBajoListaLosProductosPorDebajoDelMinimo() throws Exception {
        arroz.setStockMinimo(new BigDecimal("250"));
        productos.save(arroz);
        galletas.setStockMinimo(new BigDecimal("10"));
        productos.save(galletas);

        mockMvc.perform(get("/api/minimarket/productos/stock-bajo?idSucursal=" + idSucursal).header("Authorization", bearer))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.nombre=='Arroz extra')].stockActual").value(200.0))
                .andExpect(jsonPath("$[?(@.nombre=='Galletas')]").isEmpty());
    }

    @Test
    void devolverUnaVentaRestituyeUnaSolaVezElStockAunConVariosInventarios() throws Exception {
        // un segundo inventario con el mismo producto en la misma sucursal: antes la devolucion sumaba a ambos
        Inventario otro = new Inventario();
        otro.setNombre("Inventario 2");
        otro.setAlmacen(stockArroz.getInventario().getAlmacen());
        otro.setEstado(1);
        otro = inventarios.save(otro);
        stock(otro, arroz, "50.000");
        java.util.function.Supplier<BigDecimal> total = () -> stocks.findAll().stream()
                .filter(x -> x.getProducto().getIdproducto().equals(arroz.getIdproducto()))
                .map(InventarioProducto::stockOCero).reduce(BigDecimal.ZERO, BigDecimal::add);
        assertEquals(0, total.get().compareTo(new BigDecimal("250")));

        String respuesta = mockMvc.perform(post("/api/minimarket/ventas").header("Authorization", bearer)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ventaJson(arroz.getIdproducto(), "10", null, "4.00", "40.00")))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        assertEquals(0, total.get().compareTo(new BigDecimal("240")));
        String idVenta = respuesta.replaceAll("(?s).*\"id_venta\":(\\d+).*", "$1");

        String dev = mockMvc.perform(post("/api/minimarket/devoluciones").header("Authorization", bearer)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id_venta\":" + idVenta + ",\"motivoDevolucion\":\"prueba\",\"montoDevuelto\":40}"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        assertEquals(0, total.get().compareTo(new BigDecimal("250")));   // vuelven 10 kg, no 20

        String idDev = dev.replaceAll("(?s).*\"idDevolucion\":(\\d+).*", "$1");
        mockMvc.perform(delete("/api/minimarket/devoluciones/" + idDev).header("Authorization", bearer))
                .andExpect(status().isOk());
        assertEquals(0, total.get().compareTo(new BigDecimal("240")));
    }

    @Test
    void cadaEmpresaSoloVeYModificaLoSuyo() throws Exception {
        // otra empresa con su sucursal, cliente, producto y proveedor
        com.gadbacorp.api.entity.administrable.Empresas otra = new com.gadbacorp.api.entity.administrable.Empresas();
        otra.setRazonsocial("Otra SAC");
        otra.setEstado(1);
        otra = empresas.save(otra);
        Sucursales sucursalAjena = new Sucursales();
        sucursalAjena.setNombreSucursal("Sucursal ajena");
        sucursalAjena.setEmpresa(otra);
        sucursalAjena.setEstado(1);
        sucursalAjena = sucursalesRepo.save(sucursalAjena);
        Clientes clienteAjeno = new Clientes();
        clienteAjeno.setNombre("Cliente ajeno");
        clienteAjeno.setTipoDocumento("DNI");
        clienteAjeno.setDocumento("87654321");
        clienteAjeno.setSucursal(sucursalAjena);
        clienteAjeno = clientes.save(clienteAjeno);
        Productos productoAjeno = producto("Producto ajeno", unidad("KG"));
        productoAjeno.setEmpresa(otra);
        productoAjeno = productos.save(productoAjeno);

        // listados: solo lo propio
        mockMvc.perform(get("/api/minimarket/clientes").header("Authorization", bearer))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.nombre=='Cliente prueba')]").isNotEmpty())
                .andExpect(jsonPath("$[?(@.nombre=='Cliente ajeno')]").isEmpty());
        mockMvc.perform(get("/api/minimarket/productos").header("Authorization", bearer))
                .andExpect(jsonPath("$[?(@.nombre=='Arroz extra')]").isNotEmpty())
                .andExpect(jsonPath("$[?(@.nombre=='Producto ajeno')]").isEmpty());
        mockMvc.perform(get("/api/minimarket/sucursales").header("Authorization", bearer))
                .andExpect(jsonPath("$[?(@.nombreSucursal=='Sucursal ajena')]").isEmpty());

        // por id y por sucursal: 404 como si no existieran
        mockMvc.perform(get("/api/minimarket/clientes/" + clienteAjeno.getIdCliente()).header("Authorization", bearer))
                .andExpect(status().isNotFound());
        mockMvc.perform(get("/api/minimarket/productos/" + productoAjeno.getIdproducto()).header("Authorization", bearer))
                .andExpect(status().isNotFound());
        mockMvc.perform(get("/api/minimarket/clientes/sucursal/" + sucursalAjena.getIdSucursal()).header("Authorization", bearer))
                .andExpect(status().isNotFound());
        mockMvc.perform(delete("/api/minimarket/clientes/" + clienteAjeno.getIdCliente()).header("Authorization", bearer))
                .andExpect(status().isNotFound());
        mockMvc.perform(delete("/api/minimarket/productos/" + productoAjeno.getIdproducto()).header("Authorization", bearer))
                .andExpect(status().isNotFound());

        // escribir apuntando a otra empresa: prohibido
        mockMvc.perform(post("/api/minimarket/ventas").header("Authorization", bearer)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"idSucursal\":" + sucursalAjena.getIdSucursal() + ",\"id_cliente\":" + idCliente + ",\"id_metodo_pago\":"
                        + idMetodoPago + ",\"tipo_comprobante\":\"BOLETA\",\"total_venta\":4,\"estado\":1,\"detalles\":[]}"))
                .andExpect(status().isForbidden());
        // y vender un producto del catalogo de otra empresa tambien
        mockMvc.perform(post("/api/minimarket/ventas").header("Authorization", bearer)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ventaJson(productoAjeno.getIdproducto(), "1", null, "4.00", "4.00")))
                .andExpect(status().isNotFound());
    }

    @Test
    void elKardexRegistraCadaMovimientoYLaVentaDesglosaElIgv() throws Exception {
        com.gadbacorp.api.entity.compras.Proveedores prov = new com.gadbacorp.api.entity.compras.Proveedores();
        prov.setNombre("Proveedor kardex");
        prov.setEstado(1);
        prov = proveedores.save(prov);
        Integer saco = crearPresentacion(arroz, "Saco 50 kg", "50");

        mockMvc.perform(post("/api/minimarket/compras").header("Authorization", bearer)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"idProveedor\":" + prov.getIdProveedor() + ",\"idMetodoPago\":" + idMetodoPago
                        + ",\"idSucursal\":" + idSucursal + ",\"detalles\":[{\"idProducto\":" + arroz.getIdproducto()
                        + ",\"cantidad\":2,\"idPresentacion\":" + saco + ",\"precioCompra\":150,\"precioVenta\":180}]}"))
                .andExpect(status().isCreated());

        // 10 kg a 5.90 el kilo = 59.00 con IGV incluido: base 50.00 + IGV 9.00
        String respuesta = mockMvc.perform(post("/api/minimarket/ventas").header("Authorization", bearer)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ventaJson(arroz.getIdproducto(), "10", null, "5.90", "59.00")))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        String idVenta = respuesta.replaceAll("(?s).*\"id_venta\":(\\d+).*", "$1");

        mockMvc.perform(get("/api/minimarket/ventas/" + idVenta).header("Authorization", bearer))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.opGravada").value(50.0))
                .andExpect(jsonPath("$.igv").value(9.0))
                .andExpect(jsonPath("$.opInafecta").value(0.0));

        // kardex: lo mas reciente primero, con el stock en que quedo la sucursal
        mockMvc.perform(get("/api/minimarket/kardex/productos/" + arroz.getIdproducto()).header("Authorization", bearer))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tipo").value("VENTA"))
                .andExpect(jsonPath("$[0].cantidadBase").value(-10.0))
                .andExpect(jsonPath("$[0].stockResultante").value(290.0))
                .andExpect(jsonPath("$[1].tipo").value("COMPRA"))
                .andExpect(jsonPath("$[1].cantidadBase").value(100.0))
                .andExpect(jsonPath("$[1].stockResultante").value(300.0))
                .andExpect(jsonPath("$[1].usuario").value("empresa"));

        // anular la venta devuelve el stock y queda anotado
        mockMvc.perform(delete("/api/minimarket/ventas/" + idVenta).header("Authorization", bearer))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/minimarket/kardex/productos/" + arroz.getIdproducto()).header("Authorization", bearer))
                .andExpect(jsonPath("$[0].tipo").value("ANULACION_VENTA"))
                .andExpect(jsonPath("$[0].stockResultante").value(300.0));

        // un producto de otra empresa no tiene kardex visible
        mockMvc.perform(get("/api/minimarket/kardex/productos/999999").header("Authorization", bearer))
                .andExpect(status().isNotFound());
    }

    private void comprarSacoConLote(Integer idProveedor, Integer idPresentacion, String lote, java.time.LocalDate vence,
                                    org.springframework.test.web.servlet.ResultMatcher esperado) throws Exception {
        mockMvc.perform(post("/api/minimarket/compras").header("Authorization", bearer)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"idProveedor\":" + idProveedor + ",\"idMetodoPago\":" + idMetodoPago
                        + ",\"idSucursal\":" + idSucursal + ",\"detalles\":[{\"idProducto\":" + arroz.getIdproducto()
                        + ",\"cantidad\":1,\"idPresentacion\":" + idPresentacion + ",\"precioCompra\":150,\"precioVenta\":180,"
                        + "\"lote\":\"" + lote + "\",\"fechaVencimiento\":\"" + vence + "\"}]}"))
                .andExpect(esperado);
    }

    @Test
    void lasComprasCreanLotesQueSeConsumenPrimeroElQueVenceAntes() throws Exception {
        com.gadbacorp.api.entity.compras.Proveedores prov = new com.gadbacorp.api.entity.compras.Proveedores();
        prov.setNombre("Proveedor lotes");
        prov.setEstado(1);
        prov = proveedores.save(prov);
        Integer saco = crearPresentacion(arroz, "Saco 50 kg", "50");
        java.time.LocalDate hoy = java.time.LocalDate.now();

        // un lote que vence ayer no se puede comprar
        comprarSacoConLote(prov.getIdProveedor(), saco, "VIEJO", hoy.minusDays(1), status().isBadRequest());
        // dos lotes: A vence en 10 dias, B en 40 (se compra primero el que vence despues, el orden no importa)
        comprarSacoConLote(prov.getIdProveedor(), saco, "B", hoy.plusDays(40), status().isCreated());
        comprarSacoConLote(prov.getIdProveedor(), saco, "A", hoy.plusDays(10), status().isCreated());
        assertEquals(0, stockActualArroz().compareTo(new BigDecimal("300")));

        mockMvc.perform(get("/api/minimarket/lotes/productos/" + arroz.getIdproducto()).header("Authorization", bearer))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].codigo").value("A"))
                .andExpect(jsonPath("$[0].cantidadDisponible").value(50.0))
                .andExpect(jsonPath("$[0].diasParaVencer").value(10))
                .andExpect(jsonPath("$[1].codigo").value("B"));

        // por vencer en 30 dias: solo A
        mockMvc.perform(get("/api/minimarket/lotes/por-vencer?dias=30&idSucursal=" + idSucursal).header("Authorization", bearer))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.codigo=='A')]").isNotEmpty())
                .andExpect(jsonPath("$[?(@.codigo=='B')]").isEmpty());

        // se venden 60 kg: se agota A (50) y se toman 10 de B
        mockMvc.perform(post("/api/minimarket/ventas").header("Authorization", bearer)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ventaJson(arroz.getIdproducto(), "60", null, "5.90", "354.00")))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/minimarket/lotes/productos/" + arroz.getIdproducto()).header("Authorization", bearer))
                .andExpect(jsonPath("$[0].codigo").value("A"))
                .andExpect(jsonPath("$[0].cantidadDisponible").value(0.0))
                .andExpect(jsonPath("$[1].codigo").value("B"))
                .andExpect(jsonPath("$[1].cantidadDisponible").value(40.0));
        mockMvc.perform(get("/api/minimarket/lotes/por-vencer?dias=30").header("Authorization", bearer))
                .andExpect(jsonPath("$[?(@.codigo=='A')]").isEmpty());

        // anular la compra del lote B devuelve solo lo que no se vendio de ese lote
        mockMvc.perform(get("/api/minimarket/lotes/productos/999999").header("Authorization", bearer))
                .andExpect(status().isNotFound());
    }

    @Test
    void vendeSacosYKilosSueltosSobreElMismoStock() throws Exception {
        Integer saco = crearPresentacion(arroz, "Saco 50 kg", "50");

        // 2 sacos = 100 kg
        mockMvc.perform(post("/api/minimarket/ventas").header("Authorization", bearer).contentType(MediaType.APPLICATION_JSON)
                .content(ventaJson(arroz.getIdproducto(), "2", saco, "180.00", "360.00")))
                .andExpect(status().isOk()).andExpect(jsonPath("$.status").value(200));
        assertEquals(0, stockActualArroz().compareTo(new BigDecimal("100")));

        // 2.5 kg sueltos
        mockMvc.perform(post("/api/minimarket/ventas").header("Authorization", bearer).contentType(MediaType.APPLICATION_JSON)
                .content(ventaJson(arroz.getIdproducto(), "2.5", null, "4.00", "10.00")))
                .andExpect(status().isOk());
        assertEquals(0, stockActualArroz().compareTo(new BigDecimal("97.5")));

        // medio saco = 25 kg
        mockMvc.perform(post("/api/minimarket/ventas").header("Authorization", bearer).contentType(MediaType.APPLICATION_JSON)
                .content(ventaJson(arroz.getIdproducto(), "0.5", saco, "180.00", "90.00")))
                .andExpect(status().isOk());
        assertEquals(0, stockActualArroz().compareTo(new BigDecimal("72.5")));
    }

    @Test
    void noSeVendeMasDeLoQueHayYNoSeDescuentaNada() throws Exception {
        Integer saco = crearPresentacion(arroz, "Saco 50 kg", "50");

        // hay 200 kg: 5 sacos son 250 kg
        mockMvc.perform(post("/api/minimarket/ventas").header("Authorization", bearer).contentType(MediaType.APPLICATION_JSON)
                .content(ventaJson(arroz.getIdproducto(), "5", saco, "180.00", "900.00")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.Detalle").value(org.hamcrest.Matchers.containsString("Disponible: 200")));
        assertEquals(0, stockActualArroz().compareTo(new BigDecimal("200")));
    }

    @Test
    void unProductoEnteroRechazaFraccionesPeroAceptaPaquetes() throws Exception {
        Integer paquete = crearPresentacion(galletas, "Paquete x12", "12");

        mockMvc.perform(post("/api/minimarket/ventas").header("Authorization", bearer).contentType(MediaType.APPLICATION_JSON)
                .content(ventaJson(galletas.getIdproducto(), "1.5", null, "2.00", "3.00")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.Detalle").value(org.hamcrest.Matchers.containsString("unidades enteras")));

        mockMvc.perform(post("/api/minimarket/ventas").header("Authorization", bearer).contentType(MediaType.APPLICATION_JSON)
                .content(ventaJson(galletas.getIdproducto(), "3", paquete, "20.00", "60.00")))
                .andExpect(status().isOk());
        BigDecimal quedan = stocks.findAll().stream()
                .filter(s -> s.getProducto().getIdproducto().equals(galletas.getIdproducto()))
                .findFirst().orElseThrow().getStockactual();
        assertEquals(0, quedan.compareTo(new BigDecimal("64")));   // 100 - 3 x 12
    }

    @Test
    void anularLaVentaDevuelveElStockEnUnidadBase() throws Exception {
        Integer saco = crearPresentacion(arroz, "Saco 50 kg", "50");
        String respuesta = mockMvc.perform(post("/api/minimarket/ventas").header("Authorization", bearer)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ventaJson(arroz.getIdproducto(), "2", saco, "180.00", "360.00")))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        assertEquals(0, stockActualArroz().compareTo(new BigDecimal("100")));
        String idVenta = respuesta.replaceAll("(?s).*\"id_venta\":(\\d+).*", "$1");

        mockMvc.perform(delete("/api/minimarket/ventas/" + idVenta).header("Authorization", bearer))
                .andExpect(status().isOk());

        assertEquals(0, stockActualArroz().compareTo(new BigDecimal("200")));   // vuelven los 100 kg, no 2 unidades
    }

    @Test
    void ajustesYTrasladosRespetanLosDecimalesDeLaUnidad() throws Exception {
        // kg: admite 0.75
        mockMvc.perform(post("/api/minimarket/ajustes").header("Authorization", bearer).contentType(MediaType.APPLICATION_JSON)
                .content("{\"idinventarioproducto\":" + stockArroz.getIdinventarioproducto() + ",\"cantidad\":-0.75,\"descripcion\":\"merma\"}"))
                .andExpect(status().isCreated());
        assertEquals(0, stockActualArroz().compareTo(new BigDecimal("199.25")));

        // unidades: no admite 0.5
        Integer idStockGalletas = stocks.findAll().stream()
                .filter(s -> s.getProducto().getIdproducto().equals(galletas.getIdproducto()))
                .findFirst().orElseThrow().getIdinventarioproducto();
        mockMvc.perform(post("/api/minimarket/ajustes").header("Authorization", bearer).contentType(MediaType.APPLICATION_JSON)
                .content("{\"idinventarioproducto\":" + idStockGalletas + ",\"cantidad\":-0.5,\"descripcion\":\"x\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void editarLasLineasDeUnaCompraMantieneElTotal() throws Exception {
        com.gadbacorp.api.entity.compras.Proveedores prov = new com.gadbacorp.api.entity.compras.Proveedores();
        prov.setNombre("Proveedor test");
        prov.setEstado(1);
        prov = proveedores.save(prov);
        Integer saco = crearPresentacion(arroz, "Saco 50 kg", "50");

        String creada = mockMvc.perform(post("/api/minimarket/compras").header("Authorization", bearer)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"idProveedor\":" + prov.getIdProveedor() + ",\"idMetodoPago\":" + idMetodoPago
                        + ",\"idSucursal\":" + idSucursal + ",\"detalles\":[{\"idProducto\":" + arroz.getIdproducto()
                        + ",\"cantidad\":2,\"idPresentacion\":" + saco + ",\"precioCompra\":150,\"precioVenta\":180}]}"))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        String idCompra = creada.replaceAll("(?s).*\"idCompra\":(\\d+).*", "$1");
        mockMvc.perform(get("/api/minimarket/compras/" + idCompra).header("Authorization", bearer))
                .andExpect(jsonPath("$.compra.total").value(300.0));

        // agregar una linea de 1 saco a 150 -> 450
        String linea = mockMvc.perform(post("/api/minimarket/detalles-compras").header("Authorization", bearer)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"idCompra\":" + idCompra + ",\"idProducto\":" + arroz.getIdproducto()
                        + ",\"cantidad\":1,\"idPresentacion\":" + saco + ",\"precioUnitario\":150,\"precioCompra\":150,\"precioVenta\":180}"))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        String idDetalle = linea.replaceAll("(?s).*\"idDetalleCompra\":(\\d+).*", "$1");
        mockMvc.perform(get("/api/minimarket/compras/" + idCompra).header("Authorization", bearer))
                .andExpect(jsonPath("$.compra.total").value(450.0));

        // quitar esa linea -> 300
        mockMvc.perform(delete("/api/minimarket/detalles-compras/" + idDetalle).header("Authorization", bearer))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/minimarket/compras/" + idCompra).header("Authorization", bearer))
                .andExpect(jsonPath("$.compra.total").value(300.0));
    }
}
