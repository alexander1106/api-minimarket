package com.gadbacorp.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.gadbacorp.api.entity.administrable.Empresas;
import com.gadbacorp.api.entity.administrable.Plan;
import com.gadbacorp.api.entity.administrable.RegistroEmpresaRequest;
import com.gadbacorp.api.entity.administrable.Sucursales;
import com.gadbacorp.api.excepciones.LimitePlanExcedidoException;
import com.gadbacorp.api.excepciones.RecursoDuplicadoException;
import com.gadbacorp.api.excepciones.RegistroInvalidoException;
import com.gadbacorp.api.repository.administrable.EmpresasRepository;
import com.gadbacorp.api.repository.administrable.PlanRepository;
import com.gadbacorp.api.repository.administrable.SucursalesRepository;
import com.gadbacorp.api.service.jpa.administrable.EmpresaRegistroService;

class EmpresaRegistroServiceTest {

    private static final LocalDate HOY = LocalDate.of(2026, 10, 10);

    private EmpresasRepository empresas;
    private SucursalesRepository sucursales;
    private PlanRepository planes;
    private EmpresaRegistroService service;

    private final Plan profesional = new Plan("Profesional", new BigDecimal("169.00"), 10, 3);

    @BeforeEach
    void setUp() {
        empresas = Mockito.mock(EmpresasRepository.class);
        sucursales = Mockito.mock(SucursalesRepository.class);
        planes = Mockito.mock(PlanRepository.class);
        Clock reloj = Clock.fixed(HOY.atStartOfDay().toInstant(ZoneOffset.UTC), ZoneOffset.UTC);
        service = new EmpresaRegistroService(empresas, sucursales, planes, reloj, 14);

        when(empresas.save(any(Empresas.class))).thenAnswer(inv -> {
            Empresas e = inv.getArgument(0);
            e.setIdempresa(77);
            return e;
        });
        profesional.setIdPlan(3);
        when(planes.findById(3)).thenReturn(Optional.of(profesional));
    }

    /** Los datos del formulario de la pantalla. */
    private RegistroEmpresaRequest formulario(String estado, Integer sucursalesPedidas, String ciclo) {
        return new RegistroEmpresaRequest("Minimarket San Borja Express", "20601928491", "",
                "Juan Carlos Pérez", "contacto@empresa.pe", "+51 987 654 321",
                "Av. Javier Prado Este 2400", "San Borja", "Lima", "Lima",
                sucursalesPedidas, 3, ciclo, estado, true, "Pago adelantado", null);
    }

    @Test
    void creaLaEmpresaConTodosLosCamposDelFormulario() {
        Integer id = service.registrar(formulario("ACTIVO", 1, "MENSUAL"));

        assertEquals(77, id);
        ArgumentCaptor<Empresas> captor = ArgumentCaptor.forClass(Empresas.class);
        verify(empresas).save(captor.capture());
        Empresas e = captor.getValue();
        assertEquals("Minimarket San Borja Express", e.getNombreComercial());
        assertEquals("20601928491", e.getRuc());
        assertEquals("Juan Carlos Pérez", e.getPropietario());
        assertEquals("contacto@empresa.pe", e.getCorreo());
        assertEquals("+51 987 654 321", e.getTelefono());
        assertEquals("Av. Javier Prado Este 2400", e.getDireccion());
        assertEquals("San Borja", e.getDistrito());
        assertEquals("Lima", e.getCiudad());
        assertEquals("Lima", e.getDepartamento());
        assertEquals(profesional, e.getPlan());
        assertEquals("MENSUAL", e.getCiclo());
        assertTrue(e.getFacturacionElectronica());
        assertEquals("Pago adelantado", e.getNotas());
        assertEquals(HOY, e.getFechaRegistro());
        assertEquals(1, e.getEstado());
    }

