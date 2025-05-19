package com.gadbacorp.api.service.inventario;

import java.util.List;
import java.util.Optional;

import com.gadbacorp.api.entity.inventario.Promociones;

public interface IPromocionesService {

    List<Promociones> buscarTodos();
    //guarda 
    Promociones guardar(Promociones promociones);

    Promociones modificar(Promociones promociones);

    Optional<Promociones> buscarId(Integer id);

    void eliminar(Integer id);
}
