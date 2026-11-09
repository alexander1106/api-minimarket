package com.gadbacorp.api.host;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

class HostTest {

    @Test
    void empaquetaConElVectorConocidoDeCobol() {
        // PIC S9(9)V99 COMP-3 con 12345.67  ->  00 00 12 34 56 7C
        byte[] esperado = {0x00, 0x00, 0x12, 0x34, 0x56, 0x7C};
        assertArrayEquals(esperado, PackedDecimal.codificar(new BigDecimal("12345.67"), 11, 2));
    }

    @Test
    void losNegativosUsanElSignoD() {
        byte[] negativo = PackedDecimal.codificar(new BigDecimal("-0.05"), 11, 2);
        assertEquals((byte) 0x5D, negativo[5]);
        assertEquals(new BigDecimal("-0.05"), PackedDecimal.decodificar(negativo, 0, 11, 2));
    }

    @Test
    void idaYVueltaConservaElValor() {
        for (String v : new String[] {"0.00", "1.00", "99.99", "123456789.12", "-4500.50"}) {
            BigDecimal original = new BigDecimal(v);
            byte[] b = PackedDecimal.codificar(original, 11, 2);
            assertEquals(6, b.length);
            assertEquals(0, original.compareTo(PackedDecimal.decodificar(b, 0, 11, 2)), v);
        }
    }

    @Test
    void redondeaAlosDecimalesDelCampo() {
        byte[] b = PackedDecimal.codificar(new BigDecimal("10.005"), 11, 2);
        assertEquals(new BigDecimal("10.01"), PackedDecimal.decodificar(b, 0, 11, 2));
    }

    @Test
    void rechazaValoresQueNoCaben() {
        assertThrows(IllegalArgumentException.class, () -> PackedDecimal.codificar(new BigDecimal("1000000000.00"), 11, 2));
    }

    @Test
    void rechazaBytesQueNoSonDecimalEmpaquetado() {
        assertThrows(IllegalArgumentException.class,
                () -> PackedDecimal.decodificar(new byte[] {0x0A, 0x00, 0x12, 0x34, 0x56, 0x7C}, 0, 11, 2));
        assertThrows(IllegalArgumentException.class,
                () -> PackedDecimal.decodificar(new byte[] {0x00, 0x00, 0x12, 0x34, 0x56, 0x77}, 0, 11, 2));
    }

    private final RegistroHost.Venta venta = new RegistroHost.Venta(42, "B", "B-000042",
            LocalDate.of(2026, 10, 17), "12345678", new BigDecimal("59.90"), "PAGADA");

    @Test
    void elRegistroEsEbcdicDeLongitudFija() {
        byte[] r = RegistroHost.venta(venta);

        assertEquals(RegistroHost.LONGITUD, r.length);
        assertEquals((byte) 0xC4, r[0]);   // 'D' en EBCDIC
        assertEquals((byte) 0xF0, r[1]);   // '0' en EBCDIC (zoned decimal)
        assertEquals((byte) 0xF4, r[8]);   // id 000000042 -> '4'
        assertEquals((byte) 0xF2, r[9]);   // '2'
        assertEquals((byte) 0x40, r[RegistroHost.LONGITUD - 1]); // relleno: espacio EBCDIC
    }

    @Test
    void ventaIdaYVuelta() {
        assertEquals(venta, RegistroHost.leerVenta(RegistroHost.venta(venta)));
    }

    @Test
    void cabeceraYTotalesIdaYVuelta() {
        RegistroHost.Cabecera cab = new RegistroHost.Cabecera(LocalDate.of(2026, 1, 31), "MINIMARKET");
        assertEquals(cab, RegistroHost.leerCabecera(RegistroHost.cabecera(cab)));
        assertEquals((byte) 0xC8, RegistroHost.cabecera(cab)[0]); // 'H'

        RegistroHost.Totales tot = new RegistroHost.Totales(3, new BigDecimal("150.25"));
        assertEquals(tot, RegistroHost.leerTotales(RegistroHost.totales(tot)));
        assertEquals((byte) 0xE3, RegistroHost.totales(tot)[0]); // 'T'
    }

