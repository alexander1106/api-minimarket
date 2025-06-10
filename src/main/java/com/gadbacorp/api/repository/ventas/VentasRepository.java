package com.gadbacorp.api.repository.ventas;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gadbacorp.api.entity.ventas.Ventas;


public interface VentasRepository extends JpaRepository<Ventas, Integer> {
    List<Ventas> findByClienteIdCliente(Integer clienteId);
    

}