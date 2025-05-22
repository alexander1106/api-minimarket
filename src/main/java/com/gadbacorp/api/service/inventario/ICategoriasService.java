package com.gadbacorp.api.service.inventario;

import java.util.List;
import java.util.Optional;

import com.gadbacorp.api.entity.inventario.Categorias;

public interface ICategoriasService {

    //listar todos los clientes de la tabla
    List<Categorias> buscarTodos();
    //guarda los clientes
    Categorias guardar(Categorias categoria);

    Categorias modificar(Categorias categoria);

    Optional<Categorias> buscarId(Integer id);

    void eliminar(Integer id);
}   