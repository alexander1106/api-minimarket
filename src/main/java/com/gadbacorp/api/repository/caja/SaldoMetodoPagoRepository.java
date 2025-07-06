package com.gadbacorp.api.repository.caja;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gadbacorp.api.entity.caja.AperturaCaja;
import com.gadbacorp.api.entity.caja.SaldoMetodoPago;
import com.gadbacorp.api.entity.ventas.MetodosPago;
@Repository
public interface SaldoMetodoPagoRepository extends JpaRepository<SaldoMetodoPago, Integer>{
    Optional<SaldoMetodoPago> findByAperturaCajaAndMetodoPago(AperturaCaja aperturaCaja, MetodosPago metodoPago);
    List<SaldoMetodoPago> findByAperturaCaja(AperturaCaja aperturaCaja);
    List<SaldoMetodoPago> findByAperturaCaja_IdAperturaCaja(Integer idAperturaCaja);
}
