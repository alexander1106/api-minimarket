package com.gadbacorp.api.repository.caja;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gadbacorp.api.entity.caja.ArqueoCaja;

public interface ArqueoCajaRepositoy extends JpaRepository<ArqueoCaja,Integer> {

    boolean existsByAperturaCaja_IdAperturaCaja(Integer idAperturaCaja);
}
