package com.gadbacorp.api.repository.inventario;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gadbacorp.api.entity.inventario.InventarioProducto;

public interface InventarioProductoRepository extends JpaRepository<InventarioProducto, Integer>{
    
    Optional<InventarioProducto> findByProductoIdproductoAndInventarioIdinventario(
        Integer idProducto,
        Integer idInventario
    );
Optional<InventarioProducto> findFirstByProducto_Idproducto(Integer idproducto);

List<InventarioProducto> findAllByProducto_Idproducto(Integer idproducto);
boolean existsByProducto_Idproducto(Integer idProducto);

boolean existsByInventario_Idinventario(Integer idinventario);
boolean existsByInventarioIdinventario(Integer idinventario);
List<InventarioProducto> findByInventario_Idinventario(Integer idinventario);

}