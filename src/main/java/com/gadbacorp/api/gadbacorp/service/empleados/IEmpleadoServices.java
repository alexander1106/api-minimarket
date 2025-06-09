package com.gadbacorp.api.gadbacorp.service.empleados;

import java.util.List;
import java.util.Optional;

import com.gadbacorp.api.gadbacorp.entity.empleados.Empleado;

public interface IEmpleadoServices {

    List<Empleado> buscarTodos();

    void guardar(Empleado empleado);

    void modificar(Empleado empleado);

    Optional<Empleado> buscarId(Integer id);
    
    void eliminar(Integer id);
}
