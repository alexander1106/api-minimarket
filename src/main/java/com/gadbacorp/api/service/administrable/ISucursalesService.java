package com.gadbacorp.api.service.administrable;

import java.util.List;
import java.util.Optional;

import com.gadbacorp.api.entity.administrable.Sucursales;

public interface ISucursalesService {
    List<Sucursales> buscarTodos();
    Sucursales guardar(Sucursales sucursal);
    Sucursales modificar(Sucursales sucursal);
    Optional<Sucursales> buscarId(Integer id);
    void eliminar(Integer id);
        List<Sucursales> buscarPorEmpresa(Integer idEmpresa);

}