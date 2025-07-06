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
    
}
