package com.gadbacorp.api.repository.ventas;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gadbacorp.api.entity.ventas.DetallesDevolucion;

public interface DetallesDevolucionesRepository extends JpaRepository<DetallesDevolucion,Integer>{

List<DetallesDevolucion> findByDevolucionesIdDevolucion(Integer id);
    
}
