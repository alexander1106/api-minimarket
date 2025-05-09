package com.gadbacorp.api.service.inventario;

import java.util.List;
import java.util.Optional;

import com.gadbacorp.api.entity.inventario.Productos;

public interface IProductosService {
    
    //listar todos los Productos de la tabla
    List<Productos> buscarTodos();
    //guarda los Productos
    void guardar(Productos producto);

    void modificar(Productos producto);

    Optional<Productos> buscarId(Integer id);

    void eliminar(Integer id);
}
