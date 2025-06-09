package com.gadbacorp.api.service.inventario;

import java.util.List;
import java.util.Optional;

import com.gadbacorp.api.entity.inventario.InventarioProducto;

public interface IInventarioProductoService {

    List<InventarioProducto> buscarTodos();
    Optional<InventarioProducto> buscarId(Integer id);
    InventarioProducto guardar(InventarioProducto inventarioProducto);
    InventarioProducto modificar(InventarioProducto inventarioProducto);
    void eliminar(Integer id);

}
