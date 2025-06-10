package com.gadbacorp.api.repository.security;



import org.springframework.data.jpa.repository.JpaRepository;

import com.gadbacorp.api.entity.security.Permiso;



public interface PermisoRepository extends JpaRepository<Permiso, Long> {
    // Optional<Permiso> findByNombre(String nombre);
    // Aquí puedes agregar métodos personalizados si es necesario
    // Por ejemplo, para buscar permisos por nombre:
    // Optional<Permiso> findByNombre(String nombre);

}
