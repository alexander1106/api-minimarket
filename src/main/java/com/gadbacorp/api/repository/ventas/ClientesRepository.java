package com.gadbacorp.api.repository.ventas;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gadbacorp.api.entity.ventas.Clientes;

public interface  ClientesRepository extends  JpaRepository<Clientes, Integer>{

}
