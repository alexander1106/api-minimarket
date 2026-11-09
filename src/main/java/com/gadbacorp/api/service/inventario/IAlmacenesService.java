package com.gadbacorp.api.service.inventario;

import java.util.List;
import java.util.Optional;

import com.gadbacorp.api.entity.inventario.Almacenes;

public interface IAlmacenesService {
    List<Almacenes> buscarTodos();
    Almacenes guardar(Almacenes almacen);

    Almacenes modificar(Almacenes almacen);

    Optional<Almacenes> buscarId(Integer id);

    void eliminar(Integer id);

    Optional<Almacenes> buscarPorNombre(String nombre);
    List<Almacenes> buscarTodosPorSucursal(Integer idSucursal);
}
