package com.gadbacorp.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.gadbacorp.api.entity.administrable.Empresas;
import com.gadbacorp.api.entity.compras.Compras;
import com.gadbacorp.api.entity.compras.ProveedorDTO;
import com.gadbacorp.api.entity.compras.Proveedores;
import com.gadbacorp.api.excepciones.OperacionNoPermitidaException;
import com.gadbacorp.api.excepciones.RecursoDuplicadoException;
import com.gadbacorp.api.excepciones.RegistroInvalidoException;
import com.gadbacorp.api.repository.administrable.EmpresasRepository;
import com.gadbacorp.api.repository.compras.ComprasRepository;
import com.gadbacorp.api.repository.compras.ProveedoresRepository;
import com.gadbacorp.api.service.jpa.compras.ProveedorRegistroService;

class ProveedorRegistroServiceTest {

    private ProveedoresRepository proveedores;
    private EmpresasRepository empresas;
    private ComprasRepository compras;
    private ProveedorRegistroService service;
    private Empresas empresa;

    @BeforeEach
    void setUp() {
        proveedores = mock(ProveedoresRepository.class);
        empresas = mock(EmpresasRepository.class);
        compras = mock(ComprasRepository.class);
        service = new ProveedorRegistroService(proveedores, empresas, compras);

        empresa = new Empresas();
        empresa.setIdempresa(1);
        when(empresas.findById(1)).thenReturn(Optional.of(empresa));
        when(proveedores.save(any(Proveedores.class))).thenAnswer(i -> i.getArgument(0));
    }

    private ProveedorDTO dto() {
        ProveedorDTO d = new ProveedorDTO();
        d.setNombre("  Distribuidora Andina SAC ");
        d.setRuc("20601928491");
        d.setRegimen("General");
        d.setTelefono("+51 987 654 321");
        d.setGmail("ventas@andina.pe");
        d.setDireccion("Av. Industrial 123");
        d.setIdEmpresa(1);
        return d;
    }

    @Test
    void creaElProveedorActivoConFechaDeAltaDelServidor() {
        ProveedorDTO d = dto();
        d.setEstado(0);                        // un estado enviado por el cliente no cuenta
        d.setFecha_registro(Date.valueOf("2001-01-01"));

        Proveedores p = service.crear(d);

        assertEquals("Distribuidora Andina SAC", p.getNombre());
        assertEquals(1, p.getEstado());
        assertNotNull(p.getFecha_registro());
        assertTrue(p.getFecha_registro().getTime() > Date.valueOf("2020-01-01").getTime());
        assertEquals(empresa, p.getEmpresa());
    }

    @Test
    void lasCadenasVaciasOpcionalesSeGuardanComoNulas() {
        ProveedorDTO d = dto();
        d.setTelefono("  ");
        d.setGmail("");
        d.setRegimen(null);
        Proveedores p = service.crear(d);
        assertNull(p.getTelefono());
        assertNull(p.getGmail());
        assertNull(p.getRegimen());
    }

    @Test
    void validaNombreRucCorreoYTelefono() {
        ProveedorDTO d = new ProveedorDTO();
        d.setRuc("12345");
        d.setGmail("no-es-correo");
        d.setTelefono("123");
        d.setIdEmpresa(1);
        RegistroInvalidoException ex = assertThrows(RegistroInvalidoException.class, () -> service.crear(d));
        assertEquals(List.of("nombre", "ruc", "gmail", "telefono"), List.copyOf(ex.getErrores().keySet()));
        verify(proveedores, never()).save(any());
    }

    @Test
    void elRucDebeEmpezarConUnPrefijoValido() {
        ProveedorDTO d = dto();
        d.setRuc("30601928491");
        assertThrows(RegistroInvalidoException.class, () -> service.crear(d));
        d.setRuc("10456789012");   // persona natural
        service.crear(d);
    }

