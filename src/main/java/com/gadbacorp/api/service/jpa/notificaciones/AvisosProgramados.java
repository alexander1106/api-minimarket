package com.gadbacorp.api.service.jpa.notificaciones;

import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import com.gadbacorp.api.entity.administrable.Empresas;
import com.gadbacorp.api.entity.inventario.LoteProducto;
import com.gadbacorp.api.entity.notificaciones.Notificacion;
import com.gadbacorp.api.repository.administrable.EmpresasRepository;
import com.gadbacorp.api.repository.inventario.LoteProductoRepository;

/**
 * Revisa cada dia lo que no depende de una accion del usuario: lotes por vencer o vencidos y suscripciones
 * proximas a vencer. Tambien corre al arrancar. El mismo hecho no se vuelve a avisar mientras siga sin leerse.
 */
@Component
public class AvisosProgramados {

    private static final Logger log = LoggerFactory.getLogger(AvisosProgramados.class);

    private final boolean habilitado;
    private final int diasVencimiento;
    private final int diasSuscripcion;
    private final LoteProductoRepository lotes;
    private final EmpresasRepository empresas;
    private final NotificacionService notificaciones;
    private final TransactionTemplate lectura;

    public AvisosProgramados(@Value("${app.notificaciones.programadas:true}") boolean habilitado,
                             @Value("${app.notificaciones.dias-vencimiento:7}") int diasVencimiento,
                             @Value("${app.notificaciones.dias-suscripcion:7}") int diasSuscripcion,
                             LoteProductoRepository lotes, EmpresasRepository empresas, NotificacionService notificaciones,
                             PlatformTransactionManager tx) {
        this.lectura = new TransactionTemplate(tx);
        this.lectura.setReadOnly(true);
        this.habilitado = habilitado;
        this.diasVencimiento = diasVencimiento;
        this.diasSuscripcion = diasSuscripcion;
        this.lotes = lotes;
        this.empresas = empresas;
        this.notificaciones = notificaciones;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void alArrancar() {
        revisar();
    }

    @Scheduled(cron = "${app.notificaciones.cron:0 0 7 * * *}")
    public void revisar() {
        if (!habilitado) {
            return;
        }
        try {
            lectura.executeWithoutResult(s -> {
                avisarLotes();
                avisarSuscripciones();
            });
            notificaciones.depurar(30);
        } catch (RuntimeException e) {
            log.warn("No se pudieron revisar los avisos programados: {}", e.getMessage());
        }
    }

    private void avisarLotes() {
        LocalDate hoy = LocalDate.now();
        for (LoteProducto l : lotes.porVencer(hoy.plusDays(diasVencimiento), null)) {
            if (l.getProducto() == null || l.getProducto().getEmpresa() == null) {
                continue;
            }
            boolean vencido = l.getFechaVencimiento().isBefore(hoy);
            long dias = java.time.temporal.ChronoUnit.DAYS.between(hoy, l.getFechaVencimiento());
            String nombre = l.getProducto().getNombre() + (l.getCodigo() == null ? "" : " (lote " + l.getCodigo() + ")");
            notificaciones.notificarEmpresa(l.getProducto().getEmpresa().getIdempresa(), "Inventario",
                    vencido ? Notificacion.VENCIDO : Notificacion.POR_VENCER,
                    vencido || dias <= 2 ? Notificacion.CRITICA : Notificacion.ADVERTENCIA,
                    vencido ? "Producto vencido" : "Producto por vencer",
                    vencido ? nombre + " venció el " + l.getFechaVencimiento() + " y aún tiene " + l.getCantidadDisponible() + " en stock"
                            : nombre + " vence " + (dias == 0 ? "hoy" : "en " + dias + (dias == 1 ? " día" : " días"))
                            + " y quedan " + l.getCantidadDisponible(),
                    "/admin/lotes",
                    "LOTE:" + l.getIdLote() + ":" + (vencido ? "V" : "P"));
        }
    }

    private void avisarSuscripciones() {
        LocalDate hoy = LocalDate.now();
        for (Empresas e : empresas.findAll()) {
            LocalDate vence = e.getFechaVencimiento();
            if (vence == null || vence.isAfter(hoy.plusDays(diasSuscripcion))) {
                continue;
            }
            boolean vencida = vence.isBefore(hoy);
            notificaciones.notificarSuperadmins(Notificacion.SUSCRIPCION,
                    vencida ? Notificacion.CRITICA : Notificacion.ADVERTENCIA,
                    vencida ? "Suscripción vencida" : "Suscripción por vencer",
                    e.getRazonsocial() + (vencida ? " venció el " : " vence el ") + vence,
                    "/admin/empresas",
                    "SUSCRIPCION:" + e.getIdempresa() + ":" + vence);
        }
    }
}
