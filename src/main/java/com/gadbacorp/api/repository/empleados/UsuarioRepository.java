package com.gadbacorp.api.repository.empleados;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gadbacorp.api.entity.empleados.Usuarios;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuarios, Integer> {
   Optional<Usuarios> findByUsername(String username);
    Optional<Usuarios> findByEmail(String email);
        // Aquí puedes agregar métodos personalizados si es necesario
}
