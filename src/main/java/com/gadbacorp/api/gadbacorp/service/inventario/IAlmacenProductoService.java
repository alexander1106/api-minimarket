package com.gadbacorp.api.gadbacorp.service.inventario;

import java.util.List;
import java.util.Optional;

import com.gadbacorp.api.gadbacorp.entity.inventario.AlmacenProducto;

public interface IAlmacenProductoService {

    List<AlmacenProducto> buscarTodos();
    //guarda los Productos
    AlmacenProducto guardar(AlmacenProducto almacenproducto);

    AlmacenProducto modificar(AlmacenProducto almacenproducto);

    Optional<AlmacenProducto> buscarId(Integer id);

    void eliminar(Integer id);
     Optional<AlmacenProducto> buscarPorProductoYAlmacen(Integer idProd, Integer idAlm);

}
