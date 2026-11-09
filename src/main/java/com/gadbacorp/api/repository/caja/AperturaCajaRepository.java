package com.gadbacorp.api.repository.caja;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gadbacorp.api.entity.caja.AperturaCaja;

public interface AperturaCajaRepository extends JpaRepository<AperturaCaja, Integer> {

Optional<AperturaCaja> findByCaja_IdCajaAndCaja_EstadoCaja(Integer idCaja, String estadoCaja);

List<AperturaCaja> findByCaja_Sucursales_IdSucursalAndEstadoCaja(Integer idSucursal, String estadoCaja);

List<AperturaCaja> findByCajaSucursalesIdSucursal(Integer idSucursal);

List<AperturaCaja> findByUsuarios_IdUsuarioAndEstadoCaja(Integer idUsuario, String estadoCaja);

List<AperturaCaja> findByEstadoCaja(String string);

List<AperturaCaja> findByCaja_IdCajaAndEstadoCaja(Integer idCaja, String estadoCaja);

boolean existsByCaja_IdCajaAndEstadoCaja(Integer idCaja, String estadoCaja);


    

    /** Bloquea la fila de la apertura mientras se actualizan los saldos (evita carreras entre cobros). */
    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("SELECT a FROM AperturaCaja a WHERE a.idAperturaCaja = :id")
    Optional<AperturaCaja> buscarBloqueando(@org.springframework.data.repository.query.Param("id") Integer id);
}
