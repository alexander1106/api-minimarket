package com.gadbacorp.api.repository.inventario;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gadbacorp.api.entity.inventario.Almacenes;

public interface AlmacenesRepository extends JpaRepository<Almacenes, Integer>{
    Optional<Almacenes> findByNombreIgnoreCase(String nombre);
Optional<Almacenes> findByDireccionIgnoreCase(String direccion);
}
