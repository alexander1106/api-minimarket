package com.gadbacorp.api.repository.security;


import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.gadbacorp.api.entity.security.Permiso;
import com.gadbacorp.api.entity.security.Rol;


public interface RolRepository extends JpaRepository<Rol, Long> {
     Optional<Rol> findByNombre(String nombre);
    @Query("SELECT r.permisos FROM Rol r WHERE r.nombre = :nombre")
    Set<Permiso> findPermisosByRolNombre(String nombre);
    // Aquí puedes agregar métodos personalizados si es necesario
    // Por ejemplo, para buscar roles por nombre:
    // Optional<Rol> findByNombre(String nombre);

}
