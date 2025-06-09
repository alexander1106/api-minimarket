package com.gadbacorp.api.gadbacorp.service.compras;

import java.util.List;
import java.util.Optional;

import com.gadbacorp.api.gadbacorp.entity.compras.Proveedores;

public interface IProveedoresService {
      // Listar todos los proveedores de la tabla
    List<Proveedores> buscarTodos();
    // Guarda los proveedores
    void guardar(Proveedores proveedor);
    
    void modificar(Proveedores proveedor);

    Optional<Proveedores> buscarId(Integer id);

    void eliminar(Integer id);

}



