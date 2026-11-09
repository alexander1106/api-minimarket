package com.gadbacorp.reportes;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

@QuarkusTest
class ReportesResourceTest {

    @Inject
    ResumenVentas resumen;

    @Test
    void resumenAcumulaVentas() {
        resumen.registrar(new VentaEvent(1, "B-000001", "boleta", 10.0, 1, 1, "2026-01-01"));
        resumen.registrar(new VentaEvent(2, "F-000001", "FACTURA", 90.0, 1, 1, "2026-01-01"));

        given().when().get("/reportes/ventas/resumen")
                .then().statusCode(200)
                .body("cantidadVentas", equalTo(2))
                .body("montoTotal", equalTo(100.0f))
                .body("montoPorComprobante.BOLETA", equalTo(10.0f))
                .body("montoPorComprobante.FACTURA", equalTo(90.0f));
    }
}
