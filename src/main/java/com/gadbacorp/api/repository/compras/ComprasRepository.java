package com.gadbacorp.api.repository.compras;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.gadbacorp.api.entity.compras.Compras;

public interface ComprasRepository extends JpaRepository<Compras, Integer> {
List<Compras> findByProveedorIdProveedor(Integer idProveedor);

    /** [idProveedor, cantidad de compras, monto total, fecha de la ultima compra] por proveedor. */
    @org.springframework.data.jpa.repository.Query(
            "SELECT c.proveedor.idProveedor, COUNT(c), COALESCE(SUM(c.total), 0), MAX(c.fechaCompra) "
            + "FROM Compras c WHERE c.proveedor IS NOT NULL GROUP BY c.proveedor.idProveedor")
    List<Object[]> resumenPorProveedor();
}
