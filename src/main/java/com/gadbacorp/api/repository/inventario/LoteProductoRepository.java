package com.gadbacorp.api.repository.inventario;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.gadbacorp.api.entity.inventario.LoteProducto;

public interface LoteProductoRepository extends JpaRepository<LoteProducto, Long> {

    Optional<LoteProducto> findByIdDetalleCompra(Integer idDetalleCompra);

    /** Lotes con saldo del producto en la sucursal, el que vence antes primero; los que no vencen al final. */
    @Query("""
            select l from LoteProducto l
            where l.producto.idproducto = :idProducto and l.idSucursal = :idSucursal and l.cantidadDisponible > 0
            order by case when l.fechaVencimiento is null then 1 else 0 end, l.fechaVencimiento, l.idLote
            """)
    List<LoteProducto> disponiblesFefo(@Param("idProducto") Integer idProducto, @Param("idSucursal") Integer idSucursal);

    @Query("""
            select l from LoteProducto l
            where l.producto.idproducto = :idProducto and (:idSucursal is null or l.idSucursal = :idSucursal)
            order by case when l.fechaVencimiento is null then 1 else 0 end, l.fechaVencimiento, l.idLote
            """)
    List<LoteProducto> deProducto(@Param("idProducto") Integer idProducto, @Param("idSucursal") Integer idSucursal);

    @Query("""
            select l from LoteProducto l
            where l.cantidadDisponible > 0 and l.fechaVencimiento is not null and l.fechaVencimiento <= :limite
              and (:idSucursal is null or l.idSucursal = :idSucursal)
            order by l.fechaVencimiento, l.idLote
            """)
    List<LoteProducto> porVencer(@Param("limite") LocalDate limite, @Param("idSucursal") Integer idSucursal);
}
