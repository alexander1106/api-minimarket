package com.gadbacorp.api.service.inventario;

import java.util.List;
import java.util.Optional;

import com.gadbacorp.api.entity.inventario.TipoProducto;

public interface ITipoProductoService {
    //listar todos los tipos de producto de la tabla
    List<TipoProducto> buscarTodos();
    //guarda los tipos de productos
    void guardar(TipoProducto tipoproducto);

    void modificar(TipoProducto tipoproducto);

    Optional<TipoProducto> buscarId(Integer id);

    void eliminar(Integer id);
}
