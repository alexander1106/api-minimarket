package com.gadbacorp.api.controller.ventas;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
import com.gadbacorp.api.entity.empleados.Usuarios;
import com.gadbacorp.api.entity.ventas.MetodosPago;
import com.gadbacorp.api.entity.ventas.Pagos;
import com.gadbacorp.api.entity.ventas.PagosDTO;
import com.gadbacorp.api.entity.ventas.Ventas;
import com.gadbacorp.api.repository.caja.AperturaCajaRepository;
import com.gadbacorp.api.repository.caja.SaldoMetodoPagoRepository;
import com.gadbacorp.api.repository.empleados.UsuarioRepository;
import com.gadbacorp.api.repository.ventas.MetodosPagoRepository;
import com.gadbacorp.api.repository.ventas.PagosRepository;
import com.gadbacorp.api.repository.ventas.VentasRepository;
import com.gadbacorp.api.service.caja.ITransaccionesCajaServices;
import com.gadbacorp.api.service.jpa.ventas.CotizacionesService;
import com.gadbacorp.api.service.ventas.IPagosService;

import io.swagger.v3.oas.annotations.tags.Tag;
@Tag(name = "Ventas - Pagos")
@RestController
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
    private UsuarioRepository usuarioRepository;


    @Autowired
    private ITransaccionesCajaServices transaccionesCajaService;

    @Autowired
    private com.gadbacorp.api.service.jpa.ventas.PagoCotizacionService pagoCotizacionService;

    @GetMapping("/pagos")
    public List<Pagos> listarPagos() {
        return pagosService.listarPagos();
    }

@PostMapping("/pagos")
public ResponseEntity<?> guardar(@RequestBody PagosDTO dto) {
    try {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(pagoCotizacionService.pagar(dto, username));
    } catch (IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
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
