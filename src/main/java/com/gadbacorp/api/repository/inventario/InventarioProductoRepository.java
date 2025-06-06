package com.gadbacorp.api.repository.inventario;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gadbacorp.api.entity.inventario.InventarioProducto;

public interface InventarioProductoRepository extends JpaRepository<InventarioProducto, Integer>{
    
    Optional<InventarioProducto> findByProductoIdproductoAndInventarioIdinventario(
        Integer idProducto,
        Integer idInventario
    );
}
