package com.gadbacorp.api.controller.caja;

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
import com.gadbacorp.api.entity.caja.Caja;
import com.gadbacorp.api.entity.caja.TransaccionesCaja;
import com.gadbacorp.api.entity.caja.TransferenciaEntreCajasDTO;
import com.gadbacorp.api.entity.caja.TransferenciasEntreCajas;
import com.gadbacorp.api.repository.caja.AperturaCajaRepository;
import com.gadbacorp.api.repository.caja.CajaRepository;
import com.gadbacorp.api.repository.caja.TransaccionesCajaRepository;
import com.gadbacorp.api.service.caja.ITransferenciaEntreCajasService;

import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/api/minimarket")
@CrossOrigin("*")
public class TransferenciaEntreCajasController {
    
    @Autowired
    private ITransferenciaEntreCajasService transferenciaEntreCajasService;
    @Autowired
    private AperturaCajaRepository aperturaCajaRepository;

    @Autowired
    private TransaccionesCajaRepository transaccionCajaRepository;
    @Autowired
    private CajaRepository cajaRepository;
    @GetMapping("/transeferencias-entre-cajas")
    public List<TransferenciasEntreCajas> listarTransferenciasEntreCajas(){
        return transferenciaEntreCajasService.listarTransferenciasEntreCajas();
    }
    @GetMapping("/transferencia-entre-caja/{id}")
    public Optional<TransferenciasEntreCajas> buscarTransferencia(@PathVariable Integer id){
        return transferenciaEntreCajasService.buscarTranseferenciaEntreCajas(id);
    }

@PostMapping("/transferencia-entre-caja")
@Transactional
public ResponseEntity<?> guardarTranseferenciaEntreCaja(@RequestBody TransferenciaEntreCajasDTO dto) {
    // Buscar cajas
    Caja cajaOrigen = cajaRepository.findById(dto.getId_caja_origen()).orElse(null);
    Caja cajaDestino = cajaRepository.findById(dto.getId_caja_destino()).orElse(null);

    if (cajaOrigen == null || cajaDestino == null) {
        return ResponseEntity.badRequest().body("Una o ambas cajas no existen.");
    }

    // Buscar aperturas activas
    Optional<AperturaCaja> aperturaOrigenOpt = aperturaCajaRepository.findByCaja_IdCajaAndCaja_EstadoCaja(dto.getId_caja_origen(), "ABIERTO");
    Optional<AperturaCaja> aperturaDestinoOpt = aperturaCajaRepository.findByCaja_IdCajaAndCaja_EstadoCaja(dto.getId_caja_destino(), "ABIERTO");

    if (aperturaOrigenOpt.isEmpty() || aperturaDestinoOpt.isEmpty()) {
        return ResponseEntity.badRequest().body("Ambas cajas deben tener una apertura activa para realizar la transferencia.");
    }

    AperturaCaja aperturaOrigen = aperturaOrigenOpt.get();
    AperturaCaja aperturaDestino = aperturaDestinoOpt.get();

    // ✅ Validar que haya saldo suficiente
    if (aperturaOrigen.getSaldoFinal() < dto.getMonto()) {
        return ResponseEntity.badRequest().body("La caja origen no tiene suficiente saldo para realizar la transferencia.");
    }

    // Crear y guardar la transferencia
    TransferenciasEntreCajas transferencia = new TransferenciasEntreCajas();
    transferencia.setCajaOrigen(cajaOrigen);
    transferencia.setCajaDestino(cajaDestino);
    transferencia.setFecha(dto.getFecha());
    transferencia.setMonto(dto.getMonto());
    transferencia.setMotivo(dto.getMotivo());
    transferencia.setObservaciones(dto.getObservaciones());

    TransferenciasEntreCajas transferenciaGuardada = transferenciaEntreCajasService.guardarTransferenciasEntreCajas(transferencia);

    // Registrar transacción salida
    TransaccionesCaja transaccionSalida = new TransaccionesCaja();
    transaccionSalida.setAperturaCaja(aperturaOrigen);
    transaccionSalida.setTipoMovimiento("SALIDA");
    transaccionSalida.setMonto(dto.getMonto());
    transaccionSalida.setObservaciones("Transferencia a caja " + cajaDestino.getNombreCaja());
    transaccionCajaRepository.save(transaccionSalida);

    // Registrar transacción entrada
    TransaccionesCaja transaccionEntrada = new TransaccionesCaja();
    transaccionEntrada.setAperturaCaja(aperturaDestino);
    transaccionEntrada.setTipoMovimiento("ENTRADA");
    transaccionEntrada.setMonto(dto.getMonto());
    transaccionEntrada.setObservaciones("Transferencia desde caja " + cajaOrigen.getNombreCaja());
    transaccionCajaRepository.save(transaccionEntrada);

    // Actualizar saldos finales
    aperturaOrigen.setSaldoFinal(aperturaOrigen.getSaldoFinal() - dto.getMonto());
    aperturaDestino.setSaldoFinal(aperturaDestino.getSaldoFinal() + dto.getMonto());

    aperturaCajaRepository.save(aperturaOrigen);
    aperturaCajaRepository.save(aperturaDestino);

    return ResponseEntity.ok(transferenciaGuardada);
}

@PutMapping("/transferencia-entre-caja")
@Transactional
public ResponseEntity<?> editarTransferencias(@RequestBody TransferenciaEntreCajasDTO dto) {
    if (dto.getIdTransferenciaEntreCajas() == null) {
        return ResponseEntity.badRequest().body("Debe proporcionar el ID de la transferencia a editar.");
    }

    Optional<TransferenciasEntreCajas> transferenciaOpt = transferenciaEntreCajasService.buscarTranseferenciaEntreCajas(dto.getIdTransferenciaEntreCajas());
    if (transferenciaOpt.isEmpty()) {
        return ResponseEntity.badRequest().body("Transferencia no encontrada.");
    }

    TransferenciasEntreCajas transferenciaExistente = transferenciaOpt.get();

    // Buscar cajas nuevas
    Caja nuevaCajaOrigen = cajaRepository.findById(dto.getId_caja_origen()).orElse(null);
    Caja nuevaCajaDestino = cajaRepository.findById(dto.getId_caja_destino()).orElse(null);
    if (nuevaCajaOrigen == null || nuevaCajaDestino == null) {
        return ResponseEntity.badRequest().body("Una o ambas cajas nuevas no existen.");
    }

    // Buscar aperturas nuevas activas
    Optional<AperturaCaja> aperturaOrigenNuevaOpt = aperturaCajaRepository.findByCaja_IdCajaAndCaja_EstadoCaja(dto.getId_caja_origen(), "ABIERTO");
    Optional<AperturaCaja> aperturaDestinoNuevaOpt = aperturaCajaRepository.findByCaja_IdCajaAndCaja_EstadoCaja(dto.getId_caja_destino(), "ABIERTO");
    if (aperturaOrigenNuevaOpt.isEmpty() || aperturaDestinoNuevaOpt.isEmpty()) {
        return ResponseEntity.badRequest().body("Ambas cajas deben tener una apertura activa para la edición.");
    }

    AperturaCaja aperturaOrigenNueva = aperturaOrigenNuevaOpt.get();
    AperturaCaja aperturaDestinoNueva = aperturaDestinoNuevaOpt.get();

    // Revertir saldos anteriores
    double montoAnterior = transferenciaExistente.getMonto();
    Caja cajaOrigenAnterior = transferenciaExistente.getCajaOrigen();
    Caja cajaDestinoAnterior = transferenciaExistente.getCajaDestino();

    Optional<AperturaCaja> aperturaOrigenAntiguaOpt = aperturaCajaRepository.findByCaja_IdCajaAndCaja_EstadoCaja(cajaOrigenAnterior.getIdCaja(), "ABIERTO");
    Optional<AperturaCaja> aperturaDestinoAntiguaOpt = aperturaCajaRepository.findByCaja_IdCajaAndCaja_EstadoCaja(cajaDestinoAnterior.getIdCaja(), "ABIERTO");

    if (aperturaOrigenAntiguaOpt.isPresent() && aperturaDestinoAntiguaOpt.isPresent()) {
        AperturaCaja aperturaOrigenAntigua = aperturaOrigenAntiguaOpt.get();
        AperturaCaja aperturaDestinoAntigua = aperturaDestinoAntiguaOpt.get();

        aperturaOrigenAntigua.setSaldoFinal(aperturaOrigenAntigua.getSaldoFinal() + montoAnterior);
        aperturaDestinoAntigua.setSaldoFinal(aperturaDestinoAntigua.getSaldoFinal() - montoAnterior);

        aperturaCajaRepository.save(aperturaOrigenAntigua);
        aperturaCajaRepository.save(aperturaDestinoAntigua);
    }

    // Eliminar transacciones anteriores relacionadas
    List<TransaccionesCaja> transacciones = transaccionCajaRepository.findAll();
    for (TransaccionesCaja t : transacciones) {
        if ((t.getAperturaCaja().getIdAperturaCaja().equals(aperturaOrigenAntiguaOpt.map(AperturaCaja::getIdAperturaCaja).orElse(null)) &&
             t.getTipoMovimiento().equals("SALIDA") &&
             t.getMonto() == montoAnterior &&
             t.getObservaciones().contains(cajaDestinoAnterior.getNombreCaja()))
        || (t.getAperturaCaja().getIdAperturaCaja().equals(aperturaDestinoAntiguaOpt.map(AperturaCaja::getIdAperturaCaja).orElse(null)) &&
            t.getTipoMovimiento().equals("ENTRADA") &&
            t.getMonto() == montoAnterior &&
            t.getObservaciones().contains(cajaOrigenAnterior.getNombreCaja()))) {
            transaccionCajaRepository.delete(t);
        }
    }

    // Validar que la nueva caja origen tenga saldo suficiente
    if (aperturaOrigenNueva.getSaldoFinal() < dto.getMonto()) {
        return ResponseEntity.badRequest().body("La caja origen nueva no tiene saldo suficiente.");
    }

    // Registrar nuevas transacciones
    TransaccionesCaja nuevaSalida = new TransaccionesCaja();
    nuevaSalida.setAperturaCaja(aperturaOrigenNueva);
    nuevaSalida.setTipoMovimiento("SALIDA");
    nuevaSalida.setMonto(dto.getMonto());
    nuevaSalida.setObservaciones("Transferencia a caja " + nuevaCajaDestino.getNombreCaja());
    transaccionCajaRepository.save(nuevaSalida);

    TransaccionesCaja nuevaEntrada = new TransaccionesCaja();
    nuevaEntrada.setAperturaCaja(aperturaDestinoNueva);
    nuevaEntrada.setTipoMovimiento("ENTRADA");
    nuevaEntrada.setMonto(dto.getMonto());
    nuevaEntrada.setObservaciones("Transferencia desde caja " + nuevaCajaOrigen.getNombreCaja());
    transaccionCajaRepository.save(nuevaEntrada);

    // Actualizar nuevos saldos
    aperturaOrigenNueva.setSaldoFinal(aperturaOrigenNueva.getSaldoFinal() - dto.getMonto());
    aperturaDestinoNueva.setSaldoFinal(aperturaDestinoNueva.getSaldoFinal() + dto.getMonto());
    aperturaCajaRepository.save(aperturaOrigenNueva);
    aperturaCajaRepository.save(aperturaDestinoNueva);

    // Actualizar entidad transferencia
    transferenciaExistente.setCajaOrigen(nuevaCajaOrigen);
    transferenciaExistente.setCajaDestino(nuevaCajaDestino);
    transferenciaExistente.setFecha(dto.getFecha());
    transferenciaExistente.setMonto(dto.getMonto());
    transferenciaExistente.setMotivo(dto.getMotivo());
    transferenciaExistente.setObservaciones(dto.getObservaciones());

    transferenciaEntreCajasService.guardarTransferenciasEntreCajas(transferenciaExistente);

    return ResponseEntity.ok("Transferencia actualizada correctamente.");
}


@DeleteMapping("/transferencia-entre-caja/{id}")
@Transactional
public ResponseEntity<?> eliminarTransferenciaEntreCaja(@PathVariable Integer id) {
    // Buscar la transferencia
    Optional<TransferenciasEntreCajas> transferenciaOpt = transferenciaEntreCajasService.buscarTranseferenciaEntreCajas(id);
    if (transferenciaOpt.isEmpty()) {
        return ResponseEntity.badRequest().body("Transferencia no encontrada.");
    }

    TransferenciasEntreCajas transferencia = transferenciaOpt.get();
    double monto = transferencia.getMonto();
    Caja cajaOrigen = transferencia.getCajaOrigen();
    Caja cajaDestino = transferencia.getCajaDestino();

    // Buscar aperturas activas de ambas cajas
    Optional<AperturaCaja> aperturaOrigenOpt = aperturaCajaRepository.findByCaja_IdCajaAndCaja_EstadoCaja(cajaOrigen.getIdCaja(), "ABIERTO");
    Optional<AperturaCaja> aperturaDestinoOpt = aperturaCajaRepository.findByCaja_IdCajaAndCaja_EstadoCaja(cajaDestino.getIdCaja(), "ABIERTO");

    if (aperturaOrigenOpt.isEmpty() || aperturaDestinoOpt.isEmpty()) {
        return ResponseEntity.badRequest().body("Ambas cajas deben tener una apertura activa para revertir la transferencia.");
    }

    AperturaCaja aperturaOrigen = aperturaOrigenOpt.get();
    AperturaCaja aperturaDestino = aperturaDestinoOpt.get();

    // Eliminar transacciones relacionadas (opcionalmente podrías buscar por observación o relacionarlas con la transferencia)
    List<TransaccionesCaja> transacciones = transaccionCajaRepository.findAll(); // Considera implementar una búsqueda más específica
    for (TransaccionesCaja t : transacciones) {
        if ((t.getAperturaCaja().getIdAperturaCaja().equals(aperturaOrigen.getIdAperturaCaja()) && t.getTipoMovimiento().equals("SALIDA") && t.getMonto() == monto && t.getObservaciones().contains(cajaDestino.getNombreCaja()))
            || (t.getAperturaCaja().getIdAperturaCaja().equals(aperturaDestino.getIdAperturaCaja()) && t.getTipoMovimiento().equals("ENTRADA") && t.getMonto() == monto && t.getObservaciones().contains(cajaOrigen.getNombreCaja()))) {
            transaccionCajaRepository.delete(t);
        }
    }

    // Revertir saldos
    aperturaOrigen.setSaldoFinal(aperturaOrigen.getSaldoFinal() + monto);
    aperturaDestino.setSaldoFinal(aperturaDestino.getSaldoFinal() - monto);
    aperturaCajaRepository.save(aperturaOrigen);
    aperturaCajaRepository.save(aperturaDestino);

    // Eliminar la transferencia
    transferenciaEntreCajasService.eliminarTransferenciaEntreCajas(id);

    return ResponseEntity.ok("La transferencia entre cajas ha sido eliminada y los saldos fueron actualizados correctamente.");
}

}