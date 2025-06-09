package com.gadbacorp.api.gadbacorp.repository.ventas;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gadbacorp.api.gadbacorp.entity.ventas.DetallesVentas;
public interface  DetallesVentasRepository extends  JpaRepository<DetallesVentas, Integer>{
    
}
