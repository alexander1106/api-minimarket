package com.gadbacorp.api.controller.caja;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.gadbacorp.api.service.caja.ICajaService;

@RestController
@RequestMapping("/api/minimarket")
@CrossOrigin("*")
public class CajaController {
    @Autowired
    private ICajaService cajaService;
    
    @GetMapping("/cajas")
    public List<Caja> listarCajas() {
        return cajaService.listarCaja();
    }
    
    @GetMapping("/caja/{id}")
    public Optional<Caja> buscarCaja(@PathVariable Integer id) {
        return cajaService.buscarCaja(id);
    }
    @PostMapping("/caja")
    public Caja guardaarCaja(@RequestBody Caja caja) {
        return cajaService.guardarCaja(caja);
    }
    @PutMapping("/caja")
    public Caja actualizarCaja(@RequestBody Caja caja) {
        return cajaService.editarCaja(caja);
    }
    @DeleteMapping("/caja/{id}")
    public String elimianrCaja(@PathVariable Integer id){
         cajaService.eliminarCaja(id);
         return "La caja a sido eliminad con exito";    
    }
}
