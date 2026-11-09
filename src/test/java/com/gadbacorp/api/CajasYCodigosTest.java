package com.gadbacorp.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Date;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import com.gadbacorp.api.config.ReparadorCajasRunner;
import com.gadbacorp.api.entity.administrable.Empresas;
import com.gadbacorp.api.entity.administrable.Sucursales;
import com.gadbacorp.api.entity.caja.AperturaCaja;
import com.gadbacorp.api.entity.caja.Caja;
import com.gadbacorp.api.entity.empleados.Usuarios;
import com.gadbacorp.api.repository.administrable.EmpresasRepository;
import com.gadbacorp.api.repository.administrable.SucursalesRepository;
import com.gadbacorp.api.repository.caja.AperturaCajaRepository;
import com.gadbacorp.api.repository.caja.CajaRepository;
import com.gadbacorp.api.repository.empleados.UsuarioRepository;
import com.gadbacorp.api.repository.inventario.ProductoPresentacionRepository;
import com.gadbacorp.api.repository.inventario.ProductosRepository;
import com.gadbacorp.api.repository.seguridad.RolRepository;
import com.gadbacorp.api.repository.ventas.ClientesRepository;
import com.gadbacorp.api.repository.ventas.MetodosPagoRepository;

/** Turnos de caja (una caja, un turno, un cajero), quien cobra sobre que caja y la busqueda por codigo del lector. */
@SpringBootTest(properties = {
        "app.seed.demo=true",
        "spring.datasource.url=jdbc:h2:mem:cajasycodigos;MODE=MySQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1"})
@AutoConfigureMockMvc
class CajasYCodigosTest {

    @Autowired MockMvc mockMvc;
    @Autowired UsuarioRepository usuarios;
    @Autowired CajaRepository cajas;
    @Autowired AperturaCajaRepository aperturas;
    @Autowired ClientesRepository clientes;
    @Autowired MetodosPagoRepository metodosPago;
    @Autowired ProductosRepository productos;
    @Autowired ProductoPresentacionRepository presentaciones;
    @Autowired RolRepository roles;
    @Autowired EmpresasRepository empresas;
    @Autowired SucursalesRepository sucursales;
    @Autowired PasswordEncoder encoder;
    @Autowired ReparadorCajasRunner reparador;

