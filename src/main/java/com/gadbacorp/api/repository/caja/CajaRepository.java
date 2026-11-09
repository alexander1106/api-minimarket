package com.gadbacorp.api.repository.caja;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gadbacorp.api.entity.administrable.Sucursales;
import com.gadbacorp.api.entity.caja.Caja;

public interface CajaRepository extends JpaRepository<Caja, Integer>{

    /** Bloquea la fila de la caja mientras se abre un turno: dos aperturas a la vez no pueden ocupar la misma caja. */
    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("SELECT c FROM Caja c WHERE c.idCaja = :id")
    java.util.Optional<Caja> buscarBloqueando(@org.springframework.data.repository.query.Param("id") Integer id);


    boolean existsByNombreCajaAndSucursales_IdSucursal(String trim, Integer idSucursal);
    List<Caja> findBySucursalesAndEstadoCaja(Sucursales sucursales, String estado);

}
