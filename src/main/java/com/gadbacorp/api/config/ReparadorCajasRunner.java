package com.gadbacorp.api.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.gadbacorp.api.entity.caja.Caja;
import com.gadbacorp.api.repository.caja.AperturaCajaRepository;
import com.gadbacorp.api.repository.caja.CajaRepository;

/**
 * Deja coherente el estado de las cajas con sus turnos: una caja OCUPADA sin ningun turno abierto vuelve a LIBRE
 * (un cierre que no la libero) y una LIBRE con un turno abierto pasa a OCUPADA. Las MALOGRADAS no se tocan.
 */
@Component
@Order(2)
public class ReparadorCajasRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(ReparadorCajasRunner.class);

    private final CajaRepository cajas;
    private final AperturaCajaRepository aperturas;

    public ReparadorCajasRunner(CajaRepository cajas, AperturaCajaRepository aperturas) {
        this.cajas = cajas;
        this.aperturas = aperturas;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        for (Caja caja : cajas.findAll()) {
            boolean hayTurno = aperturas.existsByCaja_IdCajaAndEstadoCaja(caja.getIdCaja(), "ABIERTA");
            if ("OCUPADA".equalsIgnoreCase(caja.getEstadoCaja()) && !hayTurno) {
                caja.setEstadoCaja("LIBRE");
                cajas.save(caja);
                log.info("Caja '{}' estaba OCUPADA sin turno abierto: ahora esta LIBRE", caja.getNombreCaja());
            } else if ("LIBRE".equalsIgnoreCase(caja.getEstadoCaja()) && hayTurno) {
                caja.setEstadoCaja("OCUPADA");
                cajas.save(caja);
                log.info("Caja '{}' estaba LIBRE con un turno abierto: ahora esta OCUPADA", caja.getNombreCaja());
            }
        }
    }
}
