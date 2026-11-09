package com.gadbacorp.api.excepciones;

/** No hay stock suficiente para cubrir la cantidad vendida. Se trata como error de datos (400). */
public class StockInsuficienteException extends IllegalArgumentException {

    public StockInsuficienteException(String message) {
        super(message);
    }
}
