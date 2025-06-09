package com.gadbacorp.api.gadbacorp.service.inventario;

import java.util.List;
import java.util.Optional;

import com.gadbacorp.api.gadbacorp.entity.inventario.Inventario;

public interface IInventarioService {
   
     List<Inventario> buscarTodos();

    Inventario guardar(Inventario inventario);

    Inventario modificar(Inventario inventario);

    Optional<Inventario> buscarId(Integer id);

    void eliminar(Integer id);

    Inventario sincronizarStock(Integer idproducto, Integer idalmacen);

    Inventario actualizarStock(Integer idinventario, Integer nuevoStock, Integer idproducto, Integer idalmacen);
}
