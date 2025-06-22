package com.gadbacorp.api.excepciones;

public class NombreRolYaExisteException extends Exception {

    public NombreRolYaExisteException() {
        super("El nombre del rol ya existe en el sistema");
    }

    public NombreRolYaExisteException(String mensaje) {
        super(mensaje);
    }
}
