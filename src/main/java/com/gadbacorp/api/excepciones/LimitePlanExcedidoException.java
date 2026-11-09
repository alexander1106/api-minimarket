package com.gadbacorp.api.excepciones;

/** La empresa ya uso todo lo que permite su plan (usuarios o sucursales). Se responde como 409. */
public class LimitePlanExcedidoException extends RuntimeException {

    public LimitePlanExcedidoException(String message) {
        super(message);
    }
}
