package com.gadbacorp.api.repository.seguridad;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gadbacorp.api.entity.seguridad.Registros;

@Repository
public interface RegistrosRepository extends JpaRepository<Registros, Integer>{

}
