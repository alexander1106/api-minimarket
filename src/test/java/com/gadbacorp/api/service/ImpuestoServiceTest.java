package com.gadbacorp.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.gadbacorp.api.service.jpa.ventas.ImpuestoService;

class ImpuestoServiceTest {

    private final ImpuestoService service = new ImpuestoService(new BigDecimal("0.18"));

    @Test
    void separaLaBaseYElIgvDeUnPrecioQueYaLoIncluye() {
        ImpuestoService.Desglose d = service.nuevo().agregar(118.00, true);
        assertEquals(new BigDecimal("100.00"), d.opGravada());
        assertEquals(new BigDecimal("18.00"), d.igv());
        assertEquals(new BigDecimal("0.00"), d.opInafecta());
    }

    @Test
    void lasLineasInafectasNoPaganIgv() {
        ImpuestoService.Desglose d = service.nuevo().agregar(59.00, true).agregar(20.00, false);
        assertEquals(new BigDecimal("50.00"), d.opGravada());
        assertEquals(new BigDecimal("9.00"), d.igv());
        assertEquals(new BigDecimal("20.00"), d.opInafecta());
    }

    @Test
    void baseMasIgvSiempreSumaLoCobradoAunConRedondeo() {
        ImpuestoService.Desglose d = service.nuevo().agregar(10.01, true).agregar(3.33, true);
        assertEquals(new BigDecimal("13.34"), d.opGravada().add(d.igv()));
    }

    @Test
    void sinLineasTodoEsCero() {
        ImpuestoService.Desglose d = service.nuevo();
        assertEquals(0, d.igv().signum());
        assertEquals(0, d.opGravada().signum());
    }
}
