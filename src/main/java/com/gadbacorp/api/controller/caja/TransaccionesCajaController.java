package com.gadbacorp.api.controller.caja;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
import com.gadbacorp.api.entity.caja.TransaccionesCajaDTO;
import com.gadbacorp.api.entity.ventas.MetodosPago;
import com.gadbacorp.api.repository.caja.AperturaCajaRepository;
import com.gadbacorp.api.repository.caja.SaldoMetodoPagoRepository;
import com.gadbacorp.api.repository.ventas.MetodosPagoRepository;
import com.gadbacorp.api.service.caja.ITransaccionesCajaServices;

import io.swagger.v3.oas.annotations.tags.Tag;
@Tag(name = "Caja - Transacciones de caja")
@RestController
@RequestMapping("/api/minimarket")
public class TransaccionesCajaController {
    
    @Autowired
    private ITransaccionesCajaServices transaccionesCajaService;

    @Autowired
    private AperturaCajaRepository aperturaCajaRepository;

    @Autowired
    private SaldoMetodoPagoRepository saldoMetodoPagoRepository;

    @Autowired
    private MetodosPagoRepository metodosPagoRepository;

    @Autowired
    private com.gadbacorp.api.service.jpa.cajas.CajaMovimientoService cajaMovimientoService;

    @GetMapping("/transacciones-cajas")
    public List<TransaccionesCaja> listarTransaccionesCajas(){
        return transaccionesCajaService.listarTransaccionesCajas();
    }

    @GetMapping("/transacciones-cajas/{id}")
    public Optional<TransaccionesCaja> buscarTransaccion(@PathVariable Integer id){
        return transaccionesCajaService.buscarTransaccion(id);
    }

@PostMapping("/transacciones-cajas")
public ResponseEntity<?> guardarTransaccion(@RequestBody TransaccionesCajaDTO dto) {
    if (dto.getId_apertura_caja() == null) {
        return ResponseEntity.badRequest().body("La apertura de caja es obligatoria.");
    }
    if (dto.getIdMetodoPago() == null) {
        return ResponseEntity.badRequest().body("El metodo de pago es obligatorio.");
    }
    MetodosPago metodosPago = metodosPagoRepository.findById(dto.getIdMetodoPago()).orElse(null);
    if (metodosPago == null) {
        return ResponseEntity.badRequest().body("No existe el metodo de pago con id: " + dto.getIdMetodoPago());
    }
    try {
        return ResponseEntity.ok(cajaMovimientoService.registrar(dto.getId_apertura_caja(), metodosPago,
                dto.getTipoMovimiento(), dto.getMonto(), dto.getObservaciones()));
    } catch (IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}

@PutMapping({"/transacciones-cajas", "/transaccion-cajas"})
public ResponseEntity<?> editarTransaccion(@RequestBody TransaccionesCajaDTO dto) {
    if (dto.getIdTransaccionesCaja() == null) {
        return ResponseEntity.badRequest().body("El ID de la transacción es requerido.");
    }
    Optional<TransaccionesCaja> optionalTransaccion = transaccionesCajaService.buscarTransaccion(dto.getIdTransaccionesCaja());
    if (!optionalTransaccion.isPresent()) {
        return ResponseEntity.badRequest().body("No se encontró la transacción con ID: " + dto.getIdTransaccionesCaja());
    }

    TransaccionesCaja transaccionExistente = optionalTransaccion.get();
    AperturaCaja aperturaCaja = transaccionExistente.getAperturaCaja();
    if (aperturaCaja == null) {
        return ResponseEntity.badRequest().body("No se encontró la apertura de caja asociada.");
    }

    Double saldoActual = aperturaCaja.getSaldoFinal() != null ? aperturaCaja.getSaldoFinal() : 0.0;

    if ("INGRESO".equalsIgnoreCase(transaccionExistente.getTipoMovimiento())) {
        saldoActual -= transaccionExistente.getMonto();
    } else if ("EGRESO".equalsIgnoreCase(transaccionExistente.getTipoMovimiento())) {
        saldoActual += transaccionExistente.getMonto();
    }

    if (!"INGRESO".equalsIgnoreCase(dto.getTipoMovimiento()) && !"EGRESO".equalsIgnoreCase(dto.getTipoMovimiento())) {
        return ResponseEntity.badRequest().body("Tipo de movimiento inválido: " + dto.getTipoMovimiento());
    }

    if ("EGRESO".equalsIgnoreCase(dto.getTipoMovimiento()) && dto.getMonto() > saldoActual) {
        return ResponseEntity.badRequest().body("Saldo insuficiente para actualizar la transacción.");
    }

    if ("INGRESO".equalsIgnoreCase(dto.getTipoMovimiento())) {
        saldoActual += dto.getMonto();
    } else {
        saldoActual -= dto.getMonto();
    }

    aperturaCaja.setSaldoFinal(saldoActual);
    aperturaCajaRepository.save(aperturaCaja);

    transaccionExistente.setFecha(new Date());
    transaccionExistente.setMonto(dto.getMonto());
    transaccionExistente.setObservaciones(dto.getObservaciones());
    transaccionExistente.setTipoMovimiento(dto.getTipoMovimiento());

    return ResponseEntity.ok(transaccionesCajaService.editarTransacciones(transaccionExistente));
}

@DeleteMapping({"/transacciones-cajas/{id}", "/transaccion-caja/{id}"})
public ResponseEntity<?> eliminarCaja(@PathVariable Integer id) {
    Optional<TransaccionesCaja> optionalTransaccion = transaccionesCajaService.buscarTransaccion(id);
    if (!optionalTransaccion.isPresent()) {
        return ResponseEntity.badRequest().body("No se encontró la transacción con ID: " + id);
    }

    TransaccionesCaja transaccion = optionalTransaccion.get();
    AperturaCaja aperturaCaja = transaccion.getAperturaCaja();
    if (aperturaCaja == null) {
        return ResponseEntity.badRequest().body("La transacción no tiene una apertura de caja válida.");
    }

    Double saldoActual = aperturaCaja.getSaldoFinal() != null ? aperturaCaja.getSaldoFinal() : 0.0;

    if ("INGRESO".equalsIgnoreCase(transaccion.getTipoMovimiento())) {
        saldoActual -= transaccion.getMonto();
    } else if ("EGRESO".equalsIgnoreCase(transaccion.getTipoMovimiento())) {
        saldoActual += transaccion.getMonto();
    }

    aperturaCaja.setSaldoFinal(saldoActual);
    aperturaCajaRepository.save(aperturaCaja);

    transaccionesCajaService.eliminarTransacciones(id);

    return ResponseEntity.ok("La transacción fue eliminada correctamente.");
}
@GetMapping("/transacciones-cajas/apertura/{idAperturaCaja}")
public ResponseEntity<?> listarTransaccionesPorApertura(@PathVariable Integer idAperturaCaja) {
    Optional<AperturaCaja> aperturaOpt = aperturaCajaRepository.findById(idAperturaCaja);
    if (!aperturaOpt.isPresent()) {
        return ResponseEntity.badRequest().body("No existe la apertura de caja con id: " + idAperturaCaja);
    }
    List<TransaccionesCaja> transacciones = transaccionesCajaService.listarPorAperturaCaja(idAperturaCaja);
    return ResponseEntity.ok(transacciones);
}

}