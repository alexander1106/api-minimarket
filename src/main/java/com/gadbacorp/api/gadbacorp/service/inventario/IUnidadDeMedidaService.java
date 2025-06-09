package com.gadbacorp.api.gadbacorp.service.inventario;

import java.util.List;
import java.util.Optional;

import com.gadbacorp.api.gadbacorp.entity.inventario.UnidadDeMedida;


public interface IUnidadDeMedidaService {
    //listar todos los tipos de unidad de medida de la tabla
    List<UnidadDeMedida> buscarTodos();
    //guarda la unidad de medida
    UnidadDeMedida guardar(UnidadDeMedida unidaddemedida);

    UnidadDeMedida modificar(UnidadDeMedida unidaddemedida);

    Optional<UnidadDeMedida> buscarId(Integer id);

    void eliminar(Integer id);
}
