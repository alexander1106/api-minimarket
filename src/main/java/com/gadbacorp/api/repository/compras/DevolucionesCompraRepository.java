package com.gadbacorp.api.repository.compras;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.gadbacorp.api.entity.compras.DevolucionesCompra;

public interface DevolucionesCompraRepository extends JpaRepository<DevolucionesCompra, Integer> {
List<DevolucionesCompra> findByCompraIdCompra(Integer idCompra);
}