    @Test
    void textosLargosSeTruncanAlAnchoDelCampo() {
        RegistroHost.Venta larga = new RegistroHost.Venta(1, "F", "F-000001-EXTRA", LocalDate.of(2026, 1, 1),
                "20123456789", BigDecimal.TEN, "UN-ESTADO-MUY-LARGO");
        RegistroHost.Venta leida = RegistroHost.leerVenta(RegistroHost.venta(larga));
        assertEquals("F-000001", leida.nroComprobante());
        assertEquals("UN-ESTADO-", leida.estado());
    }

    @Test
    void rechazaUnTipoDeRegistroInesperado() {
        assertThrows(IllegalArgumentException.class, () -> RegistroHost.leerVenta(RegistroHost.cabecera(
                new RegistroHost.Cabecera(LocalDate.of(2026, 1, 1), "X"))));
        assertThrows(IllegalArgumentException.class, () -> RegistroHost.leerVenta(new byte[10]));
    }

    private final HostVentasService servicio = new HostVentasService(null);

    private List<RegistroHost.Venta> tresVentas() {
        return List.of(
                new RegistroHost.Venta(1, "B", "B-000001", LocalDate.of(2026, 10, 17), "11111111", new BigDecimal("10.50"), "PAGADA"),
                new RegistroHost.Venta(2, "F", "F-000001", LocalDate.of(2026, 10, 17), "20123456789", new BigDecimal("200.00"), "PAGADA"),
                new RegistroHost.Venta(3, "B", "B-000002", LocalDate.of(2026, 10, 17), "22222222", new BigDecimal("39.75"), "PAGADA"));
    }

    @Test
    void generaYLeeUnArchivoConTotalesDeControl() {
        byte[] archivo = servicio.generar(LocalDate.of(2026, 10, 17), tresVentas());

        assertEquals(5 * RegistroHost.LONGITUD, archivo.length); // H + 3 D + T
        HostVentasService.Lectura lectura = servicio.leer(archivo);
        assertEquals(3, lectura.ventas().size());
        assertEquals(3, lectura.totales().cantidad());
        assertEquals(new BigDecimal("250.25"), lectura.totales().total());
        assertEquals("MINIMARKET", lectura.cabecera().origen());
        assertEquals(tresVentas(), lectura.ventas());
    }

    @Test
    void unArchivoSinVentasEsValido() {
        HostVentasService.Lectura lectura = servicio.leer(servicio.generar(LocalDate.of(2026, 10, 17), List.of()));
        assertEquals(0, lectura.ventas().size());
        assertEquals(0, lectura.totales().total().compareTo(BigDecimal.ZERO));
    }

    @Test
    void detectaUnImporteAlteradoPorLosTotalesDeControl() {
        byte[] archivo = servicio.generar(LocalDate.of(2026, 10, 17), tresVentas());
        // cambia el importe de la segunda venta (el COMP-3 empieza en el byte 38 del registro): 200.00 -> 900.00
        int inicioImporte = 2 * RegistroHost.LONGITUD + 38;
        archivo[inicioImporte + 3] = (byte) 0x90;
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> servicio.leer(archivo));
        assertTrue(ex.getMessage().contains("Totales de control"), ex.getMessage());
    }

    @Test
    void detectaVentasFaltantes() {
        byte[] completo = servicio.generar(LocalDate.of(2026, 10, 17), tresVentas());
        // se quita la ultima venta pero se conserva el registro de totales
        byte[] recortado = new byte[completo.length - RegistroHost.LONGITUD];
        System.arraycopy(completo, 0, recortado, 0, 3 * RegistroHost.LONGITUD);
        System.arraycopy(completo, 4 * RegistroHost.LONGITUD, recortado, 3 * RegistroHost.LONGITUD, RegistroHost.LONGITUD);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> servicio.leer(recortado));
        assertTrue(ex.getMessage().contains("declara 3 ventas y trae 2"), ex.getMessage());
    }

    @Test
    void rechazaArchivosTruncadosOVacios() {
        assertThrows(IllegalArgumentException.class, () -> servicio.leer(new byte[0]));
        assertThrows(IllegalArgumentException.class, () -> servicio.leer(new byte[RegistroHost.LONGITUD + 7]));
        assertThrows(IllegalArgumentException.class, () -> servicio.leer(new byte[RegistroHost.LONGITUD]));
    }
}
