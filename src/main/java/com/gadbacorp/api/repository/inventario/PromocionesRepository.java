package com.gadbacorp.api.repository.inventario;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gadbacorp.api.entity.inventario.Promociones;

public interface PromocionesRepository extends JpaRepository<Promociones, Integer>{
    List<Promociones> findByNombreIgnoreCase(String nombre);
}
