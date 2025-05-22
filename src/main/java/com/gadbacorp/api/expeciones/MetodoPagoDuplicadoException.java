package com.gadbacorp.api.expeciones;

public class MetodoPagoDuplicadoException extends RuntimeException {
    public MetodoPagoDuplicadoException(String mensaje) {
        super(mensaje);
    }
}