    @Test
    void exigeUnaEmpresaExistente() {
        ProveedorDTO sinEmpresa = dto();
        sinEmpresa.setIdEmpresa(null);
        assertTrue(assertThrows(RegistroInvalidoException.class, () -> service.crear(sinEmpresa))
                .getErrores().containsKey("idEmpresa"));

        ProveedorDTO inexistente = dto();
        inexistente.setIdEmpresa(99);
        when(empresas.findById(99)).thenReturn(Optional.empty());
        assertTrue(assertThrows(RegistroInvalidoException.class, () -> service.crear(inexistente))
                .getErrores().containsKey("idEmpresa"));
    }

    @Test
    void rechazaUnRucRepetidoEnLaMismaEmpresa() {
        when(proveedores.existsByRucAndEmpresaIdempresa("20601928491", 1)).thenReturn(true);
        RecursoDuplicadoException ex = assertThrows(RecursoDuplicadoException.class, () -> service.crear(dto()));
        assertTrue(ex.getMessage().contains("20601928491"));
        verify(proveedores, never()).save(any());
    }

    private Proveedores existente() {
        Proveedores p = new Proveedores();
        p.setIdProveedor(7);
        p.setNombre("Viejo");
        p.setRuc("20111111111");
        p.setEstado(1);
        p.setFecha_registro(Date.valueOf("2026-01-15"));
        p.setEmpresa(empresa);
        when(proveedores.findById(7)).thenReturn(Optional.of(p));
        return p;
    }

    @Test
    void laEdicionConservaFechaDeRegistroEstadoYEmpresa() {
        Proveedores p = existente();
        ProveedorDTO d = dto();
        d.setIdProveedor(7);
        d.setEstado(0);
        d.setFecha_registro(null);

        Proveedores actualizado = service.actualizar(d);

        assertEquals("Distribuidora Andina SAC", actualizado.getNombre());
        assertEquals(Date.valueOf("2026-01-15"), actualizado.getFecha_registro());
        assertEquals(1, actualizado.getEstado());
        assertEquals(empresa, actualizado.getEmpresa());
        assertEquals(p, actualizado);
    }

    @Test
    void editarUnProveedorInexistenteDa404EnVezDeCrearUnoNuevo() {
        when(proveedores.findById(99)).thenReturn(Optional.empty());
        ProveedorDTO d = dto();
        d.setIdProveedor(99);
        assertThrows(NoSuchElementException.class, () -> service.actualizar(d));
        verify(proveedores, never()).save(any());
    }

    @Test
    void laEdicionExigeElId() {
        assertTrue(assertThrows(RegistroInvalidoException.class, () -> service.actualizar(dto()))
                .getErrores().containsKey("idProveedor"));
    }

    @Test
    void noSePuedeMoverUnProveedorAOtraEmpresa() {
        existente();
        ProveedorDTO d = dto();
        d.setIdProveedor(7);
        d.setIdEmpresa(2);
        assertThrows(OperacionNoPermitidaException.class, () -> service.actualizar(d));
    }

    @Test
    void laEdicionNoPermiteElRucDeOtroProveedor() {
        existente();
        when(proveedores.existsByRucAndEmpresaIdempresaAndIdProveedorNot("20601928491", 1, 7)).thenReturn(true);
        ProveedorDTO d = dto();
        d.setIdProveedor(7);
        assertThrows(RecursoDuplicadoException.class, () -> service.actualizar(d));
    }

    @Test
    void eliminaUnProveedorSinCompras() {
        existente();
        when(compras.findByProveedorIdProveedor(7)).thenReturn(List.of());
        service.eliminar(7);
        verify(proveedores).deleteById(7);
    }

    @Test
    void noEliminaUnProveedorConCompras() {
        existente();
        when(compras.findByProveedorIdProveedor(7)).thenReturn(List.of(new Compras(), new Compras()));
        OperacionNoPermitidaException ex = assertThrows(OperacionNoPermitidaException.class, () -> service.eliminar(7));
        assertTrue(ex.getMessage().contains("2 compra(s)"), ex.getMessage());
        verify(proveedores, never()).deleteById(any());
    }

    @Test
    void eliminarUnoInexistenteDa404() {
        when(proveedores.findById(5)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> service.eliminar(5));
    }

