package com.gadbacorp.api.service.jpa.ventas;

import java.util.List;
import java.util.function.Supplier;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import com.gadbacorp.api.entity.ventas.Secuencia;
import com.gadbacorp.api.repository.ventas.CotizacionesRepository;
import com.gadbacorp.api.repository.ventas.SecuenciaRepository;
import com.gadbacorp.api.repository.ventas.VentasRepository;

/**
 * Genera numeros correlativos de comprobantes y cotizaciones sin duplicados aunque haya
 * ventas simultaneas: cada clave tiene una fila bloqueada (FOR UPDATE) durante la transaccion.
 */
@Service
public class NumeracionService {

    private final SecuenciaRepository secuenciaRepository;
    private final VentasRepository ventasRepository;
    private final CotizacionesRepository cotizacionesRepository;
    private final TransactionTemplate nuevaTransaccion;

    public NumeracionService(SecuenciaRepository secuenciaRepository,
                             VentasRepository ventasRepository,
                             CotizacionesRepository cotizacionesRepository,
                             PlatformTransactionManager txManager) {
        this.secuenciaRepository = secuenciaRepository;
        this.ventasRepository = ventasRepository;
        this.cotizacionesRepository = cotizacionesRepository;
        this.nuevaTransaccion = new TransactionTemplate(txManager);
        this.nuevaTransaccion.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
    }

    /** B-000001 / F-000001. Cualquier tipo distinto de FACTURA numera como BOLETA. */
    @Transactional
    public String siguienteComprobante(String tipoComprobante) {
        boolean factura = tipoComprobante != null && tipoComprobante.equalsIgnoreCase("FACTURA");
        String clave = factura ? "FACTURA" : "BOLETA";
        String prefijo = factura ? "F" : "B";
        long numero = siguiente(clave, () -> maximoExistente(ventasRepository.listarNumerosPorPrefijo(prefijo + "-%")));
        return String.format("%s-%06d", prefijo, numero);
    }

    /** COT-000001 */
    @Transactional
    public String siguienteCotizacion() {
        long numero = siguiente("COTIZACION",
                () -> maximoExistente(cotizacionesRepository.listarNumerosPorPrefijo("COT-%")));
        return String.format("COT-%06d", numero);
    }

    long siguiente(String clave, Supplier<Long> valorInicial) {
        // La fila se crea ANTES de bloquearla. Si se bloqueara primero una fila que no existe, MySQL (InnoDB) deja un
        // bloqueo de hueco en esta transaccion y el INSERT de la otra transaccion espera hasta agotar el tiempo.
        if (!secuenciaRepository.existsById(clave)) {
            crearSiNoExiste(clave, valorInicial.get());
        }
        Secuencia secuencia = secuenciaRepository.bloquear(clave)
                .orElseThrow(() -> new IllegalStateException("No se pudo inicializar la secuencia " + clave));
        long siguiente = secuencia.getUltimo() + 1;
        secuencia.setUltimo(siguiente);
        secuenciaRepository.save(secuencia);
        return siguiente;
    }

    /** En su propia transaccion: si otro hilo la creo primero, se ignora el choque de clave. */
    private void crearSiNoExiste(String clave, long inicial) {
        try {
            nuevaTransaccion.executeWithoutResult(status -> secuenciaRepository.saveAndFlush(new Secuencia(clave, inicial)));
        } catch (DataIntegrityViolationException e) {
            // ya existe: la creo una transaccion concurrente
        }
    }

    static long maximoExistente(List<String> numeros) {
        long max = 0;
        for (String n : numeros) {
            if (n == null) {
                continue;
            }
            String[] partes = n.split("-");
            if (partes.length == 2) {
                try {
                    max = Math.max(max, Long.parseLong(partes[1].trim()));
                } catch (NumberFormatException ignorado) {
                    // numeros con otro formato (cotizaciones antiguas) no cuentan
                }
            }
        }
        return max;
    }
}
