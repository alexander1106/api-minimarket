package com.gadbacorp.api.excepciones;

public class UsuarioDeshabilitadoException  extends Exception {
    public UsuarioDeshabilitadoException(){
        super("El usuario esta deshabilitado en el sistema son invalidos");
    }
    public UsuarioDeshabilitadoException(String mensaje){
        super(mensaje);
    }
}
    