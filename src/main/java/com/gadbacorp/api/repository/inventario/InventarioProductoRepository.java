package com.gadbacorp.api.repository.inventario;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gadbacorp.api.entity.inventario.Almacenes;
import com.gadbacorp.api.entity.inventario.InventarioProducto;
import com.gadbacorp.api.entity.inventario.Productos;

public interface InventarioProductoRepository extends JpaRepository<InventarioProducto, Integer>{
    
    Optional<InventarioProducto> findByProductoIdproductoAndInventarioIdinventario(
        Integer idProducto,
        Integer idInventario
    );
        Optional<InventarioProducto> findByProductoAndInventarioAlmacen(Productos producto, Almacenes almacen);
}
