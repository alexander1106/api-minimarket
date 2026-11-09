package com.gadbacorp.api.config;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Cifrado simetrico AES-256-GCM para datos sensibles (tokens, llaves, documentos).
 * La clave (32 bytes en Base64) se define en app.crypto.key / APP_CRYPTO_KEY.
 * Genera una con:  openssl rand -base64 32
 */
@Service
public class CryptoService {

    private static final int IV_BYTES = 12;
    private static final int TAG_BITS = 128;

    private final SecretKey key;
    private final SecureRandom random = new SecureRandom();

    public CryptoService(@Value("${app.crypto.key:}") String base64Key) {
        if (base64Key == null || base64Key.isBlank()) {
            this.key = null;
        } else {
            byte[] raw = Base64.getDecoder().decode(base64Key);
            if (raw.length != 32) {
                throw new IllegalStateException("app.crypto.key debe ser de 32 bytes (AES-256) en Base64");
            }
            this.key = new SecretKeySpec(raw, "AES");
        }
    }

    public boolean isConfigured() {
        return key != null;
    }

    public String encrypt(String plain) {
        requireKey();
        try {
            byte[] iv = new byte[IV_BYTES];
            random.nextBytes(iv);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(TAG_BITS, iv));
            byte[] enc = cipher.doFinal(plain.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(
                    ByteBuffer.allocate(iv.length + enc.length).put(iv).put(enc).array());
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("No se pudo cifrar", e);
        }
    }

    public String decrypt(String cipherText) {
        requireKey();
        try {
            ByteBuffer buf = ByteBuffer.wrap(Base64.getDecoder().decode(cipherText));
            byte[] iv = new byte[IV_BYTES];
            buf.get(iv);
            byte[] enc = new byte[buf.remaining()];
            buf.get(enc);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(TAG_BITS, iv));
            return new String(cipher.doFinal(enc), StandardCharsets.UTF_8);
        } catch (GeneralSecurityException | RuntimeException e) {
            throw new IllegalArgumentException("Texto cifrado invalido o clave incorrecta", e);
        }
    }

    private void requireKey() {
        if (key == null) {
            throw new IllegalStateException("app.crypto.key no esta configurada (APP_CRYPTO_KEY)");
        }
    }
}
