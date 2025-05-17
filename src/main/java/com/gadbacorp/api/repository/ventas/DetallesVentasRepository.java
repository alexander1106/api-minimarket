package com.gadbacorp.api.repository.ventas;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gadbacorp.api.entity.ventas.DetallesVentas;
public interface  DetallesVentasRepository extends  JpaRepository<DetallesVentas, Integer>{
    
}
