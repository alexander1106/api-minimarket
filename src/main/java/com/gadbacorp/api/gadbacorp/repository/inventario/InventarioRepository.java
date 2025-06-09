package com.gadbacorp.api.gadbacorp.repository.inventario;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gadbacorp.api.gadbacorp.entity.inventario.Almacenes;
import com.gadbacorp.api.gadbacorp.entity.inventario.Inventario;
import com.gadbacorp.api.gadbacorp.entity.inventario.Productos;

public interface InventarioRepository extends JpaRepository<Inventario, Integer> {
   Optional<Inventario> findByProductoAndAlmacen(Productos producto, Almacenes almacen);
}
 