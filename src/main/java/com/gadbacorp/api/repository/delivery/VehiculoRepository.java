package com.gadbacorp.api.repository.delivery;


import org.springframework.data.jpa.repository.JpaRepository;
import com.gadbacorp.api.entity.delivery.Vehiculo;

public interface VehiculoRepository extends JpaRepository<Vehiculo, Integer> {
    
}