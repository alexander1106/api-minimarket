package com.gadbacorp.api.gadbacorp.service.compras;

import java.util.List;
import java.util.Optional;

import com.gadbacorp.api.gadbacorp.entity.compras.Compras;

public interface IComprasService {
  List<Compras> buscarTodos();
    // Guarda los proveedores
    void guardar(Compras compra);
    
    void modificar(Compras compra);

    Optional<Compras> buscarId(Integer id);

    void eliminar(Integer id);

}