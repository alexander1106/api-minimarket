package com.gadbacorp.api.repository.ventas;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; // <-- CORRECTO
import org.springframework.data.repository.query.Param;

import com.gadbacorp.api.entity.ventas.Ventas;

public interface VentasRepository extends JpaRepository<Ventas, Integer> {
    List<Ventas> findByClienteIdCliente(Integer clienteId);
@Query("SELECT v.nro_comrprobante FROM Ventas v WHERE LOWER(v.tipo_comprobante) = LOWER(:tipoComprobante) ORDER BY v.idVenta DESC")
List<String> findUltimoComprobantePorTipo(@Param("tipoComprobante") String tipoComprobante);

List<Ventas> findByCliente_Sucursal_IdSucursal(Integer idSucursal);


}
