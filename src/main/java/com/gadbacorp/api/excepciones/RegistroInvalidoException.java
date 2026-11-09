package com.gadbacorp.api.excepciones;

import java.util.Map;

/** Datos del formulario invalidos: errores por campo, para que el frontend los muestre junto a cada input (400). */
public class RegistroInvalidoException extends RuntimeException {

    private final transient Map<String, String> errores;

    public RegistroInvalidoException(Map<String, String> errores) {
        super("Datos inválidos");
        this.errores = errores;
    }

    public Map<String, String> getErrores() {
        return errores;
    }
}
