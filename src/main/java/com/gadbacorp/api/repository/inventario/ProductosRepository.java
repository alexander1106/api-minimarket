package com.gadbacorp.api.repository.inventario;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gadbacorp.api.entity.inventario.Productos;

public interface ProductosRepository extends JpaRepository<Productos, Integer>{
    Optional<Productos> findByNombreIgnoreCase(String nombre);
    List<Productos> findByCategoria_Idcategoria(Integer idcategoria);
    boolean existsByUnidadMedida_Idunidadmedida(Integer idunidadmedida);
    boolean existsByTipoProducto_Idtipoproducto(Integer idtipoproducto);
    boolean existsByCategoria_Idcategoria(Integer idcategoria);
    List<Productos> findByStockMinimoNotNull();
    /** El codigo (SKU) y el de barras son unicos dentro de cada empresa, no en toda la plataforma. */
    Optional<Productos> findByCodigoIgnoreCaseAndEmpresa_Idempresa(String codigo, Integer idEmpresa);
    Optional<Productos> findByCodigoBarrasAndEmpresa_Idempresa(String codigoBarras, Integer idEmpresa);
    Optional<Productos> findByCodigoIgnoreCaseAndEmpresaIsNull(String codigo);
    Optional<Productos> findByCodigoBarrasAndEmpresaIsNull(String codigoBarras);
    
}
