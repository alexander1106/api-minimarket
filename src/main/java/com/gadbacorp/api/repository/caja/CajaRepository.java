package com.gadbacorp.api.repository.caja;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gadbacorp.api.entity.administrable.Sucursales;
import com.gadbacorp.api.entity.caja.Caja;

public interface CajaRepository extends JpaRepository<Caja, Integer>{

    boolean existsByNombreCajaAndSucursales_IdSucursal(String trim, Integer idSucursal);
    List<Caja> findBySucursalesAndEstadoCaja(Sucursales sucursales, String estado);

}
