package com.gadbacorp.api.service.inventario;

import java.util.List;
import java.util.Optional;

import com.gadbacorp.api.entity.inventario.UnidadDeMedida;

public interface IUnidadDeMedidaService {
    List<UnidadDeMedida> buscarTodos();
    UnidadDeMedida guardar(UnidadDeMedida unidaddemedida);

    UnidadDeMedida modificar(UnidadDeMedida unidaddemedida);

    Optional<UnidadDeMedida> buscarId(Integer id);

    void eliminar(Integer id);
}
