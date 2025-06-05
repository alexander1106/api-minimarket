package com.gadbacorp.api.service.administrable;

import com.gadbacorp.api.entity.dto.administrable.SucursalDTO;
import com.gadbacorp.api.entity.administrable.Sucursales;

import java.util.List;
import java.util.Optional;

public interface ISucursalesService {
    List<SucursalDTO> buscarTodosDTO();
    List<Sucursales> buscarTodos();
    void guardar(Sucursales sucursal);
    void modificar(Sucursales sucursal);
    Optional<Sucursales> buscarId(Integer id);
    Optional<SucursalDTO> buscarIdDTO(Integer id); // Añade este método
    void eliminar(Integer id);
}