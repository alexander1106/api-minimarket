package com.gadbacorp.api.repository.compras;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.gadbacorp.api.entity.compras.Compras;

public interface ComprasRepository extends JpaRepository<Compras, Integer> {
    // Puedes agregar métodos personalizados aquí si es necesari// En ComprasRepository
List<Compras> findByProveedorIdProveedor(Integer idProveedor);
}