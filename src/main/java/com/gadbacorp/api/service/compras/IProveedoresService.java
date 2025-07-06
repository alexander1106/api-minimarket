package com.gadbacorp.api.service.compras;

import java.util.List;
import java.util.Optional;

import com.gadbacorp.api.entity.compras.Proveedores;

public interface IProveedoresService {
    List<Proveedores> buscarTodos();
    Proveedores guardar(Proveedores proveedor);  // Cambiado de void a Proveedores
    Proveedores modificar(Proveedores proveedor); // Cambiado de void a Proveedores
    Optional<Proveedores> buscarId(Integer id);
    void eliminar(Integer id);
    List<Proveedores> buscarPorEmpresa(Integer idEmpresa);
}