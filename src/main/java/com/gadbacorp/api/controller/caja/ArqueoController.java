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
import com.gadbacorp.api.entity.caja.ArqueoCaja;
import com.gadbacorp.api.entity.caja.ArqueoCajaDTO;
import com.gadbacorp.api.repository.caja.AperturaCajaRepository;
import com.gadbacorp.api.service.caja.IArqueoCajaService;

@RestController
@RequestMapping("/api/minimarket")
@CrossOrigin("*")
public class ArqueoController {

    @Autowired
    private IArqueoCajaService arqueoCajaService;
    @Autowired
    private AperturaCajaRepository aperturaCajaRepository;

    @GetMapping("/arqueos-cajas")
    public List<ArqueoCaja> listarArqueos () {
        return arqueoCajaService.listarArqueo();
    }

    @GetMapping("/arqueo-caja/{id}")
    public Optional<ArqueoCaja> buscarArqueoCaja(@PathVariable Integer id){
        return arqueoCajaService.bsucarArqueo(id);
    }

    @PostMapping("/arqueo-caja")
    public ResponseEntity<?> guardarArqueoCaja(@RequestBody ArqueoCajaDTO dto){
        AperturaCaja aperturaCaja = aperturaCajaRepository.findById(dto.getId_apertura_caja()).orElse(null);

        ArqueoCaja arqueoCaja= new ArqueoCaja(); 
        arqueoCaja.setEgresosTotales(dto.getEgresosTotales());
        arqueoCaja.setDiferencia(dto.getDiferencia());
        arqueoCaja.setEgresosTotales(dto.getEgresosTotales());
        arqueoCaja.setFechaArqueo(dto.getFechaArqueo());
        arqueoCaja.setObservaciones(dto.getObservaciones());
        arqueoCaja.setSaldoInciial(dto.getSaldoInciial());
        arqueoCaja.setSaldoReal(dto.getSaldoReal());
        arqueoCaja.setSaldoSistema(dto.getSaldoSistema());
        arqueoCaja.setAperturaCaja(aperturaCaja);
        return ResponseEntity.ok(arqueoCajaService.guardarArqueoCaja(arqueoCaja));
    }

    @PutMapping("/arqueo-caja")
    public ResponseEntity<?> editarArqueoCaja(@RequestBody ArqueoCajaDTO dto){
        if(dto.getIdArqueoCaja()==null){
            return ResponseEntity.badRequest().body("El id no exite");
        }
        ArqueoCaja arqueoCaja= new ArqueoCaja(); 
        arqueoCaja.setIdArqueoCaja(dto.getIdArqueoCaja());
        arqueoCaja.setEgresosTotales(dto.getEgresosTotales());
        arqueoCaja.setDiferencia(dto.getDiferencia());
        arqueoCaja.setEgresosTotales(dto.getEgresosTotales());
        arqueoCaja.setFechaArqueo(dto.getFechaArqueo());
        arqueoCaja.setObservaciones(dto.getObservaciones());
        arqueoCaja.setSaldoInciial(dto.getSaldoInciial());
        arqueoCaja.setSaldoReal(dto.getSaldoReal());
        arqueoCaja.setSaldoSistema(dto.getSaldoSistema());
        arqueoCaja.setAperturaCaja(new AperturaCaja(dto.getId_apertura_caja()));
        return ResponseEntity.ok(arqueoCajaService.editarArqueoCaja(arqueoCaja));
    }
    @DeleteMapping("/arqueo-caja/{id}")
    public String eliminarArqueo(@PathVariable Integer id){
         arqueoCajaService.eliminarArqueo(id);
         return "El arqueo de caja a sido eliminado con exito";
    }
}
