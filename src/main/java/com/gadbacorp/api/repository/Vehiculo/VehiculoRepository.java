package com.gadbacorp.api.repository.Vehiculo;

import com.gadbacorp.api.entity.Vehiculo.Vehiculo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VehiculoRepository extends JpaRepository<Vehiculo, Long> {
    // Consultas personalizadas (opcional)
    Vehiculo findByPlaca(String placa);
    List<Vehiculo> findByEstado(String estado);
}