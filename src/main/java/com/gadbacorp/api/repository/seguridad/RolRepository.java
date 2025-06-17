package com.gadbacorp.api.repository.seguridad;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gadbacorp.api.entity.seguridad.Rol;

@Repository
public interface RolRepository extends JpaRepository<Rol, Integer> {
     Optional<Rol> findByNombre(String nombre);
   
}