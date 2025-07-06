package com.gadbacorp.api.repository.ventas;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gadbacorp.api.entity.administrable.Sucursales;
import com.gadbacorp.api.entity.caja.TransferenciasEntreCajas;
import com.gadbacorp.api.entity.ventas.MetodosPago;

public interface MetodosPagoRepository extends JpaRepository<MetodosPago, Integer> {

    Optional<MetodosPago> findByNombre(String nombre);
    boolean existsByNombreAndSucursal_IdSucursal(String nombre, Integer idSucursal);

    Optional<TransferenciasEntreCajas> findByNombreIgnoreCase(String string);
boolean existsByNombreAndSucursal(String nombre, Sucursales sucursal);

List<MetodosPago> findBySucursalIdSucursal(Integer idSucursal);
boolean existsByNombreAndSucursalIdSucursal(String nombre, Sucursales idSucursal);
List<MetodosPago> findByNombreIgnoreCaseAndSucursal_IdSucursal(String nombre, Integer idSucursal);

}
