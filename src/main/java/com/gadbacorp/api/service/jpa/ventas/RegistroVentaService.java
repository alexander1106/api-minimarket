package com.gadbacorp.api.service.jpa.ventas;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gadbacorp.api.entity.delivery.Delivery;
import com.gadbacorp.api.entity.delivery.DeliveryDTO;
import com.gadbacorp.api.entity.inventario.Productos;
import com.gadbacorp.api.entity.ventas.Clientes;
import com.gadbacorp.api.entity.ventas.DetallesVentas;
import com.gadbacorp.api.entity.ventas.DetallesVentasDTO;
import com.gadbacorp.api.entity.ventas.MetodosPago;
import com.gadbacorp.api.entity.ventas.Pagos;
import com.gadbacorp.api.entity.ventas.VentaCompletaDTO;
import com.gadbacorp.api.entity.ventas.Ventas;
import com.gadbacorp.api.messaging.VentaCreadaEvent;
import com.gadbacorp.api.messaging.VentaEventPublisher;
import com.gadbacorp.api.repository.delivery.DeliveryRepository;
import com.gadbacorp.api.repository.inventario.ProductosRepository;
import com.gadbacorp.api.repository.ventas.ClientesRepository;
import com.gadbacorp.api.repository.ventas.DetallesVentasRepository;
import com.gadbacorp.api.repository.ventas.MetodosPagoRepository;
import com.gadbacorp.api.repository.ventas.PagosRepository;
import com.gadbacorp.api.repository.ventas.VentasRepository;
import com.gadbacorp.api.service.jpa.cajas.CajaMovimientoService;
import com.gadbacorp.api.service.jpa.inventario.ConversionUnidadesService;
import com.gadbacorp.api.service.jpa.inventario.StockService;

/**
 * Caso de uso "registrar venta completa": venta + detalles + descuento de stock + pago,
 * todo en una sola transaccion. Cualquier error revierte la operacion completa.
 */
@Service
public class RegistroVentaService {

    @Autowired
    private VentasRepository ventasRepository;
    @Autowired
    private ClientesRepository clientesRepository;
    @Autowired
    private MetodosPagoRepository metodosPagoRepository;
    @Autowired
    private ProductosRepository productosRepository;
    @Autowired
    private DetallesVentasRepository detallesVentasRepository;
    @Autowired
    private PagosRepository pagosRepository;
    @Autowired
    private VentaEventPublisher eventPublisher;
    @Autowired
    private NumeracionService numeracionService;
    @Autowired
    private StockService stockService;
    @Autowired
    private ConversionUnidadesService conversionUnidadesService;
    @Autowired
    private CajaMovimientoService cajaMovimientoService;
    @Autowired
    private DeliveryRepository deliveryRepository;
    @Autowired
    private ImpuestoService impuestoService;
    @Autowired
    private com.gadbacorp.api.repository.caja.AperturaCajaRepository aperturaCajaRepository;

