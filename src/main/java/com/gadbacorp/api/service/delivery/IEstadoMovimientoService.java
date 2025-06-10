package com.gadbacorp.api.service.delivery;

import java.util.List;
import java.util.Optional;

import com.gadbacorp.api.entity.delivery.EstadoMovimiento;

public interface IEstadoMovimientoService {

        //listar todos los clientes de la tabla
    List<EstadoMovimiento> buscarTodos();

    EstadoMovimiento guardar(EstadoMovimiento estadoMovimiento);

    EstadoMovimiento modificar(EstadoMovimiento estadoMovimiento);

    Optional<EstadoMovimiento> buscarId(Integer id);

    void eliminar(Integer id);

}

