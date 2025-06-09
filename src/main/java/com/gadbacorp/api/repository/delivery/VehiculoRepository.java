package com.gadbacorp.api.repository.delivery;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gadbacorp.api.entity.delivery.Vehiculo;

@Repository
public interface VehiculoRepository extends JpaRepository<Vehiculo, Long> {
    // Consultas personalizadas (opcional)
    Vehiculo findByPlaca(String placa);
    List<Vehiculo> findByEstado(String estado);
}