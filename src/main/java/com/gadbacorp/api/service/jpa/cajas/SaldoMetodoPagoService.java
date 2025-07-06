package com.gadbacorp.api.service.jpa.cajas;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gadbacorp.api.entity.caja.AperturaCaja;
import com.gadbacorp.api.entity.caja.SaldoMetodoPago;
import com.gadbacorp.api.entity.ventas.MetodosPago;
import com.gadbacorp.api.repository.caja.SaldoMetodoPagoRepository;

@Service
public class SaldoMetodoPagoService {

    @Autowired
    private SaldoMetodoPagoRepository saldoMetodoPagoRepository;
public List<SaldoMetodoPago> obtenerSaldosPorApertura(Integer idAperturaCaja) {
    return saldoMetodoPagoRepository.findByAperturaCaja_IdAperturaCaja(idAperturaCaja);
}


    public SaldoMetodoPago obtenerSaldoPorAperturaYMetodo(AperturaCaja aperturaCaja, MetodosPago metodoPago) {
        return saldoMetodoPagoRepository.findByAperturaCajaAndMetodoPago(aperturaCaja, metodoPago)
                .orElseThrow(() -> new RuntimeException("Saldo no encontrado para ese método de pago"));
    }

    public SaldoMetodoPago crearSaldo(SaldoMetodoPago saldoMetodoPago) {
        return saldoMetodoPagoRepository.save(saldoMetodoPago);
    }

    public SaldoMetodoPago actualizarSaldo(SaldoMetodoPago saldoMetodoPago) {
        return saldoMetodoPagoRepository.save(saldoMetodoPago);
    }

    public void eliminarSaldo(Integer idSaldoMetodoPago) {
        saldoMetodoPagoRepository.deleteById(idSaldoMetodoPago);
    }
    // Método para incrementar saldo
    public void incrementarSaldo(AperturaCaja aperturaCaja, MetodosPago metodoPago, Double monto) {
        SaldoMetodoPago saldo = saldoMetodoPagoRepository
                .findByAperturaCajaAndMetodoPago(aperturaCaja, metodoPago)
                .orElseThrow(() -> new RuntimeException("Saldo no encontrado"));
        saldo.setSaldo(saldo.getSaldo() + monto);
        saldoMetodoPagoRepository.save(saldo);
    }

}
