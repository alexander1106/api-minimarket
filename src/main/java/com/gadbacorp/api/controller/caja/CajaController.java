package com.gadbacorp.api.controller.caja;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

import com.gadbacorp.api.entity.administrable.Sucursales;
import com.gadbacorp.api.entity.caja.Caja;
import com.gadbacorp.api.entity.caja.CajaDTO;
import com.gadbacorp.api.repository.administrable.SucursalesRepository;
import com.gadbacorp.api.repository.caja.CajaRepository;
import com.gadbacorp.api.service.caja.ICajaService;

@RestController
@RequestMapping("/api/minimarket")
@CrossOrigin("*")
public class CajaController {
    @Autowired
    private ICajaService cajaService;
    @Autowired
    private SucursalesRepository sucursalesRepository;
      @Autowired
    private CajaRepository cajaRepository;
    
    @GetMapping("/cajas")
    public List<Caja> listarCajas() {
        return cajaService.listarCaja();
    }
    
    @GetMapping("/cajas/{id}")
    public Optional<Caja> buscarCaja(@PathVariable Integer id) {
        return cajaService.buscarCaja(id);
    }

    @GetMapping("/cajas/{idCaja}/sucursal")
    public ResponseEntity<Sucursales> obtenerSucursalPorCaja(@PathVariable Integer idCaja) {
        return cajaRepository.findById(idCaja)
                .map(caja -> ResponseEntity.ok(caja.getSucursales()))
                .orElse(ResponseEntity.notFound().build());
    }
    @GetMapping("/cajas/{idCaja}/sucursal-cajas-abiertas")
public ResponseEntity<List<Caja>> obtenerCajasAbiertasMismaSucursal(@PathVariable Integer idCaja) {
    return cajaRepository.findById(idCaja)
        .map(caja -> {
            Sucursales sucursal = caja.getSucursales();
            List<Caja> cajasAbiertas = cajaRepository.findBySucursalesAndEstadoCaja(sucursal, "OCUPADA");
            return ResponseEntity.ok(cajasAbiertas);
        })
        .orElse(ResponseEntity.notFound().build());
}




    @PutMapping("/cajas")
    public ResponseEntity<?> actualizar( @RequestBody CajaDTO dto) {
    Sucursales sucursales = sucursalesRepository.findById(dto.getIdSucursal()).orElse(null);
         if (sucursales == null) {
            return ResponseEntity.badRequest().body("Sucursal no encontrado con ID: " + dto.getIdSucursal());
        }
        
        Caja caja = new Caja();
        caja.setIdCaja(dto.getIdCaja());
        caja.setEstadoCaja(dto.getEstadoCaja());
        caja.setNombreCaja(dto.getNombreCaja());
        caja.setSaldoActual(dto.getSaldoActual());
        caja.setSucursales(sucursales);
        caja.setEstado(dto.getEstado());
        
        return ResponseEntity.ok(cajaService.guardarCaja(caja));
    }

@PostMapping("/cajas")
public ResponseEntity<?> guardarCaja(@RequestBody CajaDTO dto) {
    Sucursales sucursales = sucursalesRepository.findById(dto.getIdSucursal()).orElse(null);
    if (sucursales == null) {
        return ResponseEntity.badRequest().body("Sucursal no encontrada con ID: " + dto.getIdSucursal());
    }

    // Verificar duplicado
    boolean existe = cajaRepository.existsByNombreCajaAndSucursales_IdSucursal(
        dto.getNombreCaja().trim(),
        dto.getIdSucursal()
    );
    if (existe) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "CAJA_DUPLICADA");
        response.put("mensaje", "Ya existe una caja con ese nombre en esta sucursal.");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    Caja caja = new Caja();
    caja.setEstadoCaja(dto.getEstadoCaja());
    caja.setNombreCaja(dto.getNombreCaja());
    caja.setSaldoActual(dto.getSaldoActual());
    caja.setSucursales(sucursales);
    caja.setEstado(dto.getEstado());

    return ResponseEntity.ok(cajaService.guardarCaja(caja));
}
 
   @DeleteMapping("/cajas/{id}")
public ResponseEntity<?> eliminarCaja(@PathVariable Integer id) {
    Optional<Caja> optionalCaja = cajaRepository.findById(id);
    if (!optionalCaja.isPresent()) {
        return ResponseEntity.notFound().build();
    }

    Caja caja = optionalCaja.get();

    // Validar si tiene aperturas relacionadas
    if (caja.getAperturaCajas() != null && !caja.getAperturaCajas().isEmpty()) {
        return ResponseEntity
                .badRequest()
                .body("No se puede eliminar la caja porque tiene aperturas relacionadas.");
    }

    cajaService.eliminarCaja(id);
    return ResponseEntity.ok("La caja ha sido eliminada con éxito.");
}

}
