package com.gadbacorp.api.repository.caja;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gadbacorp.api.entity.caja.AperturaCaja;

public interface AperturaCajaRepository extends JpaRepository<AperturaCaja, Integer> {

Optional<AperturaCaja> findByCaja_IdCajaAndCaja_EstadoCaja(Integer idCaja, String estadoCaja);

List<AperturaCaja> findByCajaSucursalesIdSucursal(Integer idSucursal);
    
}
