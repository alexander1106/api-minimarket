package com.gadbacorp.api.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.gadbacorp.api.entity.administrable.EmpresaResumenDTO;
import com.gadbacorp.api.entity.administrable.Empresas;
import com.gadbacorp.api.entity.administrable.Plan;
import com.gadbacorp.api.excepciones.LimitePlanExcedidoException;
import com.gadbacorp.api.repository.administrable.EmpresasRepository;
import com.gadbacorp.api.repository.administrable.SucursalesRepository;
import com.gadbacorp.api.repository.empleados.UsuarioRepository;
import com.gadbacorp.api.service.jpa.administrable.EmpresaResumenService;

class EmpresaResumenServiceTest {

    private static final LocalDate HOY = LocalDate.of(2026, 10, 10);

    private final Plan gratis = new Plan("Gratis", new BigDecimal("0.00"), 1, 1);
    private final Plan basico = new Plan("Básico", new BigDecimal("79.00"), 3, 1);
    private final Plan empresarial = new Plan("Empresarial", new BigDecimal("349.00"), 25, 10);

    private Empresas empresa(Plan plan, String vence, boolean prueba) {
        Empresas e = new Empresas();
        e.setIdempresa(1);
        e.setRazonsocial("24 Horas Market Chiclayo");
        e.setRuc("20609182341");
        e.setCiudad("Chiclayo");
        e.setDistrito("Chiclayo");
        e.setPropietario("Fernando Tello Ríos");
        e.setTelefono("+51 979 332 108");
        e.setFechaRegistro(LocalDate.of(2026, 9, 15));
        e.setPlan(plan);
        e.setCiclo("MENSUAL");
        e.setFechaVencimiento(vence == null ? null : LocalDate.parse(vence));
        e.setEnPrueba(prueba);
        return e;
    }

    @Test
    void empresaActivaConPlanEmpresarialCoincideConLaFilaDeLaPantalla() {
        EmpresaResumenDTO dto = EmpresaResumenService.resumir(
                empresa(empresarial, "2026-11-15", false), 3, 14, HOY, null);

        assertEquals("24 Horas Market Chiclayo", dto.razonsocial());
        assertEquals(3, dto.cantidadSucursales());
        assertEquals("Fernando Tello Ríos", dto.propietario().nombre());
        assertEquals("+51 979 332 108", dto.propietario().telefono());
        assertEquals("Chiclayo", dto.ubicacion().ciudad());
        assertEquals("Empresarial", dto.plan().nombre());
        assertEquals("MENSUAL", dto.plan().ciclo());
        assertEquals(new BigDecimal("349.00"), dto.ingresoMensual());
        assertEquals("PEN", dto.moneda());
        assertEquals(LocalDate.of(2026, 11, 15), dto.vencimiento());
        assertEquals(LocalDate.of(2026, 9, 15), dto.fechaAlta());
        assertEquals(14, dto.usuarios().usados());
        assertEquals(25, dto.usuarios().limite());
        assertEquals("ACTIVO", dto.estadoSuscripcion().codigo());
        assertEquals("Activo", dto.estadoSuscripcion().etiqueta());
    }

    @Test
    void empresaEnPruebaMuestraLosDiasRestantesYNoSumaIngreso() {
        EmpresaResumenDTO dto = EmpresaResumenService.resumir(
                empresa(gratis, "2026-10-24", true), 1, 1, HOY, null);

        assertEquals("PRUEBA", dto.estadoSuscripcion().codigo());
        assertEquals("Prueba (14d)", dto.estadoSuscripcion().etiqueta());
        assertEquals(14L, dto.estadoSuscripcion().diasRestantes());
        assertEquals(0, dto.ingresoMensual().compareTo(BigDecimal.ZERO));
        assertEquals(1, dto.usuarios().limite());
    }

    @Test
    void unPlanDePagoEnPruebaTampocoCobraTodavia() {
        EmpresaResumenDTO dto = EmpresaResumenService.resumir(
                empresa(basico, "2026-10-17", true), 1, 2, HOY, null);
        assertEquals("Prueba (7d)", dto.estadoSuscripcion().etiqueta());
        assertEquals(0, dto.ingresoMensual().compareTo(BigDecimal.ZERO));
    }

