package com.gadbacorp.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/** Sin configuracion, la auditoria guarda solo lo que modifica o borra: ni creaciones ni inicios de sesion. */
@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:auditoriadefecto;MODE=MySQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1")
@AutoConfigureMockMvc
class AuditoriaPorDefectoTest {

    @Autowired MockMvc mockMvc;

    private String token(String usuario, String clave) throws Exception {
        String r = mockMvc.perform(post("/api/minimarket/login").contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"" + usuario + "\",\"password\":\"" + clave + "\"}"))
                .andReturn().getResponse().getContentAsString();
        return "Bearer " + r.replaceAll("(?s).*\"token\":\"([^\"]+)\".*", "$1");
    }

    @Test
    void soloSeGuardanLasEdicionesYLasEliminaciones() throws Exception {
        mockMvc.perform(post("/api/minimarket/login").contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"empresa\",\"password\":\"mala\"}"));
        String superadmin = token("alez", "1234");

        String creada = mockMvc.perform(post("/api/minimarket/categorias").header("Authorization", superadmin)
                .contentType(MediaType.APPLICATION_JSON).content("{\"nombre\":\"Solo borrar\"}"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        String id = creada.replaceAll("(?s).*\"idcategoria\":(\\d+).*", "$1");
        mockMvc.perform(delete("/api/minimarket/categorias/" + id).header("Authorization", superadmin)).andExpect(status().isOk());

        mockMvc.perform(get("/api/minimarket/auditoria").header("Authorization", superadmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.items[0].accion").value("ELIMINAR"))
                .andExpect(jsonPath("$.items[0].descripcion").value("alez eliminó categorias (#" + id + ")"));
        mockMvc.perform(get("/api/minimarket/auditoria/resumen").header("Authorization", superadmin))
                .andExpect(jsonPath("$.porAccion.ELIMINAR").value(1))
                .andExpect(jsonPath("$.porAccion.CREAR").doesNotExist())
                .andExpect(jsonPath("$.porAccion.INICIO_SESION").doesNotExist());
    }
}
