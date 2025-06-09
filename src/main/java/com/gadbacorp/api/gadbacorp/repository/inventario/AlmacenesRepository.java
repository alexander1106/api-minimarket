package com.gadbacorp.api.gadbacorp.repository.inventario;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gadbacorp.api.gadbacorp.entity.inventario.Almacenes;

public interface AlmacenesRepository extends JpaRepository<Almacenes, Integer>{
    Optional<Almacenes> findByNombreIgnoreCase(String nombre);
}
