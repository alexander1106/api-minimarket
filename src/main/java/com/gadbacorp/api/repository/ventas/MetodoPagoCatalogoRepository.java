package com.gadbacorp.api.repository.ventas;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gadbacorp.api.entity.ventas.MetodoPagoCatalogo;

public interface MetodoPagoCatalogoRepository extends JpaRepository<MetodoPagoCatalogo, Integer> {

    Optional<MetodoPagoCatalogo> findByNombreIgnoreCase(String nombre);
}
