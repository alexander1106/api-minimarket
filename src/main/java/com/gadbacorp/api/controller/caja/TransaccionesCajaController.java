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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.gadbacorp.api.entity.caja.AperturaCaja;
import com.gadbacorp.api.entity.caja.TransaccionesCaja;
import com.gadbacorp.api.entity.caja.TransaccionesCajaDTO;
import com.gadbacorp.api.entity.empleados.Empleado;
import com.gadbacorp.api.repository.caja.AperturaCajaRepository;
import com.gadbacorp.api.service.caja.ITransaccionesCajaServices;
import com.sun.source.tree.GuardedPatternTree;

@RestController
@RequestMapping("/api/minimarket")
@CrossOrigin("*")
public class TransaccionesCajaController {
        
    @Autowired
    private ITransaccionesCajaServices transaccionesCajaService;

    @Autowired
    private AperturaCajaRepository aperturaCajaRepository;
    @GetMapping("transacciones-cajas")
    public List<TransaccionesCaja> listarTransaccionesCajas(){
        return transaccionesCajaService.listarTransaccionesCajas();
    }

    @GetMapping("/transaccion-caja/{id}")
    public Optional<TransaccionesCaja> buscarTransaccion(@PathVariable Integer id){
        return transaccionesCajaService.buscarTransaccion(id);
    }

    @PostMapping("/transaccion-caja")
    public ResponseEntity<?> guardarTransaccion(@RequestBody TransaccionesCajaDTO dto){
        AperturaCaja aperturaCaja = aperturaCajaRepository.findById(dto.getId_apertura_caja()).orElse(null);
        TransaccionesCaja transaccionesCaja = new TransaccionesCaja();
        transaccionesCaja.setFecha(dto.getFecha());
        transaccionesCaja.setConcepto(dto.getConcepto());
        transaccionesCaja.setMonto(dto.getMonto());
        transaccionesCaja.setObservaciones(dto.getObservaciones());
        transaccionesCaja.setTipoMovimiento(dto.getTipoMovimiento());
        transaccionesCaja.setAperturaCaja(aperturaCaja);
        return ResponseEntity.ok(transaccionesCajaService.guardarTransaccion(transaccionesCaja));
    }

    @PutMapping("/transacciones-caja")
    public ResponseEntity<?> editarTransaccion(@RequestBody TransaccionesCajaDTO dto){
        if(dto.getIdTransaccionesCaja()==null){
            return ResponseEntity.badRequest().body("El id no exite");
        }
        TransaccionesCaja transaccionesCaja = new TransaccionesCaja();
        transaccionesCaja.setIdTransaccionesCaja(dto.getIdTransaccionesCaja());
        transaccionesCaja.setFecha(dto.getFecha());
        transaccionesCaja.setConcepto(dto.getConcepto());
        transaccionesCaja.setMonto(dto.getMonto());
        transaccionesCaja.setObservaciones(dto.getObservaciones());
        transaccionesCaja.setTipoMovimiento(dto.getTipoMovimiento());
        transaccionesCaja.setAperturaCaja(new AperturaCaja(dto.getId_apertura_caja()));
        return ResponseEntity.ok(transaccionesCajaService.editarTransacciones(transaccionesCaja));
    }

    @DeleteMapping("/transaccion-caja/{íd}")
    public String elimianrCaja(@PathVariable Integer id){
        transaccionesCajaService.eliminarTransacciones(id);
        return "La transaccion de esta caja a sido eliminada"; 
    }
}