package com.gadbacorp.api.gadbacorp.service.inventario;

import java.util.List;
import java.util.Optional;

import com.gadbacorp.api.gadbacorp.entity.inventario.Almacenes;

public interface IAlmacenesService {
    //listar todos los Almacenes de la tabla
    List<Almacenes> buscarTodos();
    //guarda los clientes
    Almacenes guardar(Almacenes almacen);

    Almacenes modificar(Almacenes almacen);

    Optional<Almacenes> buscarId(Integer id);

    void eliminar(Integer id);

    Optional<Almacenes> buscarPorNombre(String nombre);
}
