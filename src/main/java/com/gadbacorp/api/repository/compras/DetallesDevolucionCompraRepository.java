package com.gadbacorp.api.repository.compras;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.gadbacorp.api.entity.compras.DetallesDevolucionCompra;

public interface DetallesDevolucionCompraRepository extends JpaRepository<DetallesDevolucionCompra, Integer> {
    // Puedes agregar métodos personalizados aquí si es necesario
    // En DetallesDevolucionCompraRepository
List<DetallesDevolucionCompra> findByDevolucionCompraIdDevolucionCompra(Integer idDevolucion);
}