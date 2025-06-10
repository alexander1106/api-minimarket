package com.gadbacorp.api.service.inventario;

import java.util.List;
import java.util.Optional;

import com.gadbacorp.api.entity.inventario.Promociones;

public interface IPromocionesService {

    List<Promociones> buscarTodos();

    Optional<Promociones> buscarId(Integer id);

    Promociones guardar(Promociones promociones);

    Promociones modificar(Promociones promociones);

    void eliminar(Integer id);
}
