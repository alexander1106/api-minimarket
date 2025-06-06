package com.gadbacorp.api.repository.inventario;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gadbacorp.api.entity.inventario.Categorias;

public interface CategoriasRepository extends JpaRepository<Categorias, Integer>{
    Optional<Categorias> findByNombreIgnoreCase(String nombre);
}
