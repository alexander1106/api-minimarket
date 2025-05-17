package com.gadbacorp.api.service.ventas;

import java.util.List;
import java.util.Optional;

import com.gadbacorp.api.entity.ventas.Pagos;

public interface IPagosService {
    Pagos guardarPago(Pagos pago);
    Pagos editarPago(Pagos pago);
    void eliminarPago(Integer idPago);
    List<Pagos> listarPagos();
    Optional<Pagos> buscarPago(Integer Id);

}
