package com.gadbacorp.api.service.inventario;

import java.util.List;
import java.util.Optional;

import com.gadbacorp.api.entity.inventario.Inventario;

public interface IInventarioService {
   
    List<Inventario> buscarTodos();
    //guarda los Productos
    Inventario guardar(Inventario inventario);

    Inventario modificar(Inventario inventario);

    Optional<Inventario> buscarId(Integer id);

    void eliminar(Integer id);
}
