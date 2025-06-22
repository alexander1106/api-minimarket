package com.gadbacorp.api.service.empleados;

import java.util.List;
import java.util.Optional;

import com.gadbacorp.api.entity.empleados.Usuarios;

public interface IUsuariosService {

    List<Usuarios> buscarTodos();

    Usuarios guardar(Usuarios empleado);

    Usuarios modificar(Usuarios empleado);

    Optional<Usuarios> buscarId(Integer id);
    
    void eliminar(Integer id);    
     Usuarios obtenerUsuario(String username);

}
