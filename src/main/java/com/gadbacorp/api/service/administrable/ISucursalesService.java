package com.gadbacorp.api.service.administrable;

import java.util.List;
import java.util.Optional;

import com.gadbacorp.api.entity.administrable.Sucursales;
import com.gadbacorp.api.entity.dto.administrable.SucursalDTO;

public interface ISucursalesService {
    List<Sucursales> buscarTodos();
    List<SucursalDTO> buscarTodosDTO();

    void guardar(Sucursales sucursal);
    
    void modificar(Sucursales sucursal);

    Optional<Sucursales> buscarId(Integer id);

    void eliminar(Integer id);

}
