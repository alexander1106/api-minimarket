package com.gadbacorp.api;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.gadbacorp.api.repository.empleados.UsuarioRepository;

/**
 * Arranca la aplicacion con los datos de demostracion (base propia) y comprueba que las pantallas tengan con que trabajar,
 * y que los metodos de pago son un catalogo del superadmin que cada sucursal solo activa.
 */
@SpringBootTest(properties = {
        "app.seed.demo=true",
        "spring.datasource.url=jdbc:h2:mem:demo;MODE=MySQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1"})
@AutoConfigureMockMvc
class DatosDemoYMetodosPagoTest {

    @Autowired MockMvc mockMvc;
    @Autowired UsuarioRepository usuarios;

    private String token(String usuario) throws Exception {
        String r = mockMvc.perform(post("/api/minimarket/login").contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"" + usuario + "\",\"password\":\"1234\"}"))
                .andReturn().getResponse().getContentAsString();
        return "Bearer " + r.replaceAll("(?s).*\"token\":\"([^\"]+)\".*", "$1");
    }

    @Test
    void laEmpresaDeDemostracionTieneDatosEnTodasLasPantallas() throws Exception {
        String empresa = token("empresa");
        Integer idSucursal = usuarios.findByUsername("empresa").getSucursal().getIdSucursal();

        mockMvc.perform(get("/api/minimarket/productos").header("Authorization", empresa))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(32))
                .andExpect(jsonPath("$[?(@.codigo=='ARR-001')].marca").value("Costeño"));
        mockMvc.perform(get("/api/minimarket/almacenes").header("Authorization", empresa))
                .andExpect(jsonPath("$.length()").value(2));
        mockMvc.perform(get("/api/minimarket/clientes").header("Authorization", empresa))
                .andExpect(jsonPath("$.length()").value(3));
        mockMvc.perform(get("/api/minimarket/proveedores").header("Authorization", empresa))
                .andExpect(jsonPath("$.length()").value(2));
        mockMvc.perform(get("/api/minimarket/compras").header("Authorization", empresa))
                .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(3));
        mockMvc.perform(get("/api/minimarket/ventas").header("Authorization", empresa))
                .andExpect(jsonPath("$.length()").value(11))
                .andExpect(jsonPath("$[0].igv").isNumber());
        mockMvc.perform(get("/api/minimarket/cotizaciones").header("Authorization", empresa))
                .andExpect(jsonPath("$.length()").value(3));
        mockMvc.perform(get("/api/minimarket/ajustes").header("Authorization", empresa))
                .andExpect(jsonPath("$.length()").value(1));
        mockMvc.perform(get("/api/minimarket/inventarioproducto").header("Authorization", empresa))
                .andExpect(status().isOk());

        // lotes: el yogurt (12 dias) y la leche (25) vencen dentro de 30 dias
        mockMvc.perform(get("/api/minimarket/lotes/por-vencer?dias=30&idSucursal=" + idSucursal).header("Authorization", empresa))
                .andExpect(jsonPath("$[?(@.codigo=='YOG-A')]").isNotEmpty())
                .andExpect(jsonPath("$[?(@.codigo=='LECHE-A')]").isNotEmpty())
                .andExpect(jsonPath("$[?(@.codigo=='GAL-A')]").isEmpty());

        // el stock sale de la compra: 4 sacos de arroz (200 kg) menos 2.5 kg y un saco vendidos = 147.5
        String productos = mockMvc.perform(get("/api/minimarket/productos").header("Authorization", empresa))
                .andReturn().getResponse().getContentAsString();
        String idArroz = productos.replaceAll("(?s).*\"idproducto\":(\\d+),[^}]*\"codigo\":\"ARR-001\".*", "$1");
        mockMvc.perform(get("/api/minimarket/kardex/productos/" + idArroz).header("Authorization", empresa))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tipo").value("VENTA"))
                .andExpect(jsonPath("$[0].stockResultante").value(147.5));
    }

    /** Tablas que no forman parte del flujo de negocio y pueden estar vacias (seguridad, tokens, registro de integraciones). */
    private static final java.util.Set<String> PUEDEN_ESTAR_VACIAS = java.util.Set.of(
            "PasswordResetTokenRepository", "RegistrosRepository", "RegistroRepository", "AuditoriaRepository");

    @Autowired org.springframework.beans.factory.ListableBeanFactory beans;

    @Test
    void todasLasTablasDelFlujoTienenDatos() {
        var vacias = new java.util.ArrayList<String>();
        beans.getBeansOfType(org.springframework.data.jpa.repository.JpaRepository.class).forEach((nombre, repo) -> {
            if (repo.count() == 0 && PUEDEN_ESTAR_VACIAS.stream().noneMatch(v -> nombre.equalsIgnoreCase(v.replace("Repository", "")) || nombre.equalsIgnoreCase(v))) {
                vacias.add(nombre);
            }
        });
        assertTrue(vacias.isEmpty(), "Tablas sin datos de demostracion: " + vacias);
    }

    @Test
    void todosLosListadosResponden200ConJsonValidoConDatosReales() throws Exception {
        String empresa = token("empresa");
        var fallos = new java.util.ArrayList<String>();
        for (String ruta : new String[] {"clientes", "ventas", "detalles-ventas", "cotizaciones", "detalles-cotizaciones",
                "devoluciones", "detalles-devoluciones", "compras", "detalles-compras", "proveedores", "productos",
                "categorias", "tipoproducto", "unidad_medida", "almacenes", "inventario", "inventarioproducto", "ajustes",
                "traslados", "promociones", "cajas", "aperturas-cajas", "transacciones-cajas", "arqueos-cajas",
                "pagos", "delivery", "vehiculo", "estadomovimiento", "metodos-pago", "usuarios",
                "sucursales", "metodos-pago-catalogo"}) {
            var r = mockMvc.perform(get("/api/minimarket/" + ruta).header("Authorization", empresa)).andReturn();
            int estado = r.getResponse().getStatus();
            String cuerpo = r.getResponse().getContentAsString();
            boolean bien = estado == 204 || estado == 200 && (cuerpo.startsWith("[") || cuerpo.startsWith("{"));
            if (!bien || r.getResolvedException() != null) {
                fallos.add(ruta + " -> " + estado + " " + r.getResolvedException());
            }
        }
        assertTrue(fallos.isEmpty(), "Listados con problemas: " + fallos);
    }

    @Autowired com.gadbacorp.api.config.DatosDemoRunner demo;
    @Autowired com.gadbacorp.api.config.DatosDemoAmpliadoRunner demoAmpliado;
    @Autowired com.gadbacorp.api.repository.administrable.EmpresasRepository empresas;
    @Autowired com.gadbacorp.api.repository.administrable.SucursalesRepository sucursales;
    @Autowired com.gadbacorp.api.repository.seguridad.RolRepository roles;
    @Autowired org.springframework.security.crypto.password.PasswordEncoder encoder;

    @Test
    void unaSegundaEmpresaRecibeSusPropiosDatosSinMezclarseConLaPrimera() throws Exception {
        var empresa2 = new com.gadbacorp.api.entity.administrable.Empresas();
        empresa2.setRazonsocial("Minimarket Gaslac");
        empresa2.setEstado(1);
        empresa2 = empresas.save(empresa2);
        var sucursal2 = new com.gadbacorp.api.entity.administrable.Sucursales();
        sucursal2.setNombreSucursal("Sede Principal");
        sucursal2.setEmpresa(empresa2);
        sucursal2.setEstado(1);
        sucursal2 = sucursales.save(sucursal2);
        var admin = new com.gadbacorp.api.entity.empleados.Usuarios();
        admin.setUsername("ale2");
        admin.setPassword(encoder.encode("1234"));
        admin.setNombre("Ale");
        admin.setApellidos("Dos");
        admin.setEmail("ale2@demo.local");
        admin.setDni("00000002");
        admin.setEstado(1);
        admin.setEnable(true);
        admin.setFechaCreacion(java.time.LocalDateTime.now());
        admin.setRol(roles.findByNombre("ADMIN").orElseThrow());
        admin.setSucursal(sucursal2);
        usuarios.save(admin);

        demo.run(null);
        demoAmpliado.run(null);

        String token2 = token("ale2");
        // la sucursal nueva no tenia metodos de pago ni caja: se activaron del catalogo
        mockMvc.perform(get("/api/minimarket/sucursales/" + sucursal2.getIdSucursal() + "/metodos-pago").header("Authorization", token2))
                .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(3));
        mockMvc.perform(get("/api/minimarket/productos").header("Authorization", token2))
                .andExpect(jsonPath("$.length()").value(32))
                .andExpect(jsonPath("$[?(@.codigo=='ARR-001')]").isNotEmpty());   // mismo SKU que la otra empresa
        mockMvc.perform(get("/api/minimarket/ventas").header("Authorization", token2))
                .andExpect(jsonPath("$.length()").value(11));
        mockMvc.perform(get("/api/minimarket/compras").header("Authorization", token2))
                .andExpect(jsonPath("$.length()").value(3));
        mockMvc.perform(get("/api/minimarket/clientes").header("Authorization", token2))
                .andExpect(jsonPath("$.length()").value(3));
        // inventarios y almacenes llegan como DTO con solo el id del padre: tampoco se mezclan entre empresas
        mockMvc.perform(get("/api/minimarket/almacenes").header("Authorization", token2))
                .andExpect(jsonPath("$.length()").value(2));
        mockMvc.perform(get("/api/minimarket/inventario").header("Authorization", token2))
                .andExpect(jsonPath("$.length()").value(2));
        mockMvc.perform(get("/api/minimarket/inventarioproducto").header("Authorization", token2))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/minimarket/traslados").header("Authorization", token2))
                .andExpect(jsonPath("$.length()").value(3));
        mockMvc.perform(get("/api/minimarket/cajas").header("Authorization", token2))
                .andExpect(jsonPath("$.length()").value(2));
        // y la primera empresa sigue viendo solo lo suyo
        mockMvc.perform(get("/api/minimarket/productos").header("Authorization", token("empresa")))
                .andExpect(jsonPath("$.length()").value(32));
    }

    @Test
    void elDetergenteAparecePorDebajoDelMinimo() throws Exception {
        String empresa = token("empresa");
        mockMvc.perform(get("/api/minimarket/productos/stock-bajo").header("Authorization", empresa))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.codigo=='LIM-001')]").isNotEmpty());
    }

    @Test
    void losMetodosDePagoSonUnCatalogoDelSuperadminQueLaSucursalSoloActiva() throws Exception {
        String empresa = token("empresa");
        String superadmin = token("alez");
        Integer idSucursal = usuarios.findByUsername("empresa").getSucursal().getIdSucursal();

        // todos consultan el catalogo; solo el superadmin lo modifica
        mockMvc.perform(get("/api/minimarket/metodos-pago-catalogo").header("Authorization", empresa))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.nombre=='Yape')].requiereDetalle").value(true));
        mockMvc.perform(post("/api/minimarket/metodos-pago-catalogo").header("Authorization", empresa)
                .contentType(MediaType.APPLICATION_JSON).content("{\"nombre\":\"Bitcoin\",\"tipo\":\"DIGITAL\"}"))
                .andExpect(status().isForbidden());
        mockMvc.perform(post("/api/minimarket/metodos-pago-catalogo").header("Authorization", superadmin)
                .contentType(MediaType.APPLICATION_JSON).content("{\"nombre\":\"Cheque\",\"tipo\":\"OTRO\"}"))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/api/minimarket/metodos-pago-catalogo").header("Authorization", superadmin)
                .contentType(MediaType.APPLICATION_JSON).content("{\"nombre\":\"cheque\"}"))
                .andExpect(status().isConflict());

        String lista = mockMvc.perform(get("/api/minimarket/metodos-pago-catalogo").header("Authorization", empresa))
                .andReturn().getResponse().getContentAsString();
        String idPlin = lista.replaceAll("(?s).*\"idCatalogo\":(\\d+),\"nombre\":\"Plin\".*", "$1");
        assertTrue(idPlin.matches("\\d+"), lista);

        // la sucursal ya tiene Efectivo, Yape y Tarjeta; activar Plin exige su numero
        mockMvc.perform(post("/api/minimarket/sucursales/" + idSucursal + "/metodos-pago/activar").header("Authorization", empresa)
                .contentType(MediaType.APPLICATION_JSON).content("{\"idCatalogo\":" + idPlin + "}"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.errors.detalle").exists());
        mockMvc.perform(post("/api/minimarket/sucursales/" + idSucursal + "/metodos-pago/activar").header("Authorization", empresa)
                .contentType(MediaType.APPLICATION_JSON).content("{\"idCatalogo\":" + idPlin + ",\"detalle\":\"999 111 222\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.idMetodoPago").exists());
        mockMvc.perform(post("/api/minimarket/sucursales/" + idSucursal + "/metodos-pago/activar").header("Authorization", empresa)
                .contentType(MediaType.APPLICATION_JSON).content("{\"idCatalogo\":" + idPlin + ",\"detalle\":\"999 111 222\"}"))
                .andExpect(status().isConflict());

        // el texto libre ya no crea metodos: tiene que existir en el catalogo
        mockMvc.perform(post("/api/minimarket/sucursales/" + idSucursal + "/metodos-pago").header("Authorization", empresa)
                .contentType(MediaType.APPLICATION_JSON).content("{\"nombre\":\"Trueque\"}"))
                .andExpect(status().isBadRequest());

        // la empresa edita solo su dato (el nombre viene del catalogo)
        String metodos = mockMvc.perform(get("/api/minimarket/sucursales/" + idSucursal + "/metodos-pago").header("Authorization", empresa))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.nombre=='Plin')].detalle").value("999 111 222"))
                .andReturn().getResponse().getContentAsString();
        String idMetodo = metodos.replaceAll("(?s).*\"idMetodoPago\":(\\d+),\"nombre\":\"Plin\".*", "$1");
        mockMvc.perform(put("/api/minimarket/metodos-pago").header("Authorization", empresa)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"idMetodoPago\":" + idMetodo + ",\"nombre\":\"Hackeado\",\"detalle\":\"900 000 000\"}"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/minimarket/sucursales/" + idSucursal + "/metodos-pago").header("Authorization", empresa))
                .andExpect(jsonPath("$[?(@.nombre=='Plin')].detalle").value("900 000 000"))
                .andExpect(jsonPath("$[?(@.nombre=='Hackeado')]").isEmpty());
    }
}
