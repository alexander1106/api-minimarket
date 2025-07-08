package com.gadbacorp.api.service.inventario;

import java.util.List;
import java.util.Optional;

import com.gadbacorp.api.entity.inventario.AjusteInventario;

public interface IAjusteInventarioService {

    List<AjusteInventario> buscarTodos();
    //guarda los Productos
    AjusteInventario guardar(AjusteInventario ajusteInventario);

    AjusteInventario modificar(AjusteInventario ajusteInventario);

    List<AjusteInventario> buscarPorSucursal(Integer idSucursal);
    Optional<AjusteInventario> buscarId(Integer id);

    void eliminar(Integer id);
}