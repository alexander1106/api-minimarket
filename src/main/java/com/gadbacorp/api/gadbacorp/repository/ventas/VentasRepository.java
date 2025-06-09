package com.gadbacorp.api.gadbacorp.repository.ventas;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gadbacorp.api.gadbacorp.entity.ventas.Ventas;


public interface VentasRepository extends JpaRepository<Ventas, Integer> {
    List<Ventas> findByClienteIdCliente(Integer clienteId);
}