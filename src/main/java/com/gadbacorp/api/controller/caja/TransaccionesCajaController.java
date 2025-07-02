package com.gadbacorp.api.controller.caja;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.gadbacorp.api.entity.caja.TransaccionesCaja;
import com.gadbacorp.api.entity.caja.TransaccionesCajaDTO;
import com.gadbacorp.api.entity.ventas.MetodosPago;
import com.gadbacorp.api.repository.caja.AperturaCajaRepository;
import com.gadbacorp.api.repository.ventas.MetodosPagoRepository;
import com.gadbacorp.api.service.caja.ITransaccionesCajaServices;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/minimarket")
public class TransaccionesCajaController {
    
    @Autowired
    private ITransaccionesCajaServices transaccionesCajaService;

    @Autowired
    private AperturaCajaRepository aperturaCajaRepository;

    @Autowired
    private MetodosPagoRepository metodosPagoRepository;

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
    // Obtener la apertura de caja relacionada
    AperturaCaja aperturaCaja = aperturaCajaRepository.findById(dto.getId_apertura_caja()).orElse(null);
    if (aperturaCaja == null) {
        return ResponseEntity.badRequest().body("No existe la apertura de caja con id: " + dto.getId_apertura_caja());
    }

    MetodosPago metodosPago = metodosPagoRepository.findById(dto.getIdMetodoPago()).orElse(null);
    if (metodosPago == null) {
        return ResponseEntity.badRequest().body("No existe el metodo de pago con id: " + dto.getIdMetodoPago());
    }

    // Obtener el saldo actual (usar saldoFinal si está disponible, sino usar saldoInicial)
    Double saldoActual = aperturaCaja.getSaldoFinal() != null 
        ? aperturaCaja.getSaldoFinal() 
        : aperturaCaja.getSaldoInicial() != null 
            ? aperturaCaja.getSaldoInicial() 
            : 0.0;

    // Validar tipo de movimiento
    if (!"INGRESO".equalsIgnoreCase(dto.getTipoMovimiento()) && !"EGRESO".equalsIgnoreCase(dto.getTipoMovimiento())) {
        return ResponseEntity.badRequest().body("Tipo de movimiento inválido: " + dto.getTipoMovimiento());
    }

    // Validar saldo suficiente para egreso
    if ("EGRESO".equalsIgnoreCase(dto.getTipoMovimiento()) && dto.getMonto() > saldoActual) {
        return ResponseEntity.badRequest().body("Saldo insuficiente. No se puede egresar más de lo disponible.");
    }

    // Crear la transacción
    TransaccionesCaja transaccionesCaja = new TransaccionesCaja();
    transaccionesCaja.setFecha(new Date());
    transaccionesCaja.setMonto(dto.getMonto());
    transaccionesCaja.setObservaciones(dto.getObservaciones());
    transaccionesCaja.setTipoMovimiento(dto.getTipoMovimiento());
    transaccionesCaja.setAperturaCaja(aperturaCaja);
    transaccionesCaja.setMetodoPago(metodosPago);

    // Actualizar saldo total
    if ("INGRESO".equalsIgnoreCase(dto.getTipoMovimiento())) {
        saldoActual += dto.getMonto();
    } else {
        saldoActual -= dto.getMonto();
    }
    aperturaCaja.setSaldoFinal(saldoActual);

    // === NUEVA LÓGICA saldoEfectivo ===
    if ("Efectivo".equalsIgnoreCase(metodosPago.getNombre())) {
        // Si no tiene saldoEfectivo inicializado, ponemos en 0
        Double saldoEfectivo = aperturaCaja.getSaldoEfectivo() != null ? aperturaCaja.getSaldoEfectivo() : 0.0;
        if ("INGRESO".equalsIgnoreCase(dto.getTipoMovimiento())) {
            saldoEfectivo += dto.getMonto();
        } else {
            saldoEfectivo -= dto.getMonto();
        }
        aperturaCaja.setSaldoEfectivo(saldoEfectivo);
    }

