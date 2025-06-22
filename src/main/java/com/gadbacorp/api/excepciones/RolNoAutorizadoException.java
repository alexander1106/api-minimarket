package com.gadbacorp.api.excepciones;

public class RolNoAutorizadoException  extends Exception{
    public RolNoAutorizadoException(){
        super("El usuario ingreaado no tiene los permsiso necesarios para ingresar a este modulo");
    }
    public RolNoAutorizadoException(String mensaje){
        super(mensaje);
    }
    

}
