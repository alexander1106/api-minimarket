package com.gadbacorp.api.service.inventario;

import java.util.List;
import java.util.Optional;

import com.gadbacorp.api.entity.inventario.TipoProducto;

public interface ITipoProductoService {
    List<TipoProducto> buscarTodos();
    TipoProducto guardar(TipoProducto tipoproducto);

    TipoProducto modificar(TipoProducto tipoproducto);

    Optional<TipoProducto> buscarId(Integer id);

    void eliminar(Integer id);
}
