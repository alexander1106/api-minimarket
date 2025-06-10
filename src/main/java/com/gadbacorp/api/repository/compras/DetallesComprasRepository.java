package com.gadbacorp.api.repository.compras;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gadbacorp.api.entity.compras.DetallesCompras;

public interface DetallesComprasRepository extends JpaRepository<DetallesCompras, Integer>{
     List<DetallesCompras> findByCompraIdCompra(Integer idCompra);

}
