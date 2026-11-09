package com.gadbacorp.api.host;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Decimal empaquetado de COBOL (COMP-3, PIC S9(n)V9(m) COMP-3): dos digitos por byte y, en el ultimo
 * medio byte, el signo (C/F = positivo, D = negativo). Es el formato estandar de importes en un mainframe.
 */
public final class PackedDecimal {

    private PackedDecimal() {
    }

    /** Bytes que ocupa un campo con {@code digitos} digitos (el signo cuenta como un medio byte mas). */
    public static int longitud(int digitos) {
        return (digitos + 2) / 2;
    }

    public static byte[] codificar(BigDecimal valor, int digitos, int decimales) {
        BigDecimal escalado = valor.setScale(decimales, RoundingMode.HALF_UP);
        String numero = escalado.unscaledValue().abs().toString();
        if (numero.length() > digitos) {
            throw new IllegalArgumentException("El valor " + valor + " no cabe en PIC S9(" + (digitos - decimales)
                    + ")V9(" + decimales + ") COMP-3");
        }
        int medios = longitud(digitos) * 2;           // medios bytes totales (digitos + signo)
        StringBuilder nibbles = new StringBuilder("0".repeat(medios - 1 - numero.length())).append(numero);
        nibbles.append(escalado.signum() < 0 ? 'D' : 'C');

        byte[] salida = new byte[medios / 2];
        for (int i = 0; i < salida.length; i++) {
            salida[i] = (byte) ((Character.digit(nibbles.charAt(2 * i), 16) << 4)
                    | Character.digit(nibbles.charAt(2 * i + 1), 16));
        }
        return salida;
    }

    public static BigDecimal decodificar(byte[] datos, int desde, int digitos, int decimales) {
        int longitud = longitud(digitos);
        StringBuilder numero = new StringBuilder();
        int signo = 0;
        for (int i = 0; i < longitud; i++) {
            int b = datos[desde + i] & 0xFF;
            int alto = b >> 4;
            int bajo = b & 0x0F;
            if (alto > 9) {
                throw new IllegalArgumentException("Digito empaquetado invalido en la posicion " + (desde + i));
            }
            numero.append(alto);
            if (i < longitud - 1) {
                if (bajo > 9) {
                    throw new IllegalArgumentException("Digito empaquetado invalido en la posicion " + (desde + i));
                }
                numero.append(bajo);
            } else {
                signo = bajo;
            }
        }
        if (signo < 0xA) {
            throw new IllegalArgumentException("Signo COMP-3 invalido en la posicion " + (desde + longitud - 1));
        }
        BigDecimal valor = new BigDecimal(new java.math.BigInteger(numero.toString()), decimales);
        return signo == 0x0D ? valor.negate() : valor;
    }
}
