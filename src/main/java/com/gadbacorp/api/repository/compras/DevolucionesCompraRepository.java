package com.gadbacorp.api.repository.compras;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gadbacorp.api.entity.compras.DevolucionesCompra;

@Repository
public interface DevolucionesCompraRepository extends JpaRepository<DevolucionesCompra, Integer> {
}