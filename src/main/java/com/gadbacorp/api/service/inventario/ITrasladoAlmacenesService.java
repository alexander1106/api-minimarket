package com.gadbacorp.api.service.inventario;

import java.util.List;
import java.util.Optional;

import com.gadbacorp.api.entity.inventario.TrasladoAlmacenes;

public interface ITrasladoAlmacenesService {

    List<TrasladoAlmacenes> buscarTodos();
    //guarda los Productos
    TrasladoAlmacenes guardar(TrasladoAlmacenes trasladoAlmacenes); 

    TrasladoAlmacenes modificar(TrasladoAlmacenes trasladoAlmacenes);

    Optional<TrasladoAlmacenes> buscarId(Integer id);

    void eliminar(Integer id);

}
