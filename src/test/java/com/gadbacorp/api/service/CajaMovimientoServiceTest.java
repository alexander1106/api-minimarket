package com.gadbacorp.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gadbacorp.api.entity.caja.AperturaCaja;
import com.gadbacorp.api.entity.caja.SaldoMetodoPago;
import com.gadbacorp.api.entity.caja.TransaccionesCaja;
import com.gadbacorp.api.entity.ventas.MetodosPago;
import com.gadbacorp.api.repository.caja.AperturaCajaRepository;
import com.gadbacorp.api.repository.caja.SaldoMetodoPagoRepository;
import com.gadbacorp.api.repository.caja.TransaccionesCajaRepository;
import com.gadbacorp.api.service.jpa.cajas.CajaMovimientoService;

@ExtendWith(MockitoExtension.class)
class CajaMovimientoServiceTest {

    @Mock AperturaCajaRepository aperturaCajaRepository;
    @Mock SaldoMetodoPagoRepository saldoMetodoPagoRepository;
    @Mock TransaccionesCajaRepository transaccionesCajaRepository;

    @InjectMocks CajaMovimientoService service;

    private AperturaCaja apertura;
    private MetodosPago efectivo;
    private MetodosPago tarjeta;

    @BeforeEach
    void setUp() {
        apertura = new AperturaCaja();
        apertura.setIdAperturaCaja(1);
        apertura.setEstadoCaja("ABIERTA");
        apertura.setSaldoInicial(100.0);
        apertura.setSaldoFinal(100.0);
        apertura.setSaldoEfectivo(100.0);

        efectivo = new MetodosPago();
        efectivo.setNombre("EFECTIVO");
        tarjeta = new MetodosPago();
        tarjeta.setNombre("Tarjeta");
    }

    private SaldoMetodoPago saldoDe(MetodosPago metodo, double saldo) {
        SaldoMetodoPago s = new SaldoMetodoPago();
        s.setMetodoPago(metodo);
        s.setAperturaCaja(apertura);
        s.setSaldo(saldo);
        when(saldoMetodoPagoRepository.findByAperturaCajaAndMetodoPago(apertura, metodo)).thenReturn(Optional.of(s));
        return s;
    }

    private void aperturaEncontrada() {
        lenient().when(aperturaCajaRepository.buscarBloqueando(1)).thenReturn(Optional.of(apertura));
        lenient().when(transaccionesCajaRepository.save(any(TransaccionesCaja.class))).thenAnswer(i -> i.getArgument(0));
    }

    @Test
    void ingresoEnEfectivoSumaALosTresSaldos() {
        aperturaEncontrada();
        SaldoMetodoPago saldo = saldoDe(efectivo, 100.0);

        TransaccionesCaja t = service.registrar(1, efectivo, "ingreso", 50.0, "venta");

        assertEquals(150.0, apertura.getSaldoFinal());
        assertEquals(150.0, apertura.getSaldoEfectivo());
        assertEquals(150.0, saldo.getSaldo());
        assertEquals("INGRESO", t.getTipoMovimiento());
    }

    @Test
    void ingresoConTarjetaNoTocaElEfectivo() {
        aperturaEncontrada();
        SaldoMetodoPago saldo = saldoDe(tarjeta, 0.0);

        service.registrar(1, tarjeta, "INGRESO", 30.0, "venta");

        assertEquals(130.0, apertura.getSaldoFinal());
        assertEquals(100.0, apertura.getSaldoEfectivo());
        assertEquals(30.0, saldo.getSaldo());
    }

    @Test
    void egresoMayorAlSaldoSeRechazaYNoCambiaNada() {
        aperturaEncontrada();
        saldoDe(efectivo, 100.0);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.registrar(1, efectivo, "EGRESO", 500.0, "gasto"));

        assertEquals("Saldo insuficiente. No se puede egresar más de lo disponible.", ex.getMessage());
        assertEquals(100.0, apertura.getSaldoFinal());
        verify(transaccionesCajaRepository, never()).save(any());
    }

    @Test
    void egresoMayorAlSaldoDelMetodoSeRechaza() {
        aperturaEncontrada();
        saldoDe(tarjeta, 10.0); // hay 100 en caja pero solo 10 en tarjeta

        assertThrows(IllegalArgumentException.class,
                () -> service.registrar(1, tarjeta, "EGRESO", 50.0, "gasto"));
        assertEquals(100.0, apertura.getSaldoFinal());
    }

    @Test
    void egresoValidoRestaDeLosSaldos() {
        aperturaEncontrada();
        SaldoMetodoPago saldo = saldoDe(efectivo, 100.0);

        service.registrar(1, efectivo, "EGRESO", 40.0, "gasto");

        assertEquals(60.0, apertura.getSaldoFinal());
        assertEquals(60.0, apertura.getSaldoEfectivo());
        assertEquals(60.0, saldo.getSaldo());
    }

    @Test
    void noPermiteMovimientosEnUnaAperturaCerrada() {
        aperturaEncontrada();
        apertura.setEstadoCaja("CERRADO");

        assertThrows(IllegalArgumentException.class,
                () -> service.registrar(1, efectivo, "INGRESO", 10.0, "venta"));
    }

    @Test
    void validaTipoMontoYApertura() {
        aperturaEncontrada();
        assertThrows(IllegalArgumentException.class, () -> service.registrar(1, efectivo, "OTRO", 10.0, "x"));
        assertThrows(IllegalArgumentException.class, () -> service.registrar(1, efectivo, "INGRESO", 0.0, "x"));
        assertThrows(IllegalArgumentException.class, () -> service.registrar(1, efectivo, "INGRESO", null, "x"));
        assertThrows(IllegalArgumentException.class, () -> service.registrar(null, efectivo, "INGRESO", 5.0, "x"));
    }
}
