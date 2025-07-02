package com.gadbacorp.api.repository.empleados;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gadbacorp.api.entity.empleados.Usuarios;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuarios, Integer> {
    public Usuarios findByUsername(String username);
    Optional<Usuarios> findByEmail(String email);
        // Aquí puedes agregar métodos personalizados si es necesario
    public List<Usuarios> findBySucursal_IdSucursal(Integer idSucursal);
}
