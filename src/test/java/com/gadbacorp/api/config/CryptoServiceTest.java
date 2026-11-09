package com.gadbacorp.api.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Base64;

import org.junit.jupiter.api.Test;

class CryptoServiceTest {

    private static String clave(int byteValue) {
        byte[] k = new byte[32];
        java.util.Arrays.fill(k, (byte) byteValue);
        return Base64.getEncoder().encodeToString(k);
    }

    @Test
    void cifraYDescifra() {
        CryptoService crypto = new CryptoService(clave(7));
        String cifrado = crypto.encrypt("llave-secreta");

        assertNotEquals("llave-secreta", cifrado);
        assertEquals("llave-secreta", crypto.decrypt(cifrado));
    }

    @Test
    void cadaCifradoUsaIvDistinto() {
        CryptoService crypto = new CryptoService(clave(7));
        assertNotEquals(crypto.encrypt("igual"), crypto.encrypt("igual"));
    }

    @Test
    void fallaConClaveIncorrecta() {
        String cifrado = new CryptoService(clave(1)).encrypt("dato");
        assertThrows(IllegalArgumentException.class, () -> new CryptoService(clave(2)).decrypt(cifrado));
    }

    @Test
    void sinClaveNoEstaConfigurado() {
        CryptoService crypto = new CryptoService("");
        assertFalse(crypto.isConfigured());
        assertThrows(IllegalStateException.class, () -> crypto.encrypt("x"));
    }

    @Test
    void rechazaClaveDeTamanoIncorrecto() {
        String corta = Base64.getEncoder().encodeToString(new byte[16]);
        assertThrows(IllegalStateException.class, () -> new CryptoService(corta));
    }
}