    @Test
    void laEmpresaVencidaDejaDeAportarIngreso() {
        EmpresaResumenDTO dto = EmpresaResumenService.resumir(
                empresa(basico, "2026-10-01", false), 1, 3, HOY, null);

        assertEquals("VENCIDO", dto.estadoSuscripcion().codigo());
        assertEquals(-9L, dto.estadoSuscripcion().diasRestantes());
        assertEquals(0, dto.ingresoMensual().compareTo(BigDecimal.ZERO));
    }

    @Test
    void vencerHoyTodaviaEsActivo() {
        assertEquals("ACTIVO", EmpresaResumenService.calcularEstado(empresa(basico, "2026-10-10", false), HOY).codigo());
    }

    @Test
    void laSuspensionTienePrioridadSobreTodoLoDemas() {
        Empresas e = empresa(empresarial, "2026-11-15", false);
        e.setSuspendida(true);
        EmpresaResumenDTO dto = EmpresaResumenService.resumir(e, 3, 14, HOY, null);

        assertEquals("SUSPENDIDO", dto.estadoSuscripcion().codigo());
        assertEquals(0, dto.ingresoMensual().compareTo(BigDecimal.ZERO));
    }

    @Test
    void sinPlanNiVencimientoEstaActivaSinLimites() {
        EmpresaResumenDTO dto = EmpresaResumenService.resumir(empresa(null, null, false), 1, 5, HOY, null);

        assertEquals("ACTIVO", dto.estadoSuscripcion().codigo());
        assertNull(dto.plan());
        assertNull(dto.usuarios().limite());
        assertNull(dto.estadoSuscripcion().diasRestantes());
        assertEquals(0, dto.ingresoMensual().compareTo(BigDecimal.ZERO));
    }

    @Test
    void sunatVerificadoSoloSiSeConfirmo() {
        Empresas e = empresa(basico, "2026-11-15", false);
        assertEquals(false, EmpresaResumenService.resumir(e, 1, 1, HOY, null).sunatVerificado());
        e.setSunatVerificado(true);
        assertEquals(true, EmpresaResumenService.resumir(e, 1, 1, HOY, null).sunatVerificado());
    }

    private EmpresaResumenService servicio(long usuarios, long sucursales) {
        UsuarioRepository usuariosRepo = mock(UsuarioRepository.class);
        SucursalesRepository sucursalesRepo = mock(SucursalesRepository.class);
        when(usuariosRepo.countBySucursal_Empresa_Idempresa(1)).thenReturn(usuarios);
        when(sucursalesRepo.countByEmpresaIdempresa(1)).thenReturn(sucursales);
        return new EmpresaResumenService(mock(EmpresasRepository.class), sucursalesRepo, usuariosRepo,
                Clock.systemDefaultZone(), "./uploads");
    }

    @Test
    void noSePuedeAgregarUnUsuarioCuandoSeAgotoElCupo() {
        Empresas e = empresa(basico, null, false); // maximo 3 usuarios
        LimitePlanExcedidoException ex = assertThrows(LimitePlanExcedidoException.class,
                () -> servicio(3, 1).verificarCupoUsuarios(e));
        assertTrue(ex.getMessage().contains("3/3"), ex.getMessage());
        assertDoesNotThrow(() -> servicio(2, 1).verificarCupoUsuarios(e));
    }

    @Test
    void noSePuedeAgregarUnaSucursalCuandoSeAgotoElCupo() {
        Empresas e = empresa(basico, null, false); // maximo 1 sucursal
        assertThrows(LimitePlanExcedidoException.class, () -> servicio(1, 1).verificarCupoSucursales(e));
        assertDoesNotThrow(() -> servicio(1, 0).verificarCupoSucursales(e));
    }

    @Test
    void sinPlanOSinLimiteNoSeRestringe() {
        assertDoesNotThrow(() -> servicio(999, 999).verificarCupoUsuarios(empresa(null, null, false)));
        Plan ilimitado = new Plan("A medida", new BigDecimal("999.00"), null, null);
        assertDoesNotThrow(() -> servicio(999, 999).verificarCupoUsuarios(empresa(ilimitado, null, false)));
        assertDoesNotThrow(() -> servicio(999, 999).verificarCupoSucursales(empresa(ilimitado, null, false)));
    }
}
