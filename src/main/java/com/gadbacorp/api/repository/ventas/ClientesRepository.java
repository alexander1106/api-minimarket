package com.gadbacorp.api.repository.ventas;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gadbacorp.api.entity.ventas.Clientes;

public interface  ClientesRepository extends  JpaRepository<Clientes, Integer>{
    List<Clientes> findByDocumento(String documento);

    List<Clientes> findBySucursal_IdSucursal(Integer idSucursal);
}
