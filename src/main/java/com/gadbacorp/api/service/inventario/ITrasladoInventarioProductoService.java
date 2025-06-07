package com.gadbacorp.api.service.inventario;

import java.util.List;
import java.util.Optional;

import com.gadbacorp.api.entity.inventario.TrasladoInventarioProducto;

public interface ITrasladoInventarioProductoService {

    List<TrasladoInventarioProducto> buscarTodos();

    Optional<TrasladoInventarioProducto> buscarId(Integer id);

    TrasladoInventarioProducto guardar(TrasladoInventarioProducto trasladoInventarioProducto);

    TrasladoInventarioProducto modificar(TrasladoInventarioProducto trasladoInventarioProducto);

    void eliminar(Integer id);

}
 