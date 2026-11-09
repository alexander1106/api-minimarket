package com.gadbacorp.api.repository.seguridad;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gadbacorp.api.entity.seguridad.RolModulo;

@Repository
public interface RolModuloRepository extends JpaRepository<RolModulo, Integer> {

    @org.springframework.data.jpa.repository.Query(
            "SELECT m.nombre FROM RolModulo rm JOIN rm.modulo m JOIN rm.rol r WHERE UPPER(r.nombre) = UPPER(:rol)")
    java.util.List<String> nombresDeModulosPorRol(@org.springframework.data.repository.query.Param("rol") String rol);

    
}
