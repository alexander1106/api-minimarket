package com.gadbacorp.api.service.inventario;

import java.util.List;
import java.util.Optional;

import com.gadbacorp.api.entity.inventario.UnidadDeMedida;


public interface IUnidadDeMedidaService {
    //listar todos los tipos de unidad de medida de la tabla
    List<UnidadDeMedida> buscarTodos();
    //guarda la unidad de medida
    void guardar(UnidadDeMedida unidaddemedida);

    void modificar(UnidadDeMedida unidaddemedida);

    Optional<UnidadDeMedida> buscarId(Integer id);

    void eliminar(Integer id);
}
