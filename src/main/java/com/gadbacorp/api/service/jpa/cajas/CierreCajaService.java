package com.gadbacorp.api.service.jpa.cajas;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gadbacorp.api.entity.caja.AperturaCaja;
import com.gadbacorp.api.entity.caja.ArqueoCaja;
import com.gadbacorp.api.entity.caja.ArqueoCajaDTO;
import com.gadbacorp.api.entity.caja.Caja;
import com.gadbacorp.api.repository.caja.AperturaCajaRepository;
import com.gadbacorp.api.repository.caja.ArqueoCajaRepositoy;
import com.gadbacorp.api.repository.caja.CajaRepository;

/** Arqueo y cierre de caja con las reglas del negocio validadas en el servidor. */
@Service
public class CierreCajaService {

    private static final double TOLERANCIA = 0.009;

    @Autowired
    private AperturaCajaRepository aperturaCajaRepository;
    @Autowired
    private ArqueoCajaRepositoy arqueoCajaRepository;
    @Autowired
    private CajaRepository cajaRepository;

    /** Si es true, no se puede cerrar una apertura que no tenga arqueo registrado. */
    @Value("${app.caja.exigir-arqueo:true}")
    private boolean exigirArqueo;

    /**
     * Registra el arqueo. Si se informa montoContado debe coincidir con el efectivo que calcula el sistema;
     * el saldo del sistema siempre se toma del servidor, no del cliente.
     */
    @Transactional
    public ArqueoCaja registrarArqueo(ArqueoCajaDTO dto) {
        if (dto.getId_apertura_caja() == null) {
            throw new IllegalArgumentException("La apertura de caja es obligatoria.");
        }
        AperturaCaja apertura = aperturaCajaRepository.buscarBloqueando(dto.getId_apertura_caja())
                .orElseThrow(() -> new IllegalArgumentException(
                        "No se encontró la apertura de caja con ID: " + dto.getId_apertura_caja()));

        double efectivo = apertura.getSaldoEfectivo() != null ? apertura.getSaldoEfectivo() : 0.0;
        ArqueoCaja arqueo = new ArqueoCaja();
        arqueo.setFechaArqueo(dto.getFechaArqueo() != null ? dto.getFechaArqueo() : new Date());
        arqueo.setSaldoSistema(efectivo);
        arqueo.setObservaciones(dto.getObservaciones());
        arqueo.setAperturaCaja(apertura);

        if (dto.getMontoContado() != null) {
            double diferencia = dto.getMontoContado() - efectivo;
            if (Math.abs(diferencia) > TOLERANCIA) {
                throw new IllegalArgumentException(String.format(
                        "El monto contado (%.2f) no coincide con el efectivo del sistema (%.2f). Diferencia: %.2f",
                        dto.getMontoContado(), efectivo, diferencia));
            }
            arqueo.setSaldoReal(dto.getMontoContado());
            arqueo.setDiferencia(diferencia);
        }
        return arqueoCajaRepository.save(arqueo);
    }

    @Transactional
    public void cerrar(Integer idApertura) {
        AperturaCaja apertura = aperturaCajaRepository.buscarBloqueando(idApertura)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No se encontró la apertura de caja con el ID proporcionado."));
        if (!"ABIERTA".equalsIgnoreCase(apertura.getEstadoCaja())) {
            throw new IllegalArgumentException("La apertura de caja no está abierta.");
        }
        Caja caja = apertura.getCaja();
        if (caja == null || !"OCUPADA".equalsIgnoreCase(caja.getEstadoCaja())) {
            throw new IllegalArgumentException("La caja no está en estado OCUPADA, no se puede cerrar.");
        }
        if (exigirArqueo && !arqueoCajaRepository.existsByAperturaCaja_IdAperturaCaja(idApertura)) {
            throw new IllegalArgumentException("Debe registrar el arqueo de caja antes de cerrarla.");
        }

        double saldoFinal = apertura.getSaldoFinal() != null ? apertura.getSaldoFinal()
                : apertura.getSaldoInicial() != null ? apertura.getSaldoInicial() : 0.0;
        double saldoCaja = caja.getSaldoActual() != null ? caja.getSaldoActual() : 0.0;

        apertura.setFechaCierre(new Date());
        apertura.setEstadoCaja("CERRADO");
        apertura.setSaldoFinal(saldoFinal);
        caja.setEstadoCaja("LIBRE");
        caja.setSaldoActual(saldoCaja + saldoFinal);

        aperturaCajaRepository.save(apertura);
        cajaRepository.save(caja);
    }
}