    // Guardar cambios
    aperturaCajaRepository.save(aperturaCaja);
    TransaccionesCaja transaccionGuardada = transaccionesCajaService.guardarTransaccion(transaccionesCaja);

    return ResponseEntity.ok(transaccionGuardada);
}

@PutMapping("/transaccion-cajas")
public ResponseEntity<?> editarTransaccion(@RequestBody TransaccionesCajaDTO dto) {
    if (dto.getIdTransaccionesCaja() == null) {
        return ResponseEntity.badRequest().body("El ID de la transacción es requerido.");
    }

    // Buscar la transacción existente
    Optional<TransaccionesCaja> optionalTransaccion = transaccionesCajaService.buscarTransaccion(dto.getIdTransaccionesCaja());
    if (!optionalTransaccion.isPresent()) {
        return ResponseEntity.badRequest().body("No se encontró la transacción con ID: " + dto.getIdTransaccionesCaja());
    }

    TransaccionesCaja transaccionExistente = optionalTransaccion.get();
    AperturaCaja aperturaCaja = transaccionExistente.getAperturaCaja();
    if (aperturaCaja == null) {
        return ResponseEntity.badRequest().body("No se encontró la apertura de caja asociada.");
    }

    // Obtener saldo actual
    Double saldoActual = aperturaCaja.getSaldoFinal() != null ? aperturaCaja.getSaldoFinal() : 0.0;

    // Revertir efecto de la transacción anterior
    if ("INGRESO".equalsIgnoreCase(transaccionExistente.getTipoMovimiento())) {
        saldoActual -= transaccionExistente.getMonto();
    } else if ("EGRESO".equalsIgnoreCase(transaccionExistente.getTipoMovimiento())) {
        saldoActual += transaccionExistente.getMonto();
    }

    // Validar nuevo tipo de movimiento
    if (!"INGRESO".equalsIgnoreCase(dto.getTipoMovimiento()) && !"EGRESO".equalsIgnoreCase(dto.getTipoMovimiento())) {
        return ResponseEntity.badRequest().body("Tipo de movimiento inválido: " + dto.getTipoMovimiento());
    }

    // Validar saldo suficiente si es EGRESO nuevo
    if ("EGRESO".equalsIgnoreCase(dto.getTipoMovimiento()) && dto.getMonto() > saldoActual) {
        return ResponseEntity.badRequest().body("Saldo insuficiente para actualizar la transacción.");
    }

    // Aplicar nuevo efecto
    if ("INGRESO".equalsIgnoreCase(dto.getTipoMovimiento())) {
        saldoActual += dto.getMonto();
    } else {
        saldoActual -= dto.getMonto();
    }

    // Actualizar apertura de caja
    aperturaCaja.setSaldoFinal(saldoActual);
    aperturaCajaRepository.save(aperturaCaja);

    // Actualizar transacción
    transaccionExistente.setFecha(new Date());
    transaccionExistente.setMonto(dto.getMonto());
    transaccionExistente.setObservaciones(dto.getObservaciones());
    transaccionExistente.setTipoMovimiento(dto.getTipoMovimiento());

    return ResponseEntity.ok(transaccionesCajaService.editarTransacciones(transaccionExistente));
}

@DeleteMapping("/transaccion-caja/{id}")
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

    // Revertir el efecto de la transacción eliminada
    Double saldoActual = aperturaCaja.getSaldoFinal() != null ? aperturaCaja.getSaldoFinal() : 0.0;

    if ("INGRESO".equalsIgnoreCase(transaccion.getTipoMovimiento())) {
        saldoActual -= transaccion.getMonto();
    } else if ("EGRESO".equalsIgnoreCase(transaccion.getTipoMovimiento())) {
        saldoActual += transaccion.getMonto();
    }

    aperturaCaja.setSaldoFinal(saldoActual);
    aperturaCajaRepository.save(aperturaCaja);

    // Eliminar transacción
    transaccionesCajaService.eliminarTransacciones(id);

    return ResponseEntity.ok("La transacción fue eliminada correctamente.");
}

}