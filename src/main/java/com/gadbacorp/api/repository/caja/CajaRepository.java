package com.gadbacorp.api.repository.caja;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gadbacorp.api.entity.caja.Caja;

public interface CajaRepository extends JpaRepository<Caja, Integer>{
    
}
