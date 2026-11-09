package com.gadbacorp.api.service.jpa.ventas;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gadbacorp.api.entity.caja.AperturaCaja;
import com.gadbacorp.api.entity.empleados.Usuarios;
import com.gadbacorp.api.entity.ventas.MetodosPago;
import com.gadbacorp.api.entity.ventas.Pagos;
import com.gadbacorp.api.entity.ventas.PagosDTO;
import com.gadbacorp.api.entity.ventas.Ventas;
import com.gadbacorp.api.repository.caja.AperturaCajaRepository;
import com.gadbacorp.api.repository.empleados.UsuarioRepository;
import com.gadbacorp.api.repository.ventas.MetodosPagoRepository;
import com.gadbacorp.api.repository.ventas.PagosRepository;
import com.gadbacorp.api.repository.ventas.VentasRepository;
import com.gadbacorp.api.service.jpa.cajas.CajaMovimientoService;

/**
 * Pago de una cotizacion: la convierte en venta (descontando stock), le asigna comprobante,
 * registra el pago, marca la cotizacion como PAGADA e ingresa el dinero a la caja. Todo o nada.
 */
@Service
public class PagoCotizacionService {

    @Autowired
    private MetodosPagoRepository metodosPagoRepository;
    @Autowired
    private CotizacionesService cotizacionesService;
    @Autowired
    private VentasRepository ventasRepository;
    @Autowired
    private PagosRepository pagosRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private AperturaCajaRepository aperturaCajaRepository;
    @Autowired
    private NumeracionService numeracionService;
    @Autowired
    private CajaMovimientoService cajaMovimientoService;

    @Transactional
    public Pagos pagar(PagosDTO dto, String username) {
        if (dto.getId_metodo_pago() == null) {
            throw new IllegalArgumentException("Debe especificar método de pago.");
        }
        if (dto.getId_venta() == null) {
            throw new IllegalArgumentException("Debe especificar cotización.");
        }
        if (dto.getMontoPagado() == null || dto.getMontoPagado() <= 0) {
            throw new IllegalArgumentException("El monto pagado debe ser mayor a 0.");
        }

        MetodosPago metodo = metodosPagoRepository.findById(dto.getId_metodo_pago())
                .orElseThrow(() -> new IllegalArgumentException("Método de pago no encontrado"));
        if (metodo.getSucursal() == null) {
            throw new IllegalArgumentException("El método de pago no tiene una sucursal asignada.");
        }

        AperturaCaja apertura = resolverApertura(dto, username);

        Ventas venta = cotizacionesService.convertirCotizacionAVenta(dto.getId_venta(),
                metodo.getSucursal().getIdSucursal());

        String tipo = "FACTURA".equalsIgnoreCase(dto.getComprobantePago()) ? "FACTURA" : "BOLETA";
        venta.setTipo_comprobante(tipo);
        venta.setNro_comrprobante(numeracionService.siguienteComprobante(tipo));
        venta = ventasRepository.save(venta);

        Pagos pago = new Pagos();
        pago.setEstadoPago(dto.getEstadoPago());
        pago.setObservaciones(dto.getObservaciones());
        pago.setReferenciaPago(dto.getReferenciaPago());
        pago.setFechaPago(LocalDate.now());
        pago.setMontoPagado(dto.getMontoPagado());
        pago.setMetodosPago(metodo);
        pago.setVentas(venta);
        Pagos guardado = pagosRepository.save(pago);

        cotizacionesService.marcarCotizacionComoPagada(dto.getId_venta());

        cajaMovimientoService.registrar(apertura.getIdAperturaCaja(), metodo, CajaMovimientoService.INGRESO,
                dto.getMontoPagado(), "Ingreso por pago de cotizacion");
        return guardado;
    }

    private AperturaCaja resolverApertura(PagosDTO dto, String username) {
        if (dto.getId_apertura_caja() != null) {
            return aperturaCajaRepository.findById(dto.getId_apertura_caja())
                    .orElseThrow(() -> new IllegalArgumentException("No existe la apertura de caja indicada."));
        }
        Usuarios usuario = usuarioRepository.findByUsername(username);
        if (usuario == null) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }
        List<AperturaCaja> abiertas = aperturaCajaRepository
                .findByUsuarios_IdUsuarioAndEstadoCaja(usuario.getIdUsuario(), "ABIERTA");
        if (abiertas.size() != 1) {
            throw new IllegalArgumentException(
                    "Se esperaba exactamente una apertura de caja activa, pero se encontraron: " + abiertas.size());
        }
        return abiertas.get(0);
    }
}
