package com.gadbacorp.api.repository.inventario;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gadbacorp.api.entity.inventario.ProductoPresentacion;

public interface ProductoPresentacionRepository extends JpaRepository<ProductoPresentacion, Integer> {

    List<ProductoPresentacion> findByProductoIdproductoOrderByFactorAsc(Integer idProducto);

    List<ProductoPresentacion> findByCodigoBarras(String codigoBarras);

    Optional<ProductoPresentacion> findByIdPresentacionAndProductoIdproducto(Integer idPresentacion, Integer idProducto);

    boolean existsByProductoIdproductoAndNombreIgnoreCase(Integer idProducto, String nombre);

    boolean existsByProductoIdproductoAndNombreIgnoreCaseAndIdPresentacionNot(Integer idProducto, String nombre, Integer idPresentacion);
}
