package com.gadbacorp.api.service.inventario;

import java.util.List;
import java.util.Optional;

import com.gadbacorp.api.entity.inventario.Almacenes;

public interface IAlmacenesService {
    //listar todos los Almacenes de la tabla
    List<Almacenes> buscarTodos();
    //guarda los clientes
    void guardar(Almacenes almacen);

    void modificar(Almacenes almacen);

    Optional<Almacenes> buscarId(Integer id);

    void eliminar(Integer id);
}
