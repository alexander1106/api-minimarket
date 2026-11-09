package com.gadbacorp.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.gadbacorp.api.repository.inventario.ProductosRepository;

/** Con el perfil prod y una configuracion propia la aplicacion arranca, responde el health y no carga datos de demostracion. */
@SpringBootTest(properties = {
        "jwt.secret=un-secreto-propio-de-mas-de-treinta-y-dos-caracteres",
        "app.crypto.key=q83QuPdsb0uEH9qGUm2SXZLsT7yTcfhqb9bmuSjZ9Wo=",
        "app.cors.allowed-origins=https://minimarket.netlify.app",
        "app.seed.admin-password=Claveadmin#2026",
        "app.seed.empresa-password=Claveempresa#2026",
        "spring.datasource.url=jdbc:h2:mem:perfilprod;MODE=MySQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1"})
@ActiveProfiles("prod")
@AutoConfigureMockMvc
class PerfilProduccionTest {

    @Autowired MockMvc mockMvc;
    @Autowired ProductosRepository productos;

    @Test
    void arrancaConLaConfiguracionDeProduccion() throws Exception {
        mockMvc.perform(get("/actuator/health")).andExpect(status().isOk()).andExpect(jsonPath("$.status").value("UP"));
        assertEquals(0, productos.count());
    }
}
