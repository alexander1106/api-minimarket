package com.gadbacorp.api.repository.inventario;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gadbacorp.api.entity.inventario.Inventario;

public interface InventarioRepository extends JpaRepository<Inventario, Integer> {
   Optional<Inventario> findByAlmacenIdalmacenAndNombreIgnoreCase(
        Integer idAlmacen,
        String nombre
    );
}
 