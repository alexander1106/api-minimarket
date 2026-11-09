package com.gadbacorp.api.excepciones;

/** Ya existe un registro con ese dato unico (por ejemplo un RUC). Se responde como 409. */
public class RecursoDuplicadoException extends RuntimeException {

    public RecursoDuplicadoException(String message) {
        super(message);
    }
}
