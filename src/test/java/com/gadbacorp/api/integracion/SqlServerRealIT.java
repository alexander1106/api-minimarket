package com.gadbacorp.api.integracion;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MSSQLServerContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Levanta la aplicacion con el perfil "sqlserver" contra un SQL Server real: Hibernate crea el esquema,
 * el seed carga roles/usuarios y el login funciona.
 */
@Tag("integracion")
@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("sqlserver")
class SqlServerRealIT {

    @Container
    static final MSSQLServerContainer<?> SQLSERVER =
            new MSSQLServerContainer<>("mcr.microsoft.com/mssql/server:2022-latest").acceptLicense();

    @DynamicPropertySource
    static void propiedades(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", SQLSERVER::getJdbcUrl);
        registry.add("spring.datasource.username", SQLSERVER::getUsername);
        registry.add("spring.datasource.password", SQLSERVER::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        registry.add("spring.jpa.database-platform", () -> "org.hibernate.dialect.SQLServerDialect");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired
    MockMvc mockMvc;

    @Test
    void laAplicacionArrancaConSqlServerYElLoginFunciona() throws Exception {
        String respuesta = mockMvc.perform(post("/api/minimarket/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"alez\",\"password\":\"1234\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertTrue(respuesta.contains("\"rol\":\"SUPERADMIN\""), respuesta);
    }
}
