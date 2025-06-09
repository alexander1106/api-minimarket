package com.gadbacorp.api.gadbacorp.repository.inventario;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gadbacorp.api.gadbacorp.entity.inventario.AlmacenProducto;
import com.gadbacorp.api.gadbacorp.entity.inventario.Almacenes;
import com.gadbacorp.api.gadbacorp.entity.inventario.Productos;

public interface AlmacenProductoRepository extends JpaRepository<AlmacenProducto, Integer>{
    
    Optional<AlmacenProducto> findByProductoAndAlmacen(Productos producto, Almacenes almacen);
    // en AlmacenProductoRepository
Optional<AlmacenProducto> findByProducto_IdproductoAndAlmacen_Idalmacen(Integer idProd, Integer idAlm);


}
