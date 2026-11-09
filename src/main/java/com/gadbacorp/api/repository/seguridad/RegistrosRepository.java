package com.gadbacorp.api.repository.seguridad;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.gadbacorp.api.entity.seguridad.Registros;

@Repository
public interface RegistrosRepository extends JpaRepository<Registros, Integer>{

    @Query("SELECT r FROM Registros r WHERE r.access_token = :token")
    Optional<Registros> buscarPorAccessToken(@Param("token") String token);
}
