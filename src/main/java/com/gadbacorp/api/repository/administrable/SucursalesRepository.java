package com.gadbacorp.api.repository.administrable;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.gadbacorp.api.entity.administrable.Sucursales;

public interface SucursalesRepository extends JpaRepository<Sucursales, Integer> {

    @Query("SELECT DISTINCT s FROM Sucursales s LEFT JOIN FETCH s.empresa LEFT JOIN FETCH s.almacenes")
    List<Sucursales> findAllWithEmpresaAndAlmacenes();

    List<Sucursales> findByEmpresaIdempresa(Integer idEmpresa);
    List<Sucursales> findAllByEmpresa_Idempresa(Integer idEmpresa);

    long countByEmpresaIdempresa(Integer idEmpresa);

    /** [idEmpresa, cantidad de sucursales] por empresa. */
    @Query("SELECT s.empresa.idempresa, COUNT(s) FROM Sucursales s GROUP BY s.empresa.idempresa")
    List<Object[]> contarPorEmpresa();

}
