package com.gadbacorp.api.service.inventario;

import java.util.List;
import java.util.Optional;

import com.gadbacorp.api.entity.inventario.AjusteInventario;
import com.gadbacorp.api.entity.inventario.AjusteInventarioDTO;

public interface IAjusteInventarioService {

    List<AjusteInventario> buscarTodos();
    //guarda los Productos
    AjusteInventario guardar(AjusteInventario ajusteInventario);

    AjusteInventario modificar(AjusteInventario ajusteInventario);

    AjusteInventario ajustarStock(AjusteInventarioDTO dto);
    AjusteInventario modificarAjuste(AjusteInventarioDTO dto);

    Optional<AjusteInventario> buscarId(Integer id);

    void eliminar(Integer id);
}