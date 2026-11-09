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
    public List<Usuarios> findBySucursal_IdSucursal(Integer idSucursal);
    Optional<Usuarios> findOptionalByUsername(String username);

    long countBySucursal_Empresa_Idempresa(Integer idEmpresa);

    long countByRolId(Integer idRol);

    /** [idRol, cantidad de usuarios] por rol. */
    @org.springframework.data.jpa.repository.Query(
            "SELECT u.rol.id, COUNT(u) FROM Usuarios u WHERE u.rol IS NOT NULL GROUP BY u.rol.id")
    List<Object[]> contarPorRol();

    List<Usuarios> findBySucursal_Empresa_Idempresa(Integer idEmpresa);

    /** [idEmpresa, cantidad de usuarios] por empresa (el SUPERADMIN no tiene sucursal y no cuenta). */
    @org.springframework.data.jpa.repository.Query(
            "SELECT u.sucursal.empresa.idempresa, COUNT(u) FROM Usuarios u WHERE u.sucursal IS NOT NULL GROUP BY u.sucursal.empresa.idempresa")
    List<Object[]> contarPorEmpresa();
}
