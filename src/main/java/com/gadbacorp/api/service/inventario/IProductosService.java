package com.gadbacorp.api.service.inventario;

import java.util.List;
import java.util.Optional;

import com.gadbacorp.api.entity.inventario.Productos;

public interface IProductosService {
    
    // Métodos para entidades
    List<Productos> buscarTodos();
    Productos guardar(Productos producto);
    Productos modificar(Productos producto);
    Optional<Productos> buscarId(Integer id);
    void eliminar(Integer id);
    List<Productos> listarProductosPorCategoria(Integer id);

}

 