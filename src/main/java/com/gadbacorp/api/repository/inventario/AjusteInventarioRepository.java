package com.gadbacorp.api.repository.inventario;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gadbacorp.api.entity.inventario.AjusteInventario;

public interface AjusteInventarioRepository extends JpaRepository<AjusteInventario, Integer>{
List<AjusteInventario> findByInventarioProducto_Inventario_Almacen_Sucursal_IdSucursal(Integer idSucursal);
}
