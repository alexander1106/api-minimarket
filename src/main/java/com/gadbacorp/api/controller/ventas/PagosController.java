package com.gadbacorp.api.controller.ventas;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gadbacorp.api.entity.caja.AperturaCaja;
import com.gadbacorp.api.entity.caja.SaldoMetodoPago;
import com.gadbacorp.api.entity.caja.TransaccionesCaja;
import com.gadbacorp.api.entity.ventas.MetodosPago;
import com.gadbacorp.api.entity.ventas.Pagos;
import com.gadbacorp.api.entity.ventas.PagosDTO;
import com.gadbacorp.api.entity.ventas.Ventas;
import com.gadbacorp.api.repository.caja.AperturaCajaRepository;
import com.gadbacorp.api.repository.caja.SaldoMetodoPagoRepository;
import com.gadbacorp.api.repository.ventas.MetodosPagoRepository;
import com.gadbacorp.api.repository.ventas.PagosRepository;
import com.gadbacorp.api.repository.ventas.VentasRepository;
import com.gadbacorp.api.service.caja.ITransaccionesCajaServices;
import com.gadbacorp.api.service.jpa.ventas.CotizacionesService;
import com.gadbacorp.api.service.ventas.IPagosService;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/minimarket")
public class PagosController {

    @Autowired
    private IPagosService pagosService;

    @Autowired
    private MetodosPagoRepository metodosPagoRepository;
    @Autowired
    private AperturaCajaRepository aperturaCajaRepository;
    @Autowired
    private VentasRepository ventasRepository;
    @Autowired
    private SaldoMetodoPagoRepository saldoMetodoPagoRepository;
    @Autowired
    private PagosRepository pagosRepository;

    @Autowired
    private ITransaccionesCajaServices transaccionesCajaService;

    @Autowired
    private CotizacionesService cotizacionesService;

