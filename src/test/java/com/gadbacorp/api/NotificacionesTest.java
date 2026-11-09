package com.gadbacorp.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.gadbacorp.api.repository.empleados.UsuarioRepository;
import com.gadbacorp.api.repository.inventario.ProductosRepository;
import com.gadbacorp.api.service.jpa.inventario.StockService;

/** Notificaciones: las genera el sistema y cada usuario las ve y marca solo para el. */
@SpringBootTest(properties = {
        "app.seed.demo=true",
        "spring.datasource.url=jdbc:h2:mem:notificaciones;MODE=MySQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1"})
@AutoConfigureMockMvc
class NotificacionesTest {

    @Autowired MockMvc mockMvc;
    @Autowired UsuarioRepository usuarios;
    @Autowired ProductosRepository productos;
    @Autowired StockService stockService;

    private String token(String usuario) throws Exception {
        String r = mockMvc.perform(post("/api/minimarket/login").contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"" + usuario + "\",\"password\":\"1234\"}"))
                .andReturn().getResponse().getContentAsString();
        return "Bearer " + r.replaceAll("(?s).*\"token\":\"([^\"]+)\".*", "$1");
    }

    private long noLeidas(String token) throws Exception {
        String r = mockMvc.perform(get("/api/minimarket/notificaciones/contador").header("Authorization", token))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        return Long.parseLong(r.replaceAll("(?s).*\"noLeidas\":(\\d+).*", "$1"));
    }

    @Test
    void elSistemaAvisaYCadaUsuarioVeYMarcaSoloLasSuyas() throws Exception {
        String empresa = token("empresa");
        String superadmin = token("alez");

        // al arrancar se revisaron los lotes: el pan vence en 2 dias
        assertTrue(noLeidas(empresa) >= 1);
        String lista = mockMvc.perform(get("/api/minimarket/notificaciones?soloNoLeidas=true&tipo=POR_VENCER").header("Authorization", empresa))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].titulo").value("Producto por vencer"))
                .andExpect(jsonPath("$.items[?(@.severidad=='CRITICA' && @.mensaje =~ /.*Pan franc.s.*/)]").isNotEmpty())
                .andExpect(jsonPath("$.items[0].ruta").value("/admin/lotes"))
                .andExpect(jsonPath("$.items[0].leida").value(false))
                .andReturn().getResponse().getContentAsString();
        String id = lista.replaceAll("(?s).*\"items\":\\[\\{\"idNotificacion\":(\\d+).*", "$1");

        // el superadmin no ve los avisos de la empresa ni puede tocarlos
        mockMvc.perform(get("/api/minimarket/notificaciones?tipo=POR_VENCER").header("Authorization", superadmin))
                .andExpect(jsonPath("$.items.length()").value(0));
        mockMvc.perform(put("/api/minimarket/notificaciones/" + id + "/leida").header("Authorization", superadmin))
                .andExpect(status().isNotFound());
        mockMvc.perform(delete("/api/minimarket/notificaciones/" + id).header("Authorization", superadmin))
                .andExpect(status().isNotFound());

        // marcar leida y volver a no leida
        long antes = noLeidas(empresa);
        mockMvc.perform(put("/api/minimarket/notificaciones/" + id + "/leida").header("Authorization", empresa))
                .andExpect(status().isOk()).andExpect(jsonPath("$.leida").value(true))
                .andExpect(jsonPath("$.fechaLectura").exists());
        assertEquals(antes - 1, noLeidas(empresa));
        mockMvc.perform(put("/api/minimarket/notificaciones/" + id + "/no-leida").header("Authorization", empresa))
                .andExpect(jsonPath("$.leida").value(false));
        assertEquals(antes, noLeidas(empresa));

        // una salida de stock que deja el producto bajo su minimo genera aviso, y no se repite mientras no se lea
        Integer idEmpresa = usuarios.findByUsername("empresa").getSucursal().getEmpresa().getIdempresa();
        Integer idSucursal = usuarios.findByUsername("empresa").getSucursal().getIdSucursal();
        Integer idDetergente = productos.findByCodigoIgnoreCaseAndEmpresa_Idempresa("LIM-001", idEmpresa).orElseThrow().getIdproducto();
        stockService.descontar(idSucursal, idDetergente, BigDecimal.ONE, "VENTA", "prueba");
        stockService.descontar(idSucursal, idDetergente, BigDecimal.ONE, "VENTA", "prueba");
        mockMvc.perform(get("/api/minimarket/notificaciones?tipo=STOCK_BAJO&soloNoLeidas=true").header("Authorization", empresa))
                .andExpect(jsonPath("$.items[?(@.mensaje =~ /Detergente.*/)]", org.hamcrest.Matchers.hasSize(1)))
                .andExpect(jsonPath("$.items[?(@.mensaje =~ /Detergente.*/)].titulo").value("Stock bajo"))
                .andExpect(jsonPath("$.items[?(@.mensaje =~ /Detergente.*/)].ruta").value("/admin/productos"));

        // marcar todas, filtrar y limpiar
        mockMvc.perform(put("/api/minimarket/notificaciones/marcar-todas").header("Authorization", empresa))
                .andExpect(status().isOk()).andExpect(jsonPath("$.marcadas").isNumber());
        assertEquals(0, noLeidas(empresa));
        mockMvc.perform(get("/api/minimarket/notificaciones?soloNoLeidas=true").header("Authorization", empresa))
                .andExpect(jsonPath("$.items.length()").value(0)).andExpect(jsonPath("$.noLeidas").value(0));
        mockMvc.perform(get("/api/minimarket/notificaciones?tamano=1&pagina=0").header("Authorization", empresa))
                .andExpect(jsonPath("$.items.length()").value(1)).andExpect(jsonPath("$.totalPaginas").isNumber());
        mockMvc.perform(delete("/api/minimarket/notificaciones/" + id).header("Authorization", empresa))
                .andExpect(status().isOk());
        mockMvc.perform(delete("/api/minimarket/notificaciones/leidas").header("Authorization", empresa))
                .andExpect(status().isOk()).andExpect(jsonPath("$.eliminadas").isNumber());
        mockMvc.perform(get("/api/minimarket/notificaciones").header("Authorization", empresa))
                .andExpect(jsonPath("$.total").value(0));

        // sin sesion no hay notificaciones
        mockMvc.perform(get("/api/minimarket/notificaciones/contador")).andExpect(status().isUnauthorized());
    }
}
