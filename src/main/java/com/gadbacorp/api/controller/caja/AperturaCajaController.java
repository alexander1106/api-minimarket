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
import com.gadbacorp.api.entity.caja.AperturaCajaDTO;
import com.gadbacorp.api.entity.caja.Caja;
import com.gadbacorp.api.entity.empleados.Empleado;
import com.gadbacorp.api.repository.caja.CajaRepository;
import com.gadbacorp.api.repository.empleados.EmpleadosRepository;
import com.gadbacorp.api.service.caja.IAperturaCajaService;

@RestController
@RequestMapping("/api/minimarket")
@CrossOrigin    
public class AperturaCajaController {

    @Autowired
    private IAperturaCajaService aperturaCajaService;

    @Autowired
    private EmpleadosRepository empleadosRepository;

    @Autowired
    private CajaRepository cajaRepository;
    @GetMapping("/aperturas-cajas")
    public List<AperturaCaja> listarAperturasCajas() {
        return aperturaCajaService.listarAperturaCajas();
    }
    
    @GetMapping("/apertura-caja/{id}")
    public Optional<AperturaCaja> buscarCaja(@PathVariable Integer id) {
        return aperturaCajaService.buscarAperturaCaja(id);
    }
    
    @PostMapping("/apertura-caja")
    public ResponseEntity<?> guardarAperturaCaja(@RequestBody AperturaCajaDTO dto) {    
        Empleado empleado = empleadosRepository.findById(dto.getId_empleado()).orElse(null);
        Caja caja = cajaRepository.findById(dto.getId_caja()).orElse(null);
        AperturaCaja aperturaCaja = new AperturaCaja();
        aperturaCaja.setFechaApertura(dto.getFechaApertura());
        aperturaCaja.setFechaCierre(dto.getFechaCierre());
        aperturaCaja.setSaldoInicial(dto.getSaldoInicial());
        aperturaCaja.setSaldoFinal(dto.getSaldoFinal());
        aperturaCaja.setEmpleado(empleado);
        aperturaCaja.setCaja(caja);
        return ResponseEntity.ok(aperturaCajaService.guardarAperturaCaja(aperturaCaja));
    }

    @PutMapping("/apetura-caja")
    public ResponseEntity<?> modificarAperturaCaja(@RequestBody AperturaCajaDTO dto) {
        if(dto.getIdAperturaCaja()==null){
            return ResponseEntity.badRequest().body("El id no exite");
        }
        AperturaCaja aperturaCaja = new AperturaCaja();
        aperturaCaja.setFechaApertura(dto.getFechaApertura());
        aperturaCaja.setFechaCierre(dto.getFechaCierre());
        aperturaCaja.setSaldoInicial(dto.getSaldoInicial());
        aperturaCaja.setSaldoFinal(dto.getSaldoFinal());
        aperturaCaja.setEmpleado(new Empleado(dto.getId_empleado()));
        aperturaCaja.setCaja(new Caja(dto.getId_caja()));
        return ResponseEntity.ok(aperturaCajaService.editarAperturaCaja(aperturaCaja));
    }

    @DeleteMapping("/apertura-caja/{id}")
    public String eliminarAperturaCaja(@PathVariable Integer id){
        aperturaCajaService.eliminarAperturaCaja(id);
        return "La apertura de caja  a sido eliminad con exito";    
    }
}
