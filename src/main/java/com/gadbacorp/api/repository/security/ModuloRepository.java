package com.gadbacorp.api.repository.security;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gadbacorp.api.entity.security.Modulo;


public interface ModuloRepository extends JpaRepository<Modulo, Long> {

    // Aquí puedes agregar métodos personalizados si es necesario
    // Por ejemplo, para buscar módulos por nombre:
    // Optional<Modulo> findByNombre(String nombre);

}
