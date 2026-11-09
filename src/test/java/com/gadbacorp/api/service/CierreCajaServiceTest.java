package com.gadbacorp.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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
import org.springframework.test.util.ReflectionTestUtils;

import com.gadbacorp.api.entity.caja.AperturaCaja;
import com.gadbacorp.api.entity.caja.ArqueoCaja;
import com.gadbacorp.api.entity.caja.ArqueoCajaDTO;
import com.gadbacorp.api.entity.caja.Caja;
import com.gadbacorp.api.repository.caja.AperturaCajaRepository;
import com.gadbacorp.api.repository.caja.ArqueoCajaRepositoy;
import com.gadbacorp.api.repository.caja.CajaRepository;
import com.gadbacorp.api.service.jpa.cajas.CierreCajaService;

@ExtendWith(MockitoExtension.class)
class CierreCajaServiceTest {

    @Mock AperturaCajaRepository aperturaCajaRepository;
    @Mock ArqueoCajaRepositoy arqueoCajaRepository;
    @Mock CajaRepository cajaRepository;

    @InjectMocks CierreCajaService service;

    private AperturaCaja apertura;
    private Caja caja;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(service, "exigirArqueo", true);
        caja = new Caja();
        caja.setEstadoCaja("OCUPADA");
        caja.setSaldoActual(1000.0);

        apertura = new AperturaCaja();
        apertura.setIdAperturaCaja(7);
        apertura.setEstadoCaja("ABIERTA");
        apertura.setSaldoInicial(100.0);
        apertura.setSaldoFinal(350.0);
        apertura.setSaldoEfectivo(250.0);
        apertura.setCaja(caja);
        org.mockito.Mockito.lenient().when(aperturaCajaRepository.buscarBloqueando(7)).thenReturn(Optional.of(apertura));
    }

    private ArqueoCajaDTO dto(Double contado) {
        ArqueoCajaDTO dto = new ArqueoCajaDTO();
        dto.setId_apertura_caja(7);
        dto.setMontoContado(contado);
        return dto;
    }

    @Test
    void arqueoConMontoQueCoincideSeGuardaConElSaldoDelServidor() {
        when(arqueoCajaRepository.save(any(ArqueoCaja.class))).thenAnswer(i -> i.getArgument(0));

        ArqueoCaja arqueo = service.registrarArqueo(dto(250.0));

        assertEquals(250.0, arqueo.getSaldoSistema());
        assertEquals(0.0, arqueo.getDiferencia());
        assertNotNull(arqueo.getFechaArqueo());
    }

    @Test
    void arqueoConMontoDistintoSeRechazaIndicandoLaDiferencia() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.registrarArqueo(dto(240.0)));
        assertEquals(true, ex.getMessage().contains("Diferencia: -10.00"));
        verify(arqueoCajaRepository, never()).save(any());
    }

    @Test
    void noSePuedeCerrarSinArqueo() {
        when(arqueoCajaRepository.existsByAperturaCaja_IdAperturaCaja(7)).thenReturn(false);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.cerrar(7));

        assertEquals("Debe registrar el arqueo de caja antes de cerrarla.", ex.getMessage());
        assertEquals("ABIERTA", apertura.getEstadoCaja());
    }

    @Test
    void cierreConArqueoLiberaLaCajaYSumaElSaldo() {
        when(arqueoCajaRepository.existsByAperturaCaja_IdAperturaCaja(7)).thenReturn(true);

        service.cerrar(7);

        assertEquals("CERRADO", apertura.getEstadoCaja());
        assertNotNull(apertura.getFechaCierre());
        assertEquals("LIBRE", caja.getEstadoCaja());
        assertEquals(1350.0, caja.getSaldoActual());
        verify(aperturaCajaRepository).save(apertura);
        verify(cajaRepository).save(caja);
    }

    @Test
    void sinExigirArqueoPermiteCerrar() {
        ReflectionTestUtils.setField(service, "exigirArqueo", false);
        service.cerrar(7);
        assertEquals("CERRADO", apertura.getEstadoCaja());
    }

    @Test
    void cerrarDosVecesFalla() {
        ReflectionTestUtils.setField(service, "exigirArqueo", false);
        service.cerrar(7);
        assertThrows(IllegalArgumentException.class, () -> service.cerrar(7));
    }

    @Test
    void cierreConSaldoFinalNuloUsaElSaldoInicialEnVezDeFallar() {
        ReflectionTestUtils.setField(service, "exigirArqueo", false);
        apertura.setSaldoFinal(null);
        service.cerrar(7);
        assertEquals(1100.0, caja.getSaldoActual());
    }
}
