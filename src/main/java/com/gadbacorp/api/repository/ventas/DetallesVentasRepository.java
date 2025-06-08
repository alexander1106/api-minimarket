package com.gadbacorp.api.repository.ventas;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gadbacorp.api.entity.ventas.DetallesVentas;
public interface  DetallesVentasRepository extends  JpaRepository<DetallesVentas, Integer>{

    List<DetallesVentas> findByVentas_IdVenta(Integer idVenta);
    
}
