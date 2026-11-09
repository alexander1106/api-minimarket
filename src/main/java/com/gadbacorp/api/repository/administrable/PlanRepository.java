package com.gadbacorp.api.repository.administrable;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gadbacorp.api.entity.administrable.Plan;

public interface PlanRepository extends JpaRepository<Plan, Integer> {

    Optional<Plan> findByNombre(String nombre);

    Optional<Plan> findByNombreIgnoreCase(String nombre);
}
