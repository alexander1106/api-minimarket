package com.gadbacorp.api.repository.inventario;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gadbacorp.api.entity.inventario.AlmacenProducto;
import com.gadbacorp.api.entity.inventario.Almacenes;
import com.gadbacorp.api.entity.inventario.Productos;

public interface AlmacenProductoRepository extends JpaRepository<AlmacenProducto, Integer>{
    /**
     * Busca el registro intermedio de producto–almacén
     * para poder actualizar su stock.
     */
    Optional<AlmacenProducto> findByProductoAndAlmacen(Productos producto, Almacenes almacen);
}
