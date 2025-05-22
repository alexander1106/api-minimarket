package com.gadbacorp.api.service.inventario;

import java.util.List;
import java.util.Optional;

import com.gadbacorp.api.entity.inventario.AlmacenProducto;

public interface IAlmacenProductoService {

    List<AlmacenProducto> buscarTodos();
    //guarda los Productos
    void guardar(AlmacenProducto almacenproducto);

    void modificar(AlmacenProducto almacenproducto);

    Optional<AlmacenProducto> buscarId(Integer id);

    void eliminar(Integer id);

}
