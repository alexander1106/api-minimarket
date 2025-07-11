package com.gadbacorp.api.repository.caja;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gadbacorp.api.entity.caja.TransaccionesCaja;

public interface  TransaccionesCajaRepository extends  JpaRepository<TransaccionesCaja, Integer> {

    List<TransaccionesCaja> findByAperturaCajaIdAperturaCaja(Integer idAperturaCaja);
    
}
