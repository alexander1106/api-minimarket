package com.gadbacorp.api.repository.inventario;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gadbacorp.api.entity.inventario.Productos;

public interface ProductosRepository extends JpaRepository<Productos, Integer>{

}
