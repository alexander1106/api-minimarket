package com.gadbacorp.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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

@SpringBootTest
@AutoConfigureMockMvc
class PlanesCrudTest {

    @Autowired MockMvc mockMvc;

    private String token(String usuario) throws Exception {
        String r = mockMvc.perform(post("/api/minimarket/login").contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"" + usuario + "\",\"password\":\"1234\"}"))
                .andReturn().getResponse().getContentAsString();
        return "Bearer " + r.replaceAll("(?s).*\"token\":\"([^\"]+)\".*", "$1");
    }

    private String crear(String superadmin, String cuerpo, int estado) throws Exception {
        return mockMvc.perform(post("/api/minimarket/planes").header("Authorization", superadmin)
                .contentType(MediaType.APPLICATION_JSON).content(cuerpo))
                .andExpect(status().is(estado)).andReturn().getResponse().getContentAsString();
    }

    @Test
    void elSuperadminAdministraLosPlanesYLasEmpresasSoloLosConsultan() throws Exception {
        String superadmin = token("alez");
        String empresa = token("empresa");

        mockMvc.perform(get("/api/minimarket/planes").header("Authorization", empresa))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.nombre=='Gratis')].totalEmpresas").isNotEmpty());
        mockMvc.perform(post("/api/minimarket/planes").header("Authorization", empresa)
                .contentType(MediaType.APPLICATION_JSON).content("{\"nombre\":\"Hack\",\"precioMensual\":1}"))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/api/minimarket/planes").header("Authorization", superadmin)
                .contentType(MediaType.APPLICATION_JSON).content("{\"nombre\":\" \",\"precioMensual\":-5,\"maxUsuarios\":0}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.nombre").exists())
                .andExpect(jsonPath("$.errors.precioMensual").exists())
                .andExpect(jsonPath("$.errors.maxUsuarios").exists());
        String creado = crear(superadmin, "{\"nombre\":\"Premium\",\"precioMensual\":499.90,\"maxUsuarios\":50,\"maxSucursales\":20}", 201);
        String id = creado.replaceAll("(?s).*\"idPlan\":(\\d+).*", "$1");
        crear(superadmin, "{\"nombre\":\"premium\",\"precioMensual\":1}", 409);

        // editar: precio y limites; el nombre no puede chocar con otro plan
        mockMvc.perform(put("/api/minimarket/planes").header("Authorization", superadmin)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"idPlan\":" + id + ",\"nombre\":\"Premium\",\"precioMensual\":449.90,\"maxUsuarios\":60,\"maxSucursales\":null}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.precioMensual").value(449.90))
                .andExpect(jsonPath("$.maxUsuarios").value(60))
                .andExpect(jsonPath("$.maxSucursales").doesNotExist());
        mockMvc.perform(put("/api/minimarket/planes").header("Authorization", superadmin)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"idPlan\":" + id + ",\"nombre\":\"Gratis\",\"precioMensual\":1}"))
                .andExpect(status().isConflict());
        mockMvc.perform(put("/api/minimarket/planes").header("Authorization", superadmin)
                .contentType(MediaType.APPLICATION_JSON).content("{\"idPlan\":999999,\"nombre\":\"X\",\"precioMensual\":1}"))
                .andExpect(status().isNotFound());

        // desactivar (no se borra) y filtrar solo activos
        mockMvc.perform(delete("/api/minimarket/planes/" + id).header("Authorization", superadmin)).andExpect(status().isOk());
        mockMvc.perform(get("/api/minimarket/planes/" + id).header("Authorization", superadmin))
                .andExpect(jsonPath("$.estado").value(0));
        mockMvc.perform(get("/api/minimarket/planes?soloActivos=true").header("Authorization", superadmin))
                .andExpect(jsonPath("$[?(@.nombre=='Premium')]").isEmpty());
        mockMvc.perform(put("/api/minimarket/planes").header("Authorization", superadmin)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"idPlan\":" + id + ",\"nombre\":\"Premium\",\"precioMensual\":449.90,\"estado\":1}"))
                .andExpect(jsonPath("$.estado").value(1));

        // un plan en uso no se desactiva
        String planes = mockMvc.perform(get("/api/minimarket/planes").header("Authorization", superadmin))
                .andReturn().getResponse().getContentAsString();
        String enUso = planes.replaceAll("(?s).*\"idPlan\":(\\d+),\"nombre\":\"Empresarial\".*", "$1");
        mockMvc.perform(delete("/api/minimarket/planes/" + enUso).header("Authorization", superadmin))
                .andExpect(status().isConflict());
        mockMvc.perform(delete("/api/minimarket/planes/999999").header("Authorization", superadmin))
                .andExpect(status().isNotFound());
    }
}