    @GetMapping("/pagos")
    public List<Pagos> listarPagos() {
        return pagosService.listarPagos();
    }

@PostMapping("/pagos")
public ResponseEntity<?> guardar(@RequestBody PagosDTO dto) {
    if (dto.getId_metodo_pago() == null) {
        return ResponseEntity.badRequest().body("Debe especificar método de pago.");
    }
    if (dto.getId_venta() == null) {
        return ResponseEntity.badRequest().body("Debe especificar cotización.");
    }

    // Buscar método de pago
    MetodosPago metodo = metodosPagoRepository.findById(dto.getId_metodo_pago())
            .orElseThrow(() -> new IllegalArgumentException("Método de pago no encontrado"));

    if (metodo.getSucursal() == null) {
        return ResponseEntity.badRequest().body("El método de pago no tiene una sucursal asignada.");
    }
    Integer idSucursal = metodo.getSucursal().getIdSucursal();

    // Convertir cotización en venta
    Ventas venta = cotizacionesService.convertirCotizacionAVenta(dto.getId_venta(), idSucursal);


          // Generar comprobante correlativo
        String tipoComprobante = dto.getComprobantePago();
        String prefijo = tipoComprobante.equalsIgnoreCase("FACTURA") ? "F" : "B";
        List<String> lista = ventasRepository.findUltimoComprobantePorTipo(tipoComprobante);
        String ultimoNro = (lista == null || lista.isEmpty()) ? null : lista.get(0);

        int nuevoNumero = 1;
        if (ultimoNro != null && !ultimoNro.isEmpty()) {
            String[] partes = ultimoNro.split("-");
            if (partes.length == 2) {
                nuevoNumero = Integer.parseInt(partes[1]) + 1;
            }
        }
        String numeroFormateado = String.format("%s-%06d", prefijo, nuevoNumero);
        venta.setNro_comrprobante(numeroFormateado);
        venta = ventasRepository.save(venta);

    // Registrar pago
    Pagos pago = new Pagos();
    pago.setEstadoPago(dto.getEstadoPago());
    pago.setObservaciones(dto.getObservaciones());
    pago.setReferenciaPago(dto.getReferenciaPago());
    pago.setFechaPago(LocalDate.now());
    pago.setMontoPagado(dto.getMontoPagado());
    pago.setMetodosPago(metodo);
    pago.setVentas(venta);

    Pagos pagoGuardado = pagosService.guardarPago(pago);

    // Cambiar estado de la cotización a 'PAGADA'
    cotizacionesService.marcarCotizacionComoPagada(dto.getId_venta());

    // === CREAR la transacción de caja automáticamente con lógica de validaciones ===
AperturaCaja aperturaCaja = aperturaCajaRepository
        .findByCaja_Sucursales_IdSucursalAndEstadoCaja(idSucursal, "ABIERTA")
        .orElseThrow(() -> new IllegalArgumentException("No existe una apertura de caja activa para la sucursal."));

    // Obtener saldo actual
    Double saldoActual = aperturaCaja.getSaldoFinal() != null
            ? aperturaCaja.getSaldoFinal()
            : aperturaCaja.getSaldoInicial() != null
                ? aperturaCaja.getSaldoInicial()
                : 0.0;

    // Crear transacción de caja como INGRESO
    TransaccionesCaja transaccion = new TransaccionesCaja();
    transaccion.setFecha(new Date());
    transaccion.setMonto(dto.getMontoPagado());
    transaccion.setObservaciones("Ingreso por pago de cotizacion");
    transaccion.setTipoMovimiento("INGRESO");
    transaccion.setAperturaCaja(aperturaCaja);
    transaccion.setMetodoPago(metodo);

    // Actualizar saldo total
    saldoActual += dto.getMontoPagado();
    aperturaCaja.setSaldoFinal(saldoActual);

    // === LÓGICA saldoEfectivo ===
    if ("Efectivo".equalsIgnoreCase(metodo.getNombre())) {
        Double saldoEfectivo = aperturaCaja.getSaldoEfectivo() != null ? aperturaCaja.getSaldoEfectivo() : 0.0;
        saldoEfectivo += dto.getMontoPagado();
        aperturaCaja.setSaldoEfectivo(saldoEfectivo);
    }

    // Actualizar saldo por método de pago
    Optional<SaldoMetodoPago> saldoMetodoOpt = saldoMetodoPagoRepository.findByAperturaCajaAndMetodoPago(aperturaCaja, metodo);
    SaldoMetodoPago saldoMetodoPago;
    if (saldoMetodoOpt.isPresent()) {
        saldoMetodoPago = saldoMetodoOpt.get();
    } else {
        saldoMetodoPago = new SaldoMetodoPago();
        saldoMetodoPago.setAperturaCaja(aperturaCaja);
        saldoMetodoPago.setMetodoPago(metodo);
        saldoMetodoPago.setSaldo(0.0);
        saldoMetodoPago.setEstado(1);
    }

    Double saldoMetodoActual = saldoMetodoPago.getSaldo() != null ? saldoMetodoPago.getSaldo() : 0.0;
    saldoMetodoActual += dto.getMontoPagado();
    saldoMetodoPago.setSaldo(saldoMetodoActual);
    
    // Guardar todo
    aperturaCajaRepository.save(aperturaCaja);
    saldoMetodoPagoRepository.save(saldoMetodoPago);
    transaccionesCajaService.guardarTransaccion(transaccion);

    return ResponseEntity.ok(pagoGuardado);
}

     @GetMapping("/pagos/{id}")
    public Optional<Pagos> buscarId(@PathVariable("id") Integer id){
        return pagosService.buscarPago(id);
    }

    @PutMapping("/pagos")
    public ResponseEntity<?> modificar(@RequestBody PagosDTO dto) {
        if (dto.getIdPago() == null) {
            return ResponseEntity.badRequest().body("El ID del pago es obligatorio para modificar.");
        }

        Optional<Pagos> optionalPago = pagosRepository.findById(dto.getIdPago());
        if (!optionalPago.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pago no encontrado con ID: " + dto.getIdPago());
        }

        Pagos pago = optionalPago.get();
        pago.setEstadoPago(dto.getEstadoPago());
        pago.setObservaciones(dto.getObservaciones());
        pago.setReferenciaPago(dto.getReferenciaPago());
        pago.setFechaPago(LocalDate.now());
        pago.setMontoPagado(dto.getMontoPagado());

        // Relaciones
        MetodosPago metodo = metodosPagoRepository.findById(dto.getId_metodo_pago()).orElse(null);
        pago.setMetodosPago(metodo);

        Ventas venta = ventasRepository.findById(dto.getId_venta()).orElse(null);
        pago.setVentas(venta);

        Pagos pagoActualizado = pagosService.guardarPago(pago);
        return ResponseEntity.ok(pagoActualizado);
    }

    @DeleteMapping("/pagos/{id}")
    public String eliminar(@PathVariable Integer id){
        pagosService.eliminarPago(id);
        return "El pago fue eliminado";
    }

}
