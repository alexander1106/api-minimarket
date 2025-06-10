package com.gadbacorp.api.service.delivery;

import java.util.List;
import java.util.Optional;
import com.gadbacorp.api.entity.delivery.Vehiculo;

public interface IVehiculoService {

    List<Vehiculo> buscarTodos();

    Vehiculo guardar(Vehiculo vehiculo);

    Vehiculo modificar(Vehiculo vehiculo);

    Optional<Vehiculo> buscarId(Integer id);

    void eliminar(Integer id);

}
