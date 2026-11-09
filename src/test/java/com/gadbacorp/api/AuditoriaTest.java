package com.gadbacorp.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.gadbacorp.api.service.jpa.auditoria.AuditoriaService;

@SpringBootTest(properties = {
        "app.auditoria.acciones=CREAR,EDITAR,ELIMINAR,INICIO_SESION,INICIO_SESION_FALLIDO,OTRO",
        "spring.datasource.url=jdbc:h2:mem:auditoria;MODE=MySQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1"})
@AutoConfigureMockMvc
class AuditoriaTest {

    @Autowired MockMvc mockMvc;

    private String token(String usuario, String clave) throws Exception {
        String r = mockMvc.perform(post("/api/minimarket/login").contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"" + usuario + "\",\"password\":\"" + clave + "\"}"))
                .andReturn().getResponse().getContentAsString();
        return "Bearer " + r.replaceAll("(?s).*\"token\":\"([^\"]+)\".*", "$1");
    }

    @Test
    void sanitizaContrasenasTokensYTextosLargos() {
        String limpio = AuditoriaService.sanitizar(
                "{\"username\":\"ana\",\"password\":\"Secreta123\",\"datos\":{\"token\":\"abc\",\"imagen\":\"" + "x".repeat(500) + "\"}}");
        assertFalse(limpio.contains("Secreta123"));
        assertFalse(limpio.contains("abc"));
        assertTrue(limpio.contains("\"password\":\"***\""));
        assertTrue(limpio.contains("[500 caracteres]"));
        assertTrue(limpio.length() < 600);
        assertNull(AuditoriaService.sanitizar(""));
        assertEquals("[cuerpo no JSON]", AuditoriaService.sanitizar("no es json"));
    }

    @Test
    void registraQuienHaceQueYSoloElSuperadminLoConsulta() throws Exception {
        // inicio de sesion fallido y correcto
        mockMvc.perform(post("/api/minimarket/login").contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"empresa\",\"password\":\"incorrecta\"}"));
        String empresa = token("empresa", "1234");
        String superadmin = token("alez", "1234");

        // alez crea y elimina una categoria; tambien intenta crear un usuario con contrasena
        String creada = mockMvc.perform(post("/api/minimarket/categorias").header("Authorization", superadmin)
                .contentType(MediaType.APPLICATION_JSON).content("{\"nombre\":\"Auditada\"}"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        String id = creada.replaceAll("(?s).*\"idcategoria\":(\\d+).*", "$1");
        mockMvc.perform(delete("/api/minimarket/categorias/" + id).header("Authorization", superadmin))
                .andExpect(status().isOk());
        mockMvc.perform(post("/api/minimarket/usuarios").header("Authorization", superadmin)
                .contentType(MediaType.APPLICATION_JSON).content("{\"username\":\"zz\",\"password\":\"Secreta123\",\"rolId\":99999}"))
                .andExpect(status().isBadRequest());

        // las empresas no ven la auditoria
        mockMvc.perform(get("/api/minimarket/auditoria").header("Authorization", empresa)).andExpect(status().isForbidden());
        mockMvc.perform(get("/api/minimarket/auditoria/resumen").header("Authorization", empresa)).andExpect(status().isForbidden());
        mockMvc.perform(get("/api/minimarket/auditoria")).andExpect(status().isUnauthorized());

        // lo hecho por alez, con su frase, modulo y el id del registro
        mockMvc.perform(get("/api/minimarket/auditoria?username=alez&accion=CREAR&modulo=Productos").header("Authorization", superadmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[?(@.descripcion == 'alez creó un registro en categorias (#" + id + ")')]").isNotEmpty())
                .andExpect(jsonPath("$.items[0].rol").value("SUPERADMIN"))
                .andExpect(jsonPath("$.total").isNumber());
        String eliminadas = mockMvc.perform(get("/api/minimarket/auditoria?accion=ELIMINAR&q=categorias").header("Authorization", superadmin))
                .andExpect(jsonPath("$.items[0].idRegistro").value(id))
                .andExpect(jsonPath("$.items[0].descripcion").value("alez eliminó categorias (#" + id + ")"))
                .andReturn().getResponse().getContentAsString();
        assertTrue(eliminadas.contains("\"exito\":true"));

        // inicios de sesion
        mockMvc.perform(get("/api/minimarket/auditoria?accion=INICIO_SESION_FALLIDO").header("Authorization", superadmin))
                .andExpect(jsonPath("$.items[?(@.username == 'empresa')]").isNotEmpty())
                .andExpect(jsonPath("$.items[0].exito").value(false));
        mockMvc.perform(get("/api/minimarket/auditoria?accion=INICIO_SESION&username=empresa").header("Authorization", superadmin))
                .andExpect(jsonPath("$.items[0].exito").value(true));

        // la contrasena nunca queda guardada; el detalle muestra lo demas
        String fallido = mockMvc.perform(get("/api/minimarket/auditoria?modulo=Seguridad&exito=false&accion=CREAR").header("Authorization", superadmin))
                .andExpect(jsonPath("$.items[0].username").value("alez"))
                .andReturn().getResponse().getContentAsString();
        String idRegistro = fallido.replaceAll("(?s).*\"items\":\\[\\{\"id\":(\\d+).*", "$1");
        String detalle = mockMvc.perform(get("/api/minimarket/auditoria/" + idRegistro).header("Authorization", superadmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.metodo").value("POST"))
                .andExpect(jsonPath("$.estadoHttp").value(400))
                .andExpect(jsonPath("$.ip").exists())
                .andReturn().getResponse().getContentAsString();
        assertFalse(detalle.contains("Secreta123"));
        assertTrue(detalle.contains("\\\"password\\\":\\\"***\\\""));
        assertTrue(detalle.contains("zz"));

        // la ruta de otra empresa o un id inexistente
        mockMvc.perform(get("/api/minimarket/auditoria/99999999").header("Authorization", superadmin)).andExpect(status().isNotFound());

        // resumen y filtros
        mockMvc.perform(get("/api/minimarket/auditoria/resumen").header("Authorization", superadmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").isNumber())
                .andExpect(jsonPath("$.fallidos").isNumber())
                .andExpect(jsonPath("$.porAccion.CREAR").isNumber())
                .andExpect(jsonPath("$.topUsuarios.alez").isNumber());
        mockMvc.perform(get("/api/minimarket/auditoria/filtros").header("Authorization", superadmin))
                .andExpect(jsonPath("$.acciones", Matchers.hasItem("ELIMINAR")))
                .andExpect(jsonPath("$.modulos", Matchers.hasItem("Productos")))
                .andExpect(jsonPath("$.usuarios", Matchers.hasItem("alez")));

        // las consultas y los avisos de rutina no se auditan
        mockMvc.perform(get("/api/minimarket/auditoria?q=notificaciones").header("Authorization", superadmin))
                .andExpect(jsonPath("$.items.length()").value(0));
    }
}