    @Test
    void sinRazonSocialSunatUsaElNombreComercial() {
        service.registrar(formulario("ACTIVO", 1, "MENSUAL"));
        ArgumentCaptor<Empresas> captor = ArgumentCaptor.forClass(Empresas.class);
        verify(empresas).save(captor.capture());
        assertEquals("Minimarket San Borja Express", captor.getValue().getRazonsocial());
    }

    @Test
    void conRazonSocialSunatLaRespeta() {
        RegistroEmpresaRequest r = new RegistroEmpresaRequest("San Borja Express", "20601928491",
                "COMERCIALIZADORA SAN BORJA EXPRESS S.A.C.", "Juan", "a@b.pe", "987654321", "Av 1",
                null, null, null, null, null, null, null, null, null, null);
        service.registrar(r);
        ArgumentCaptor<Empresas> captor = ArgumentCaptor.forClass(Empresas.class);
        verify(empresas).save(captor.capture());
        assertEquals("COMERCIALIZADORA SAN BORJA EXPRESS S.A.C.", captor.getValue().getRazonsocial());
        assertEquals("MENSUAL", captor.getValue().getCiclo());
    }

    private Empresas guardada(RegistroEmpresaRequest r) {
        service.registrar(r);
        ArgumentCaptor<Empresas> captor = ArgumentCaptor.forClass(Empresas.class);
        verify(empresas).save(captor.capture());
        return captor.getValue();
    }

    @Test
    void activoMensualVenceEnUnMes() {
        Empresas e = guardada(formulario("ACTIVO", 1, "MENSUAL"));
        assertFalse(e.getEnPrueba());
        assertFalse(e.getSuspendida());
        assertEquals(HOY.plusMonths(1), e.getFechaVencimiento());
    }

    @Test
    void activoAnualVenceEnUnAnio() {
        assertEquals(HOY.plusYears(1), guardada(formulario("ACTIVO", 1, "ANUAL")).getFechaVencimiento());
    }

    @Test
    void enPruebaDuraLosDiasConfigurados() {
        Empresas e = guardada(formulario("PRUEBA", 1, "MENSUAL"));
        assertTrue(e.getEnPrueba());
        assertEquals(HOY.plusDays(14), e.getFechaVencimiento());
    }

    @Test
    void suspendidoNaceSuspendida() {
        assertTrue(guardada(formulario("SUSPENDIDO", 1, "MENSUAL")).getSuspendida());
    }

    @Test
    void sinEstadoInicialEsActivo() {
        Empresas e = guardada(formulario(null, 1, null));
        assertFalse(e.getEnPrueba());
        assertFalse(e.getSuspendida());
        assertEquals("MENSUAL", e.getCiclo());
    }

    @Test
    void creaLaSedePrincipalConLaDireccionYElTelefonoDeLaEmpresa() {
        service.registrar(formulario("ACTIVO", 1, "MENSUAL"));
        ArgumentCaptor<Sucursales> captor = ArgumentCaptor.forClass(Sucursales.class);
        verify(sucursales).save(captor.capture());
        Sucursales s = captor.getValue();
        assertEquals("Sede Principal", s.getNombreSucursal());
        assertEquals("Av. Javier Prado Este 2400", s.getDireccion());
        assertEquals("+51 987 654 321", s.getContacto());
        assertEquals(1, s.getEstado());
    }

    @Test
    void creaTantasSucursalesComoSePidieron() {
        service.registrar(formulario("ACTIVO", 3, "MENSUAL"));
        ArgumentCaptor<Sucursales> captor = ArgumentCaptor.forClass(Sucursales.class);
        verify(sucursales, times(3)).save(captor.capture());
        assertEquals(List.of("Sede Principal", "Sucursal 2", "Sucursal 3"),
                captor.getAllValues().stream().map(Sucursales::getNombreSucursal).toList());
    }

    @Test
    void noCreaMasSucursalesQueLasDelPlan() {
        // el plan Profesional permite 3
        assertThrows(LimitePlanExcedidoException.class, () -> service.registrar(formulario("ACTIVO", 4, "MENSUAL")));
        verify(empresas, never()).save(any());
        verify(sucursales, never()).save(any());
    }

