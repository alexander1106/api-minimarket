package com.gadbacorp.api.service.ventas;

import java.util.List;
import java.util.Optional;

import com.gadbacorp.api.entity.ventas.DetallesVentas;

public interface IDetallesVentasService {
  DetallesVentas guardarDetallesVentas(DetallesVentas detallesVentas);
    DetallesVentas editarDetallesVentas(DetallesVentas detallesVentas);
    void eliminarDetallesVentas(Integer idDetallesVenta);
    void eliminarPorVentas(Integer idVenta);
    List<DetallesVentas> listDetallesVentas();
    Optional<DetallesVentas> buscarDetallesVentas(Integer IdVenta);

}
