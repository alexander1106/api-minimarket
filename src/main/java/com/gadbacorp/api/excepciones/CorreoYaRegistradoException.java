package com.gadbacorp.api.excepciones;

public class CorreoYaRegistradoException extends Exception {
    public CorreoYaRegistradoException() {
        super("El correo electrónico ya existe en la base de datos, vuelva a intentar");
    }

    public CorreoYaRegistradoException(String mensaje) {
        super(mensaje);
    }
}