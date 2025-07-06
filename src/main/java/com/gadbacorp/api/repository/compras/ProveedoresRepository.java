package com.gadbacorp.api.repository.compras;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gadbacorp.api.entity.compras.Proveedores;

public interface ProveedoresRepository extends JpaRepository<Proveedores, Integer>{

     List<Proveedores> findByEmpresaIdempresa(Integer idEmpresa);
}
