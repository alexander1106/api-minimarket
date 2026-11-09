package com.gadbacorp.api.excepciones;

/** La operacion es valida pero el estado actual no la permite (rol del sistema, rol con usuarios...). Se responde 409. */
public class OperacionNoPermitidaException extends RuntimeException {

    public OperacionNoPermitidaException(String message) {
        super(message);
    }
}