    /**
     * @throws IllegalArgumentException datos invalidos o entidades inexistentes (HTTP 400)
     * @throws StockInsuficienteException no hay stock suficiente en la sucursal (HTTP 400)
     */
    @Transactional
    public Ventas registrar(VentaCompletaDTO dto) {
        if (dto.getId_cliente() == null) {
            throw new IllegalArgumentException("El id_cliente es obligatorio.");
        }
        if (dto.getId_metodo_pago() == null) {
            throw new IllegalArgumentException("El id_metodo_pago es obligatorio.");
        }
        if (dto.getIdSucursal() == null) {
            throw new IllegalArgumentException("La sucursal es obligatoria.");
        }
        if (dto.getTipo_comprobante() == null || dto.getTipo_comprobante().isBlank()) {
            throw new IllegalArgumentException("El tipo_comprobante es obligatorio.");
        }
        if (dto.getDetalles() == null || dto.getDetalles().isEmpty()) {
            throw new IllegalArgumentException("La venta debe tener al menos un detalle.");
        }

        com.gadbacorp.api.entity.empleados.Usuarios quien = usuarioActual();
        validarCaja(dto, quien);

        Clientes cliente = clientesRepository.findById(dto.getId_cliente())
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado."));
        MetodosPago metodoPago = metodosPagoRepository.findById(dto.getId_metodo_pago())
                .orElseThrow(() -> new IllegalArgumentException("Método de pago no encontrado."));

        Ventas venta = new Ventas();
        venta.setTotal_venta(dto.getTotal_venta());
        venta.setTipo_comprobante(dto.getTipo_comprobante());
        venta.setFecha_venta(LocalDate.now());
        venta.setEstado_venta("PAGADA");
        venta.setEstado(dto.getEstado());
        venta.setCliente(cliente);
        venta.setNro_comrprobante(numeracionService.siguienteComprobante(dto.getTipo_comprobante()));
        venta.setIdAperturaCaja(dto.getId_apertura_caja());
        if (quien != null) {
            venta.setIdVendedor(quien.getIdUsuario());
            venta.setVendedor(quien.getUsername());
        }
        venta = ventasRepository.save(venta);

        ImpuestoService.Desglose igv = impuestoService.nuevo();
        for (DetallesVentasDTO detalleDTO : dto.getDetalles()) {
            if (detalleDTO.getId_producto() == null) {
                throw new IllegalArgumentException("El id_producto en detalle es obligatorio.");
            }
            if (detalleDTO.getCantidad() == null || detalleDTO.getCantidad().signum() <= 0) {
                throw new IllegalArgumentException("La cantidad del detalle debe ser mayor a 0.");
            }

            Productos producto = productosRepository.findById(detalleDTO.getId_producto())
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado."));

            // la cantidad puede venir en una presentacion (2 sacos) o en unidad base (2.5 kg): el stock se mueve en base
            ConversionUnidadesService.Conversion conversion =
                    conversionUnidadesService.convertir(producto, detalleDTO.getIdPresentacion(), detalleDTO.getCantidad());

            DetallesVentas detalle = new DetallesVentas();
            detalle.setVentas(venta);
            detalle.setProductos(producto);
            detalle.setFechaVenta(LocalDate.now());
            detalle.setPrecioUnitario(detalleDTO.getPecioUnitario());
            detalle.setCantidad(detalleDTO.getCantidad());
            detalle.setPresentacion(conversion.presentacion());
            detalle.setCantidadBase(conversion.cantidadBase());
            detalle.setSubTotal(detalleDTO.getSubTotal());
            detalle.setEstado(detalleDTO.getEstado());
            detallesVentasRepository.save(detalle);
            igv.agregar(detalleDTO.getSubTotal() != null ? detalleDTO.getSubTotal() : 0,
                    !Boolean.FALSE.equals(producto.getAfectoIgv()));

            stockService.descontar(dto.getIdSucursal(), detalleDTO.getId_producto(), conversion.cantidadBase(),
                    com.gadbacorp.api.entity.inventario.MovimientoStock.VENTA, "Venta " + venta.getNro_comrprobante());
        }

        venta.setOpGravada(igv.opGravada());
        venta.setOpInafecta(igv.opInafecta());
        venta.setIgv(igv.igv());
        venta = ventasRepository.save(venta);

        Pagos pago = new Pagos();
        pago.setVentas(venta);
        pago.setEstadoPago(dto.getEstadoPago());
        pago.setFechaPago(LocalDate.now());
        pago.setMontoPagado(dto.getMontoPagado());
        pago.setObservaciones(dto.getObservaciones());
        pago.setMetodosPago(metodoPago);
        pagosRepository.save(pago);

        DeliveryDTO envio = dto.getDelivery();
        if (envio != null) {
            Delivery delivery = new Delivery();
            delivery.setDireccion(envio.getDireccion());
            delivery.setEstadoDelivery("Pendiente");
            delivery.setFechaEnvio(envio.getFechaEnvio());
            delivery.setFechaEntrega(envio.getFechaEntrega());
            delivery.setEncargado(envio.getEncargado());
            delivery.setCostoEnvio(envio.getCostoEnvio());
            delivery.setObservaciones(envio.getObservaciones());
            delivery.setEstado(envio.getEstado() != null ? envio.getEstado() : 1);
            delivery.setVenta(venta);
            deliveryRepository.save(delivery);
        }

        if (dto.getId_apertura_caja() != null) {
            double monto = dto.getMontoPagado() != null ? dto.getMontoPagado() : dto.getTotal_venta();
            cajaMovimientoService.registrar(dto.getId_apertura_caja(), metodoPago,
                    CajaMovimientoService.INGRESO, monto, "Ingreso por venta " + venta.getNro_comrprobante());
        }

        eventPublisher.publicarVentaCreada(new VentaCreadaEvent(
                venta.getIdVenta(),
                venta.getNro_comrprobante(),
                venta.getTipo_comprobante(),
                venta.getTotal_venta(),
                cliente.getIdCliente(),
                dto.getIdSucursal(),
                String.valueOf(venta.getFecha_venta())));

        return venta;
    }

    /** Roles que pueden cobrar sobre el turno de caja de otra persona (supervision). */
    private static final java.util.Set<String> ROLES_SUPERVISION = java.util.Set.of("ADMIN", "EMPRESA", "SUPERVISOR");

    private static com.gadbacorp.api.entity.empleados.Usuarios usuarioActual() {
        org.springframework.security.core.Authentication a =
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        return a != null && a.getPrincipal() instanceof com.gadbacorp.api.entity.empleados.Usuarios u ? u : null;
    }

    /**
     * El turno de caja debe estar abierto y ser de la sucursal de la venta. Un cajero solo cobra en su propio turno;
     * ADMIN, EMPRESA y SUPERVISOR pueden hacerlo en cualquier turno abierto de su sucursal.
     */
    private void validarCaja(VentaCompletaDTO dto, com.gadbacorp.api.entity.empleados.Usuarios quien) {
        if (dto.getId_apertura_caja() == null) {
            return;
        }
        com.gadbacorp.api.entity.caja.AperturaCaja apertura = aperturaCajaRepository.findById(dto.getId_apertura_caja())
                .orElseThrow(() -> new IllegalArgumentException("No existe la apertura de caja con id: " + dto.getId_apertura_caja()));
        if (!"ABIERTA".equalsIgnoreCase(apertura.getEstadoCaja())) {
            throw new IllegalArgumentException("La apertura de caja no está abierta.");
        }
        if (apertura.getCaja() == null || apertura.getCaja().getSucursales() == null
                || !apertura.getCaja().getSucursales().getIdSucursal().equals(dto.getIdSucursal())) {
            throw new IllegalArgumentException("La caja no pertenece a la sucursal de la venta.");
        }
        boolean esSuya = quien != null && apertura.getUsuarios() != null
                && quien.getIdUsuario().equals(apertura.getUsuarios().getIdUsuario());
        String rol = quien == null || quien.getRol() == null ? null : quien.getRol().getNombre();
        if (quien != null && !esSuya && (rol == null || !ROLES_SUPERVISION.contains(rol))) {
            throw new com.gadbacorp.api.excepciones.OperacionNoPermitidaException(
                    "Solo puedes cobrar en tu propia caja. Abre un turno o pide a un administrador que cobre en la caja de "
                            + (apertura.getUsuarios() == null ? "otro usuario" : apertura.getUsuarios().getUsername()) + ".");
        }
    }
}
