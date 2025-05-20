package com.gadbacorp.api.repository.inventario;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gadbacorp.api.entity.inventario.Productos;

public interface ProductosRepository extends JpaRepository<Productos, Integer>{
    Optional<Productos> findByNombreIgnoreCase(String nombre);

}
