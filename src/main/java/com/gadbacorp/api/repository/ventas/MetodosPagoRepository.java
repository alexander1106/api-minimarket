package com.gadbacorp.api.repository.ventas;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gadbacorp.api.entity.caja.TransferenciasEntreCajas;
import com.gadbacorp.api.entity.ventas.MetodosPago;

public interface MetodosPagoRepository extends JpaRepository<MetodosPago, Integer> {

    Optional<MetodosPago> findByNombre(String nombre);

    Optional<TransferenciasEntreCajas> findByNombreIgnoreCase(String string);

}
