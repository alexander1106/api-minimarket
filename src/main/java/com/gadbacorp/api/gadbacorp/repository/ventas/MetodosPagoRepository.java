package com.gadbacorp.api.gadbacorp.repository.ventas;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gadbacorp.api.gadbacorp.entity.ventas.MetodosPago;

public interface MetodosPagoRepository extends JpaRepository<MetodosPago, Integer> {

    Optional<MetodosPago> findByNombre(String nombre);

}
