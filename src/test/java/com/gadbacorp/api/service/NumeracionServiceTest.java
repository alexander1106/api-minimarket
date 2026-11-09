package com.gadbacorp.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.transaction.PlatformTransactionManager;

import com.gadbacorp.api.entity.ventas.Secuencia;
import com.gadbacorp.api.repository.ventas.CotizacionesRepository;
import com.gadbacorp.api.repository.ventas.SecuenciaRepository;
import com.gadbacorp.api.repository.ventas.VentasRepository;
import com.gadbacorp.api.service.jpa.ventas.NumeracionService;

class NumeracionServiceTest {

    private SecuenciaRepository secuencias;
    private VentasRepository ventas;
    private CotizacionesRepository cotizaciones;
    private NumeracionService service;

    @BeforeEach
    void setUp() {
        secuencias = mock(SecuenciaRepository.class);
        ventas = mock(VentasRepository.class);
        cotizaciones = mock(CotizacionesRepository.class);
        service = new NumeracionService(secuencias, ventas, cotizaciones, mock(PlatformTransactionManager.class));
    }

    @Test
    void incrementaLaSecuenciaExistente() {
        Secuencia fila = new Secuencia("BOLETA", 41L);
        when(secuencias.existsById("BOLETA")).thenReturn(true);
        when(secuencias.bloquear("BOLETA")).thenReturn(Optional.of(fila));

        assertEquals("B-000042", service.siguienteComprobante("boleta"));
        assertEquals(42L, fila.getUltimo());
        verify(secuencias).save(fila);
    }

    @Test
    void facturaUsaSuPropioContadorConPrefijoF() {
        when(secuencias.existsById("FACTURA")).thenReturn(true);
        when(secuencias.bloquear("FACTURA")).thenReturn(Optional.of(new Secuencia("FACTURA", 0L)));
        assertEquals("F-000001", service.siguienteComprobante("FACTURA"));
    }

    @Test
    void cualquierOtroTipoNumeraComoBoleta() {
        when(secuencias.existsById("BOLETA")).thenReturn(true);
        when(secuencias.bloquear("BOLETA")).thenReturn(Optional.of(new Secuencia("BOLETA", 9L)));
        assertEquals("B-000010", service.siguienteComprobante("NOTA DE VENTA"));
        assertEquals("B-000011", service.siguienteComprobante(null));
    }

    @Test
    void siNoExisteLaFilaArrancaDesdeElMayorNumeroYaEmitido() {
        Secuencia creada = new Secuencia("BOLETA", 120L);
        when(secuencias.bloquear("BOLETA")).thenReturn(Optional.of(creada));
        when(ventas.listarNumerosPorPrefijo("B-%")).thenReturn(List.of("B-000007", "B-000120", "B-000099"));

        assertEquals("B-000121", service.siguienteComprobante("BOLETA"));
        verify(secuencias).saveAndFlush(any(Secuencia.class));
    }

    @Test
    void cotizacionesTienenSuPropioFormato() {
        when(secuencias.existsById("COTIZACION")).thenReturn(true);
        when(secuencias.bloquear("COTIZACION")).thenReturn(Optional.of(new Secuencia("COTIZACION", 5L)));
        assertEquals("COT-000006", service.siguienteCotizacion());
    }

    @Test
    void alInicializarCuentaNumerosAntiguosPeroIgnoraLosInvalidos() {
        List<String> numeros = Arrays.asList("COT-482913", "COT-abc", null, "sin-guion-extra-1", "COT-000010");
        when(secuencias.bloquear("COTIZACION")).thenReturn(Optional.of(new Secuencia("COTIZACION", 482913L)));
        when(cotizaciones.listarNumerosPorPrefijo("COT-%")).thenReturn(numeros);

        assertEquals("COT-482914", service.siguienteCotizacion());

        ArgumentCaptor<Secuencia> inicial = ArgumentCaptor.forClass(Secuencia.class);
        verify(secuencias).saveAndFlush(inicial.capture());
        assertEquals(482913L, inicial.getValue().getUltimo());
    }
}
