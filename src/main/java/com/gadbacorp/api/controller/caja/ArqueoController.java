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
    public List<ArqueoCaja> listarArqueos() {
        return arqueoCajaService.listarArqueo();
    }

    @GetMapping("/arqueos-cajas/{id}")
    public ResponseEntity<?> buscarArqueoCaja(@PathVariable Integer id) {
        Optional<ArqueoCaja> arqueo = arqueoCajaService.bsucarArqueo(id);
        return arqueo.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    @PostMapping("/arqueos-cajas")
    public ResponseEntity<?> guardarArqueoCaja(@RequestBody ArqueoCajaDTO dto) {
        AperturaCaja aperturaCaja = aperturaCajaRepository.findById(dto.getId_apertura_caja()).orElse(null);
        if (aperturaCaja == null) {
            return ResponseEntity.badRequest().body("No se encontró la apertura de caja con ID: " + dto.getId_apertura_caja());
        }

        // Crear el arqueo
        ArqueoCaja arqueoCaja = new ArqueoCaja();
        arqueoCaja.setFechaArqueo(dto.getFechaArqueo());
        arqueoCaja.setSaldoSistema(dto.getSaldoSistema());
        arqueoCaja.setObservaciones(dto.getObservaciones());
        arqueoCaja.setAperturaCaja(aperturaCaja);

        return ResponseEntity.ok(arqueoCajaService.guardarArqueoCaja(arqueoCaja));
    }


    @PutMapping("/arqueos-cajas")
    public ResponseEntity<?> editarArqueoCaja(@RequestBody ArqueoCajaDTO dto) {
        if (dto.getIdArqueoCaja() == null) {
            return ResponseEntity.badRequest().body("El ID del arqueo es obligatorio para editar.");
        }

        AperturaCaja aperturaCaja = aperturaCajaRepository.findById(dto.getId_apertura_caja()).orElse(null);
        if (aperturaCaja == null) {
            return ResponseEntity.badRequest().body("No se encontró la apertura de caja con ID: " + dto.getId_apertura_caja());
        }

        ArqueoCaja arqueoCaja = new ArqueoCaja();
        arqueoCaja.setIdArqueoCaja(dto.getIdArqueoCaja());
        arqueoCaja.setFechaArqueo(dto.getFechaArqueo());
        arqueoCaja.setSaldoSistema(dto.getSaldoSistema());
        arqueoCaja.setObservaciones(dto.getObservaciones());
        arqueoCaja.setEstado(dto.getEstado());
        arqueoCaja.setAperturaCaja(aperturaCaja);

        return ResponseEntity.ok(arqueoCajaService.editarArqueoCaja(arqueoCaja));
    }

    @DeleteMapping("/arqueos-cajas/{id}")
    public ResponseEntity<String> eliminarArqueo(@PathVariable Integer id) {
        arqueoCajaService.eliminarArqueo(id);
        return ResponseEntity.ok("El arqueo de caja ha sido eliminado con éxito.");
    }
}
