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

import com.gadbacorp.api.entity.caja.Caja;
import com.gadbacorp.api.entity.caja.TransferenciaEntreCajasDTO;
import com.gadbacorp.api.entity.caja.TransferenciasEntreCajas;
import com.gadbacorp.api.repository.caja.CajaRepository;
import com.gadbacorp.api.service.caja.ITransferenciaEntreCajasService;

@RestController
@RequestMapping("/api/minimarket")
@CrossOrigin("*")
public class TransferenciaEntreCajasController {
    
    @Autowired
    private ITransferenciaEntreCajasService transferenciaEntreCajasService;

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
    public ResponseEntity<?> guardarTranseferenciaEntreCaja(@RequestBody TransferenciaEntreCajasDTO dto){
        Caja cajaOrigen= cajaRepository.findById(dto.getId_caja_origen()).orElse(null);
        Caja cajaDestin = cajaRepository.findById(dto.getId_caja_destino()).orElse(null);

        TransferenciasEntreCajas transferenciasEntreCajas =new TransferenciasEntreCajas();
        transferenciasEntreCajas.setCajaOrigen(cajaOrigen);
        transferenciasEntreCajas.setCajaDestino(cajaDestin);
        transferenciasEntreCajas.setFecha(dto.getFecha());
        transferenciasEntreCajas.setMonto(dto.getMonto());
        transferenciasEntreCajas.setMotivo(dto.getMotivo());
        transferenciasEntreCajas.setObservaciones(dto.getObservaciones());
        return ResponseEntity.ok(transferenciaEntreCajasService.guardarTransferenciasEntreCajas(transferenciasEntreCajas));
    }

    @PutMapping("/transferencia-entre-caja")
    public ResponseEntity<?> editarTransferencias(@RequestBody TransferenciaEntreCajasDTO dto){
    if(dto.getIdTransferenciaEntreCajas()==null){
        return ResponseEntity.badRequest().body("El id no existe");
    }
    TransferenciasEntreCajas transferenciasEntreCajas = new TransferenciasEntreCajas(); 
    transferenciasEntreCajas.setIdTransferenciaEntreCajas(dto.getIdTransferenciaEntreCajas());
    transferenciasEntreCajas.setCajaOrigen(new Caja(dto.getId_caja_origen()));
        transferenciasEntreCajas.setCajaDestino(new Caja(dto.getId_caja_destino()));
        transferenciasEntreCajas.setFecha(dto.getFecha());
        transferenciasEntreCajas.setMonto(dto.getMonto());
        transferenciasEntreCajas.setMotivo(dto.getMotivo());
        return ResponseEntity.ok(transferenciaEntreCajasService.editarTransferenciasEntreCajas(transferenciasEntreCajas));
    }

    @DeleteMapping("/transferencia-entre-caja/{id}")
    public String eliminarTransferenciaEntreCaja(@PathVariable Integer id){
        transferenciaEntreCajasService.eliminarTransferenciaEntreCajas(id);
        return "La tranferencia entre cajas a sifo eliminada con exitot"; 
        
    }
}