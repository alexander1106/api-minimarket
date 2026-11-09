package com.gadbacorp.api.repository.administrable;

import org.springframework.data.jpa.repository.JpaRepository;
import com.gadbacorp.api.entity.administrable.Empresas;

public interface EmpresasRepository extends JpaRepository<Empresas, Integer>{

    boolean existsByRuc(String ruc);

    long countByPlan_IdPlan(Integer idPlan);
}
