package com.gadbacorp.api.repository.inventario;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.gadbacorp.api.entity.inventario.MovimientoStock;

public interface MovimientoStockRepository extends JpaRepository<MovimientoStock, Long> {

    @Query("""
            select m from MovimientoStock m
            where m.producto.idproducto = :idProducto
              and (:idSucursal is null or m.idSucursal = :idSucursal)
              and (:desde is null or m.fecha >= :desde)
              and (:hasta is null or m.fecha <= :hasta)
            order by m.fecha desc, m.idMovimiento desc
            """)
    List<MovimientoStock> consultar(@Param("idProducto") Integer idProducto, @Param("idSucursal") Integer idSucursal,
                                    @Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta);
}
