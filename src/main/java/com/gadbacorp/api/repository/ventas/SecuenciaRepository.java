package com.gadbacorp.api.repository.ventas;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.gadbacorp.api.entity.ventas.Secuencia;

import jakarta.persistence.LockModeType;

public interface SecuenciaRepository extends JpaRepository<Secuencia, String> {

    /** SELECT ... FOR UPDATE: serializa la generacion de numeros entre transacciones concurrentes. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Secuencia s WHERE s.clave = :clave")
    Optional<Secuencia> bloquear(@Param("clave") String clave);
}
