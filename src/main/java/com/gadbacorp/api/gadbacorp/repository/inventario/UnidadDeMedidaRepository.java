package com.gadbacorp.api.gadbacorp.repository.inventario;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gadbacorp.api.gadbacorp.entity.inventario.UnidadDeMedida;

public interface UnidadDeMedidaRepository extends JpaRepository<UnidadDeMedida, Integer>{
    Optional<UnidadDeMedida> findByNombreIgnoreCase(String nombre);
}
