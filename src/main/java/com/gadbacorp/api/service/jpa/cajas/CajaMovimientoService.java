package com.gadbacorp.api.service.jpa.cajas;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gadbacorp.api.entity.caja.AperturaCaja;
import com.gadbacorp.api.entity.caja.SaldoMetodoPago;
import com.gadbacorp.api.entity.caja.TransaccionesCaja;
import com.gadbacorp.api.entity.ventas.MetodosPago;
import com.gadbacorp.api.repository.caja.AperturaCajaRepository;
import com.gadbacorp.api.repository.caja.SaldoMetodoPagoRepository;
import com.gadbacorp.api.repository.caja.TransaccionesCajaRepository;

/**
 * Unico punto donde se mueve el dinero de una caja: valida, actualiza el saldo total de la apertura,
 * el saldo en efectivo y el saldo por metodo de pago, y registra la transaccion. Todo en una transaccion
 * y con la apertura bloqueada, de modo que dos cobros simultaneos no pisen el saldo.
 */
@Service
public class CajaMovimientoService {

    public static final String INGRESO = "INGRESO";
    public static final String EGRESO = "EGRESO";

    @Autowired
    private AperturaCajaRepository aperturaCajaRepository;
    @Autowired
    private SaldoMetodoPagoRepository saldoMetodoPagoRepository;
    @Autowired
    private TransaccionesCajaRepository transaccionesCajaRepository;

    /**
     * @throws IllegalArgumentException si los datos no son validos o el saldo no alcanza
     */
    @Transactional
    public TransaccionesCaja registrar(Integer idAperturaCaja, MetodosPago metodo, String tipoMovimiento,
                                       Double monto, String observaciones) {
        if (idAperturaCaja == null) {
            throw new IllegalArgumentException("La apertura de caja es obligatoria.");
        }
        AperturaCaja apertura = aperturaCajaRepository.buscarBloqueando(idAperturaCaja)
                .orElseThrow(() -> new IllegalArgumentException("No existe la apertura de caja con id: " + idAperturaCaja));
        if (metodo == null) {
            throw new IllegalArgumentException("El metodo de pago es obligatorio.");
        }
        boolean ingreso = INGRESO.equalsIgnoreCase(tipoMovimiento);
        if (!ingreso && !EGRESO.equalsIgnoreCase(tipoMovimiento)) {
            throw new IllegalArgumentException("Tipo de movimiento inválido: " + tipoMovimiento);
        }
        if (monto == null || monto <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor a 0.");
        }
        if (!"ABIERTA".equalsIgnoreCase(apertura.getEstadoCaja())) {
            throw new IllegalArgumentException("La apertura de caja no está abierta.");
        }

        double saldoTotal = apertura.getSaldoFinal() != null ? apertura.getSaldoFinal()
                : apertura.getSaldoInicial() != null ? apertura.getSaldoInicial() : 0.0;

        SaldoMetodoPago saldoMetodo = saldoMetodoPagoRepository
                .findByAperturaCajaAndMetodoPago(apertura, metodo)
                .orElseGet(() -> {
                    SaldoMetodoPago nuevo = new SaldoMetodoPago();
                    nuevo.setAperturaCaja(apertura);
                    nuevo.setMetodoPago(metodo);
                    nuevo.setSaldo(0.0);
                    nuevo.setEstado(1);
                    return nuevo;
                });
        double saldoDelMetodo = saldoMetodo.getSaldo() != null ? saldoMetodo.getSaldo() : 0.0;

        if (!ingreso) {
            if (monto > saldoTotal) {
                throw new IllegalArgumentException("Saldo insuficiente. No se puede egresar más de lo disponible.");
            }
            if (monto > saldoDelMetodo) {
                throw new IllegalArgumentException(
                        "Saldo insuficiente en el método de pago. No se puede egresar más de lo disponible.");
            }
        }

        double signo = ingreso ? monto : -monto;
        apertura.setSaldoFinal(saldoTotal + signo);
        if ("Efectivo".equalsIgnoreCase(metodo.getNombre())) {
            double efectivo = apertura.getSaldoEfectivo() != null ? apertura.getSaldoEfectivo() : 0.0;
            apertura.setSaldoEfectivo(efectivo + signo);
        }
        saldoMetodo.setSaldo(saldoDelMetodo + signo);

        TransaccionesCaja transaccion = new TransaccionesCaja();
        transaccion.setFecha(new Date());
        transaccion.setMonto(monto);
        transaccion.setObservaciones(observaciones);
        transaccion.setTipoMovimiento(ingreso ? INGRESO : EGRESO);
        transaccion.setAperturaCaja(apertura);
        transaccion.setMetodoPago(metodo);

        aperturaCajaRepository.save(apertura);
        saldoMetodoPagoRepository.save(saldoMetodo);
        return transaccionesCajaRepository.save(transaccion);
    }
}
