package com.gadbacorp.api.repository.caja;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gadbacorp.api.entity.caja.TransferenciasEntreCajas;

public interface TransferenciaEntreCajasRepository extends  JpaRepository<TransferenciasEntreCajas, Integer>{
    
}
