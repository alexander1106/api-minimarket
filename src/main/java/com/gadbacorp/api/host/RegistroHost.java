package com.gadbacorp.api.host;

import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Registros del archivo de intercambio con el Host. Longitud fija de {@value #LONGITUD} bytes, EBCDIC (IBM037).
 *
 * <pre>
 * Copybook COBOL (los tres tipos de registro redefinen la misma area de 60 bytes):
 *
 *  01 REG-HOST.
 *     05 TIPO-REG            PIC X(1).            H = cabecera, D = venta, T = totales
 *     05 CUERPO-H REDEFINES ...
 *
 *  Cabecera (H)                       Venta (D)                         Totales (T)
 *     FECHA-GEN   PIC 9(8)              ID-VENTA    PIC 9(9)              CANTIDAD   PIC 9(9)
 *     ORIGEN      PIC X(10)             TIPO-COMP   PIC X(1)  B/F         TOTAL-GEN  PIC S9(9)V99 COMP-3
 *     FILLER      PIC X(41)             NRO-COMP    PIC X(8)              FILLER     PIC X(44)
 *                                       FECHA       PIC 9(8)
 *                                       DOC-CLIENTE PIC X(11)
 *                                       TOTAL       PIC S9(9)V99 COMP-3
 *                                       ESTADO      PIC X(10)
 *                                       FILLER      PIC X(6)
 * </pre>
 */
public final class RegistroHost {

    public static final int LONGITUD = 60;
    public static final Charset EBCDIC = Charset.forName("IBM037");
    private static final DateTimeFormatter FECHA = DateTimeFormatter.BASIC_ISO_DATE; // yyyyMMdd
    private static final int DIGITOS_IMPORTE = 11;
    private static final int DECIMALES = 2;

    private RegistroHost() {
    }

    /** Una venta del archivo. */
    public record Venta(long idVenta, String tipoComprobante, String nroComprobante, LocalDate fecha,
                        String documentoCliente, BigDecimal total, String estado) {
    }

    public record Cabecera(LocalDate fechaGeneracion, String origen) {
    }

    public record Totales(long cantidad, BigDecimal total) {
    }

    public static byte[] cabecera(Cabecera c) {
        Escritor w = new Escritor('H');
        w.numerico(c.fechaGeneracion().format(FECHA), 8);
        w.texto(c.origen(), 10);
        return w.bytes();
    }

    public static byte[] venta(Venta v) {
        Escritor w = new Escritor('D');
        w.numerico(String.valueOf(v.idVenta()), 9);
        w.texto(v.tipoComprobante(), 1);
        w.texto(v.nroComprobante(), 8);
        w.numerico(v.fecha().format(FECHA), 8);
        w.texto(v.documentoCliente(), 11);
        w.empaquetado(v.total());
        w.texto(v.estado(), 10);
        return w.bytes();
    }

    public static byte[] totales(Totales t) {
        Escritor w = new Escritor('T');
        w.numerico(String.valueOf(t.cantidad()), 9);
        w.empaquetado(t.total());
        return w.bytes();
    }

    public static char tipo(byte[] registro) {
        verificarLongitud(registro);
        return new String(registro, 0, 1, EBCDIC).charAt(0);
    }

    public static Cabecera leerCabecera(byte[] r) {
        Lector l = new Lector(r, 'H');
        return new Cabecera(LocalDate.parse(l.numerico(8), FECHA), l.texto(10));
    }

    public static Venta leerVenta(byte[] r) {
        Lector l = new Lector(r, 'D');
        long id = Long.parseLong(l.numerico(9));
        String tipoComp = l.texto(1);
        String nro = l.texto(8);
        LocalDate fecha = LocalDate.parse(l.numerico(8), FECHA);
        String doc = l.texto(11);
        BigDecimal total = l.empaquetado();
        String estado = l.texto(10);
        return new Venta(id, tipoComp, nro, fecha, doc, total, estado);
    }

    public static Totales leerTotales(byte[] r) {
        Lector l = new Lector(r, 'T');
        long cantidad = Long.parseLong(l.numerico(9));
        return new Totales(cantidad, l.empaquetado());
    }

    private static void verificarLongitud(byte[] registro) {
        if (registro == null || registro.length != LONGITUD) {
            throw new IllegalArgumentException("Registro de longitud invalida: se esperaban " + LONGITUD + " bytes");
        }
    }

    private static final class Escritor {
        private final byte[] datos = new byte[LONGITUD];
        private int pos;

        Escritor(char tipo) {
            // relleno inicial: espacios EBCDIC (0x40)
            java.util.Arrays.fill(datos, " ".getBytes(EBCDIC)[0]);
            texto(String.valueOf(tipo), 1);
        }

        void texto(String valor, int largo) {
            String v = valor == null ? "" : valor;
            if (v.length() > largo) {
                v = v.substring(0, largo);
            }
            byte[] b = String.format("%-" + largo + "s", v).getBytes(EBCDIC);
            System.arraycopy(b, 0, datos, pos, largo);
            pos += largo;
        }

        /** PIC 9(n) en formato zoned/display: digitos EBCDIC (0xF0-0xF9) con ceros a la izquierda. */
        void numerico(String valor, int largo) {
            if (valor.length() > largo || !valor.chars().allMatch(Character::isDigit)) {
                throw new IllegalArgumentException("Valor numerico invalido para PIC 9(" + largo + "): " + valor);
            }
            byte[] b = ("0".repeat(largo - valor.length()) + valor).getBytes(EBCDIC);
            System.arraycopy(b, 0, datos, pos, largo);
            pos += largo;
        }

        void empaquetado(BigDecimal valor) {
            byte[] b = PackedDecimal.codificar(valor, DIGITOS_IMPORTE, DECIMALES);
            System.arraycopy(b, 0, datos, pos, b.length);
            pos += b.length;
        }

        byte[] bytes() {
            return datos;
        }
    }

    private static final class Lector {
        private final byte[] datos;
        private int pos = 1;

        Lector(byte[] datos, char tipoEsperado) {
            verificarLongitud(datos);
            this.datos = datos;
            char tipo = tipo(datos);
            if (tipo != tipoEsperado) {
                throw new IllegalArgumentException("Tipo de registro inesperado: '" + tipo + "' (se esperaba '" + tipoEsperado + "')");
            }
        }

        String texto(int largo) {
            String s = new String(datos, pos, largo, EBCDIC);
            pos += largo;
            return s.stripTrailing();
        }

        String numerico(int largo) {
            String s = new String(datos, pos, largo, EBCDIC);
            pos += largo;
            if (!s.chars().allMatch(Character::isDigit)) {
                throw new IllegalArgumentException("Campo numerico invalido en la posicion " + (pos - largo) + ": '" + s + "'");
            }
            return s;
        }

        BigDecimal empaquetado() {
            BigDecimal v = PackedDecimal.decodificar(datos, pos, DIGITOS_IMPORTE, DECIMALES);
            pos += PackedDecimal.longitud(DIGITOS_IMPORTE);
            return v;
        }
    }
}