    private ProveedorDTO fichaCompleta() {
        ProveedorDTO d = dto();
        d.setRazonSocial("DISTRIBUIDORA ANDINA S.A.C.");
        d.setDistrito("San Borja");
        d.setProvincia("Lima");
        d.setDepartamento("Lima");
        d.setContactoNombre("María Quispe");
        d.setContactoCargo("Ejecutiva de ventas");
        d.setTelefonoSecundario("+51 912 345 678");
        d.setSitioWeb("www.andina.pe");
        d.setRubro("Abarrotes");
        d.setCondicionPago("credito");
        d.setDiasCredito(30);
        d.setMoneda("usd");
        d.setLimiteCredito(new java.math.BigDecimal("5000.00"));
        d.setBanco("BCP");
        d.setNumeroCuenta("193-1234567-0-12");
        d.setCci("00219300123456701234");
        d.setEstadoSunat("ACTIVO");
        d.setCondicionSunat("HABIDO");
        d.setNotas("Entrega los martes");
        return d;
    }

    @Test
    void guardaTodaLaFichaDelProveedor() {
        Proveedores p = service.crear(fichaCompleta());

        assertEquals("DISTRIBUIDORA ANDINA S.A.C.", p.getRazonSocial());
        assertEquals("María Quispe", p.getContactoNombre());
        assertEquals("Abarrotes", p.getRubro());
        assertEquals("CREDITO", p.getCondicionPago());
        assertEquals(30, p.getDiasCredito());
        assertEquals("USD", p.getMoneda());
        assertEquals(new java.math.BigDecimal("5000.00"), p.getLimiteCredito());
        assertEquals("00219300123456701234", p.getCci());
        assertEquals("HABIDO", p.getCondicionSunat());
        assertEquals("Entrega los martes", p.getNotas());
    }

    @Test
    void sinDatosComercialesAsumeContadoYSoles() {
        Proveedores p = service.crear(dto());
        assertEquals("CONTADO", p.getCondicionPago());
        assertNull(p.getDiasCredito());
        assertEquals("PEN", p.getMoneda());
    }

    @Test
    void alContadoSeDescartanLosDiasDeCredito() {
        ProveedorDTO d = dto();
        d.setCondicionPago("CONTADO");
        d.setDiasCredito(45);
        assertNull(service.crear(d).getDiasCredito());
    }

    @Test
    void elCreditoExigeLosDias() {
        ProveedorDTO d = dto();
        d.setCondicionPago("CREDITO");
        assertTrue(assertThrows(RegistroInvalidoException.class, () -> service.crear(d))
                .getErrores().containsKey("diasCredito"));
        d.setDiasCredito(400);
        assertTrue(assertThrows(RegistroInvalidoException.class, () -> service.crear(d))
                .getErrores().containsKey("diasCredito"));
        d.setDiasCredito(30);
        service.crear(d);
    }

    @Test
    void validaLosDatosBancariosWebYContacto() {
        ProveedorDTO d = dto();
        d.setCci("123");
        d.setNumeroCuenta("abc");
        d.setSitioWeb("no es una web");
        d.setTelefonoSecundario("12");
        d.setCondicionPago("A PLAZOS");
        d.setMoneda("EUR");
        d.setLimiteCredito(new java.math.BigDecimal("-1"));
        d.setNotas("x".repeat(1001));
        RegistroInvalidoException ex = assertThrows(RegistroInvalidoException.class, () -> service.crear(d));
        assertTrue(ex.getErrores().keySet().containsAll(List.of("cci", "numeroCuenta", "sitioWeb",
                "telefonoSecundario", "condicionPago", "moneda", "limiteCredito", "notas")), ex.getErrores().toString());
    }

    @Test
    void laEdicionActualizaLaFichaSinTocarFechaNiEstado() {
        Proveedores p = existente();
        ProveedorDTO d = fichaCompleta();
        d.setIdProveedor(7);

        service.actualizar(d);

        assertEquals("BCP", p.getBanco());
        assertEquals("Ejecutiva de ventas", p.getContactoCargo());
        assertEquals(Date.valueOf("2026-01-15"), p.getFecha_registro());
        assertEquals(1, p.getEstado());
    }
}
