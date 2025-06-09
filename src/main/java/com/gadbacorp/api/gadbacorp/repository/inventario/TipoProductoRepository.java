package com.gadbacorp.api.gadbacorp.repository.inventario;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gadbacorp.api.gadbacorp.entity.inventario.TipoProducto;

public interface TipoProductoRepository extends JpaRepository<TipoProducto, Integer>{
    Optional<TipoProducto> findByNombreIgnoreCase(String nombre);
}
 