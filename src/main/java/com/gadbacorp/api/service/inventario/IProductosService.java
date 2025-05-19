package com.gadbacorp.api.service.inventario;

import java.util.List;
import java.util.Optional;

import com.gadbacorp.api.entity.inventario.Productos;
import com.gadbacorp.api.entity.inventario.ProductosDTO;

public interface IProductosService {
    
    // Métodos para entidades
    List<Productos> buscarTodos();
    Productos guardar(Productos producto);
    Productos modificar(Productos producto);
    Optional<Productos> buscarId(Integer id);
    void eliminar(Integer id);

    // Métodos para DTOs
    List<ProductosDTO> buscarTodosDTO();
    Optional<ProductosDTO> buscarIdDTO(Integer id);
    ProductosDTO guardarDTO(ProductosDTO dto);
    ProductosDTO actualizarDTO(Integer id, ProductosDTO dto);
}

 