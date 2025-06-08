package com.gadbacorp.api.service.inventario;

import java.util.List;
import java.util.Optional;

import com.gadbacorp.api.entity.inventario.InventarioProducto;
import com.gadbacorp.api.entity.inventario.InventarioProductosDTO;

public interface IInventarioProductoService {

    List<InventarioProductosDTO> listarTodosDTO();

    Optional<InventarioProductosDTO> buscarPorIdDTO(Integer idinventarioproducto);

    InventarioProductosDTO crearDTO(InventarioProductosDTO dto);

    InventarioProductosDTO actualizarDTO(Integer idinventarioproducto, InventarioProductosDTO dto);

    void eliminar(Integer idinventarioproducto);

    Optional<InventarioProducto> buscarPorId(Integer idinventarioproducto);

}
