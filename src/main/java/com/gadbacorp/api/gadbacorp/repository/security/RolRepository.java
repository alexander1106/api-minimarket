package com.gadbacorp.api.gadbacorp.repository.security;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gadbacorp.api.gadbacorp.entity.security.Rol;


public interface RolRepository extends JpaRepository<Rol, Long> {
     Optional<Rol> findByNombre(String nombre);
    // Aquí puedes agregar métodos personalizados si es necesario
    // Por ejemplo, para buscar roles por nombre:
    // Optional<Rol> findByNombre(String nombre);

}