    @Test
    void marcaCadaCampoObligatorioFaltante() {
        RegistroEmpresaRequest vacio = new RegistroEmpresaRequest(null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null, null, null);
        RegistroInvalidoException ex = assertThrows(RegistroInvalidoException.class, () -> service.registrar(vacio));
        assertEquals(List.of("nombreComercial", "ruc", "propietario", "correo", "telefono", "direccion"),
                List.copyOf(ex.getErrores().keySet()));
        verify(empresas, never()).save(any());
    }

    @Test
    void validaElFormatoDeRucCorreoYTelefono() {
        RegistroEmpresaRequest r = new RegistroEmpresaRequest("X", "123", null, "Juan", "no-es-correo", "12345",
                "Av 1", null, null, null, 0, null, "SEMANAL", "QUIZAS", null, null, "15/11/2026");
        RegistroInvalidoException ex = assertThrows(RegistroInvalidoException.class, () -> service.registrar(r));
        assertTrue(ex.getErrores().keySet().containsAll(
                List.of("ruc", "correo", "telefono", "sucursales", "ciclo", "estadoInicial", "fechaVencimiento")),
                ex.getErrores().toString());
    }

    @Test
    void rechazaUnRucYaRegistrado() {
        when(empresas.existsByRuc("20601928491")).thenReturn(true);
        RecursoDuplicadoException ex = assertThrows(RecursoDuplicadoException.class,
                () -> service.registrar(formulario("ACTIVO", 1, "MENSUAL")));
        assertTrue(ex.getMessage().contains("20601928491"));
        verify(empresas, never()).save(any());
    }

    @Test
    void rechazaUnPlanInexistente() {
        RegistroEmpresaRequest r = new RegistroEmpresaRequest("X", "20601928491", null, "Juan", "a@b.pe", "987654321",
                "Av 1", null, null, null, 1, 999, null, null, null, null, null);
        assertThrows(RegistroInvalidoException.class, () -> service.registrar(r));
    }

    @Test
    void laEdicionSoloCambiaLoQueLlega() {
        Empresas existente = new Empresas();
        existente.setIdempresa(5);
        existente.setNombreComercial("Viejo");
        existente.setRuc("20111111111");
        existente.setTelefono("999999999");
        existente.setPropietario("Ana");
        when(empresas.findById(5)).thenReturn(Optional.of(existente));

        service.actualizar(5, new RegistroEmpresaRequest("Nuevo nombre", null, null, null, null, null, null,
                null, null, null, null, null, null, null, null, "una nota", null));

        assertEquals("Nuevo nombre", existente.getNombreComercial());
        assertEquals("20111111111", existente.getRuc());
        assertEquals("999999999", existente.getTelefono());
        assertEquals("Ana", existente.getPropietario());
        assertEquals("una nota", existente.getNotas());
        verify(empresas).save(existente);
    }

    @Test
    void laEdicionNoPermiteTomarElRucDeOtraEmpresa() {
        Empresas existente = new Empresas();
        existente.setIdempresa(5);
        existente.setRuc("20111111111");
        when(empresas.findById(5)).thenReturn(Optional.of(existente));
        when(empresas.existsByRuc("20222222222")).thenReturn(true);

        assertThrows(RecursoDuplicadoException.class, () -> service.actualizar(5,
                new RegistroEmpresaRequest(null, "20222222222", null, null, null, null, null,
                        null, null, null, null, null, null, null, null, null, null)));
    }

    @Test
    void editarUnaEmpresaInexistenteFalla() {
        when(empresas.findById(9)).thenReturn(Optional.empty());
        assertThrows(java.util.NoSuchElementException.class, () -> service.actualizar(9,
                new RegistroEmpresaRequest(null, null, null, null, null, null, null,
                        null, null, null, null, null, null, null, null, null, null)));
    }
}