    private String token(String usuario) throws Exception {
        String r = mockMvc.perform(post("/api/minimarket/login").contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"" + usuario + "\",\"password\":\"1234\"}"))
                .andReturn().getResponse().getContentAsString();
        return "Bearer " + r.replaceAll("(?s).*\"token\":\"([^\"]+)\".*", "$1");
    }

    private Usuarios crearUsuario(String username, String rol, Sucursales sucursal) {
        Usuarios u = new Usuarios();
        u.setUsername(username);
        u.setPassword(encoder.encode("1234"));
        u.setNombre(username);
        u.setApellidos("Prueba");
        u.setEmail(username + "@demo.local");
        u.setDni("1" + Math.abs(username.hashCode() % 10000000));
        u.setEstado(1);
        u.setEnable(true);
        u.setFechaCreacion(java.time.LocalDateTime.now());
        u.setRol(roles.findByNombre(rol).orElseThrow());
        u.setSucursal(sucursal);
        return usuarios.save(u);
    }

    private String aperturaJson(Caja caja, Usuarios empleado) {
        return "{\"id_caja\":" + caja.getIdCaja() + ",\"id_empleado\":" + empleado.getIdUsuario()
                + ",\"fechaApertura\":\"2026-11-09T10:00:00.000Z\",\"saldoInicial\":50,\"estadoCaja\":\"CERRADO\"}";
    }

    private String ventaJson(Sucursales s, Integer idApertura) {
        Integer cliente = clientes.findAll().stream().filter(c -> c.getSucursal() != null
                && c.getSucursal().getIdSucursal().equals(s.getIdSucursal())).findFirst().orElseThrow().getIdCliente();
        Integer metodo = metodosPago.findBySucursalIdSucursal(s.getIdSucursal()).stream()
                .filter(m -> "Efectivo".equals(m.getNombre())).findFirst().orElseThrow().getIdMetodoPago();
        Integer arroz = productos.findByCodigoIgnoreCaseAndEmpresa_Idempresa("ARR-001", s.getEmpresa().getIdempresa())
                .orElseThrow().getIdproducto();
        return "{\"idSucursal\":" + s.getIdSucursal() + ",\"id_cliente\":" + cliente + ",\"id_metodo_pago\":" + metodo
                + ",\"tipo_comprobante\":\"BOLETA\",\"total_venta\":4.2,\"estado\":1,\"estadoPago\":\"REALIZADO\",\"montoPagado\":4.2,"
                + "\"id_apertura_caja\":" + idApertura + ",\"detalles\":[{\"id_producto\":" + arroz
                + ",\"cantidad\":1,\"pecioUnitario\":4.2,\"subTotal\":4.2,\"estado\":1}]}";
    }

    @Test
    void unaCajaUnTurnoYQuienPuedeCobrarSobreCadaCaja() throws Exception {
        Usuarios empresaUsr = usuarios.findByUsername("empresa");
        Sucursales sucursal = empresaUsr.getSucursal();
        Caja caja1 = cajas.findAll().stream().filter(c -> "Caja 1".equals(c.getNombreCaja())
                && c.getSucursales().getIdSucursal().equals(sucursal.getIdSucursal())).findFirst().orElseThrow();
        Caja caja2 = cajas.findAll().stream().filter(c -> "Caja 2".equals(c.getNombreCaja())
                && c.getSucursales().getIdSucursal().equals(sucursal.getIdSucursal())).findFirst().orElseThrow();
        Usuarios cajero = crearUsuario("cajero1", "CAJERO", sucursal);
        String empresa = token("empresa");
        String delCajero = token("cajero1");

        // la Caja 1 ya esta en uso por "empresa" y "empresa" no puede abrir otra mientras tenga un turno abierto
        mockMvc.perform(post("/api/minimarket/aperturas-cajas").header("Authorization", delCajero)
                .contentType(MediaType.APPLICATION_JSON).content(aperturaJson(caja1, cajero)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("La Caja 1 ya está en uso por empresa."));
        mockMvc.perform(post("/api/minimarket/aperturas-cajas").header("Authorization", empresa)
                .contentType(MediaType.APPLICATION_JSON).content(aperturaJson(caja2, empresaUsr)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(Matchers.startsWith("Ya tienes una caja abierta (Caja 1)")));

        // el cajero abre la Caja 2 (el estado del turno lo fija el servidor, no el cliente)
        mockMvc.perform(post("/api/minimarket/aperturas-cajas").header("Authorization", delCajero)
                .contentType(MediaType.APPLICATION_JSON).content(aperturaJson(caja2, cajero)))
                .andExpect(status().isOk());
        AperturaCaja turnoCajero = aperturas.findByUsuarios_IdUsuarioAndEstadoCaja(cajero.getIdUsuario(), "ABIERTA").get(0);
        AperturaCaja turnoEmpresa = aperturas.findByUsuarios_IdUsuarioAndEstadoCaja(empresaUsr.getIdUsuario(), "ABIERTA").get(0);
        assertEquals("OCUPADA", cajas.findById(caja2.getIdCaja()).orElseThrow().getEstadoCaja());
        mockMvc.perform(post("/api/minimarket/aperturas-cajas").header("Authorization", delCajero)
                .contentType(MediaType.APPLICATION_JSON).content(aperturaJson(caja2, cajero)))
                .andExpect(status().isConflict());

        // los turnos abiertos de la sucursal, para elegir donde cobrar
        mockMvc.perform(get("/api/minimarket/aperturas-cajas/sucursal/" + sucursal.getIdSucursal() + "/abiertas").header("Authorization", delCajero))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[?(@.cajero=='cajero1')].esMia").value(true))
                .andExpect(jsonPath("$[?(@.cajero=='empresa')].esMia").value(false))
                .andExpect(jsonPath("$[?(@.cajero=='empresa')].nombreCaja").value("Caja 1"));

        // un cajero solo cobra en su propio turno; el administrador puede cobrar en el de otro
        mockMvc.perform(post("/api/minimarket/ventas").header("Authorization", delCajero)
                .contentType(MediaType.APPLICATION_JSON).content(ventaJson(sucursal, turnoEmpresa.getIdAperturaCaja())))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.Detalle").value(Matchers.startsWith("Solo puedes cobrar en tu propia caja")));
        mockMvc.perform(post("/api/minimarket/ventas").header("Authorization", delCajero)
                .contentType(MediaType.APPLICATION_JSON).content(ventaJson(sucursal, turnoCajero.getIdAperturaCaja())))
                .andExpect(status().isOk());
        mockMvc.perform(post("/api/minimarket/ventas").header("Authorization", empresa)
                .contentType(MediaType.APPLICATION_JSON).content(ventaJson(sucursal, turnoCajero.getIdAperturaCaja())))
                .andExpect(status().isOk());

        // la venta guarda quien cobro y el arqueo del turno se explica por vendedor
        mockMvc.perform(get("/api/minimarket/aperturas-cajas/" + turnoCajero.getIdAperturaCaja() + "/vendedores").header("Authorization", empresa))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[?(@.vendedor=='cajero1')].ventas").value(1))
                .andExpect(jsonPath("$[?(@.vendedor=='empresa')].ventas").value(1));
        mockMvc.perform(get("/api/minimarket/ventas").header("Authorization", empresa))
                .andExpect(jsonPath("$[?(@.vendedor=='cajero1')].idAperturaCaja").value(turnoCajero.getIdAperturaCaja()));

        // no se puede cobrar sobre el turno de otra empresa
        Empresas otra = new Empresas();
        otra.setRazonsocial("Otra SAC");
        otra.setEstado(1);
        otra = empresas.save(otra);
        Sucursales sucursalAjena = new Sucursales();
        sucursalAjena.setNombreSucursal("Ajena");
        sucursalAjena.setEmpresa(otra);
        sucursalAjena.setEstado(1);
        sucursalAjena = sucursales.save(sucursalAjena);
        Caja cajaAjena = new Caja();
        cajaAjena.setNombreCaja("Caja ajena");
        cajaAjena.setSaldoActual(0.0);
        cajaAjena.setEstado(1);
        cajaAjena.setEstadoCaja("OCUPADA");
        cajaAjena.setSucursales(sucursalAjena);
        cajaAjena = cajas.save(cajaAjena);
        AperturaCaja turnoAjeno = new AperturaCaja();
        turnoAjeno.setCaja(cajaAjena);
        turnoAjeno.setEstadoCaja("ABIERTA");
        turnoAjeno.setFechaApertura(new Date());
        turnoAjeno.setSaldoInicial(10.0);
        turnoAjeno.setSaldoEfectivo(0.0);
        turnoAjeno.setUsuarios(empresaUsr);
        turnoAjeno = aperturas.save(turnoAjeno);
        mockMvc.perform(post("/api/minimarket/ventas").header("Authorization", empresa)
                .contentType(MediaType.APPLICATION_JSON).content(ventaJson(sucursal, turnoAjeno.getIdAperturaCaja())))
                .andExpect(status().isBadRequest());

        // al arrancar se liberan las cajas ocupadas que no tienen ningun turno abierto
        Caja huerfana = new Caja();
        huerfana.setNombreCaja("Caja 3");
        huerfana.setSaldoActual(0.0);
        huerfana.setEstado(1);
        huerfana.setEstadoCaja("OCUPADA");
        huerfana.setSucursales(sucursal);
        huerfana = cajas.save(huerfana);
        reparador.run(null);
        assertEquals("LIBRE", cajas.findById(huerfana.getIdCaja()).orElseThrow().getEstadoCaja());
        assertEquals("OCUPADA", cajas.findById(caja1.getIdCaja()).orElseThrow().getEstadoCaja());
    }

    @Test
    void elLectorEncuentraElProductoPorCodigoDeBarrasSkuOPresentacion() throws Exception {
        String empresa = token("empresa");
        mockMvc.perform(get("/api/minimarket/productos/codigo/7750001000011").header("Authorization", empresa))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.coincidencia").value("CODIGO_BARRAS"))
                .andExpect(jsonPath("$.producto.codigo").value("ARR-001"))
                .andExpect(jsonPath("$.idPresentacion").doesNotExist());
        mockMvc.perform(get("/api/minimarket/productos/codigo/arr-001").header("Authorization", empresa))
                .andExpect(jsonPath("$.coincidencia").value("SKU"))
                .andExpect(jsonPath("$.producto.nombre").value("Arroz extra"));

        Integer idEmpresa = usuarios.findByUsername("empresa").getSucursal().getEmpresa().getIdempresa();
        Integer idArroz = productos.findByCodigoIgnoreCaseAndEmpresa_Idempresa("ARR-001", idEmpresa).orElseThrow().getIdproducto();
        var saco = presentaciones.findByProductoIdproductoOrderByFactorAsc(idArroz).get(0);
        saco.setCodigoBarras("775000SACO50");
        presentaciones.save(saco);
        mockMvc.perform(get("/api/minimarket/productos/codigo/775000SACO50").header("Authorization", empresa))
                .andExpect(jsonPath("$.coincidencia").value("PRESENTACION"))
                .andExpect(jsonPath("$.presentacion").value("Saco 50 kg"))
                .andExpect(jsonPath("$.idPresentacion").value(saco.getIdPresentacion()))
                .andExpect(jsonPath("$.producto.codigo").value("ARR-001"));

        mockMvc.perform(get("/api/minimarket/productos/codigo/NOEXISTE").header("Authorization", empresa))
                .andExpect(status().isNotFound());
    }
}
