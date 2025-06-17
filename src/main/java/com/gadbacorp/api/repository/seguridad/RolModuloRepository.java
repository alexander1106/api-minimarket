package com.gadbacorp.api.repository.seguridad;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gadbacorp.api.entity.seguridad.RolModulo;

@Repository
public interface RolModuloRepository extends JpaRepository<RolModulo, Integer> {
    
}
