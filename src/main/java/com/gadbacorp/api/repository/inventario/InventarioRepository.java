package com.gadbacorp.api.repository.inventario;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gadbacorp.api.entity.inventario.Almacenes;
import com.gadbacorp.api.entity.inventario.Inventario;
import com.gadbacorp.api.entity.inventario.Productos;

public interface InventarioRepository extends JpaRepository<Inventario, Integer> {
   Optional<Inventario> findByProductoAndAlmacen(Productos producto, Almacenes almacen);
}
 