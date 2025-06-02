package com.gadbacorp.api.controller.ventas;

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

import com.gadbacorp.api.entity.ventas.Clientes;
import com.gadbacorp.api.entity.ventas.Cotizaciones;
import com.gadbacorp.api.entity.ventas.CotizacionesDTO;
import com.gadbacorp.api.repository.ventas.ClientesRepository;
import com.gadbacorp.api.service.ventas.ICotizacionesService;

@RestController
@RequestMapping("/api/minimarket")
public class CotizacionController {
    
    @Autowired
    private ICotizacionesService cotizacionesService;

    @Autowired
    private ClientesRepository clientesRepository;
    
    @GetMapping("/cotizaciones")
    public List<Cotizaciones> buscarTodos() {
        return cotizacionesService.listarCotizacioneses();
    }
    
    @GetMapping("/cotizacion/{id}")
    public Optional<Cotizaciones> buscarCotizaciones(@PathVariable Integer id) {
        return cotizacionesService.buscarCotizacion(id);
    }

    @PostMapping("/cotizacion")
    public ResponseEntity<?> guardarCotizacion(@RequestBody CotizacionesDTO dto) {
        
        Clientes cliente = clientesRepository.findById(dto.getId_cliente()).orElse(null);
        if (cliente == null) {
            return ResponseEntity.badRequest().body("Cliente no encontrado con ID: " + dto.getId_cliente());
        }
        Cotizaciones cotizaciones = new Cotizaciones();
        cotizaciones.setFechaCotizacion(dto.getFechaCotizacion());
        cotizaciones.setEstadoCotizacion(dto.getEstadoCotizacion());
        cotizaciones.setNumeroCotizacion(dto.getNumeroCotizacion());
        cotizaciones.setTotalCotizacion(dto.getTotalCotizacion());
        cotizaciones.setCliente(cliente); // Primero asigna el cliente
        return ResponseEntity.ok(cotizacionesService.guardarCotizacion(cotizaciones));
    }

  @PutMapping("/cotizacion")
    public ResponseEntity <?> modificar(@RequestBody CotizacionesDTO dto) {
        if(dto.getIdCotizaciones() == null){
            return ResponseEntity.badRequest().body("Id no existe");
        }
         Cotizaciones cotizaciones = new Cotizaciones();
        cotizaciones.setIdCotizaciones(dto.getIdCotizaciones());
        cotizaciones.setFechaCotizacion(dto.getFechaCotizacion());
        cotizaciones.setEstadoCotizacion(dto.getEstadoCotizacion());
        cotizaciones.setNumeroCotizacion(dto.getNumeroCotizacion());

        cotizaciones.setCliente(new Clientes(dto.getId_cliente())); // Primero asigna el cliente
        return ResponseEntity.ok(cotizacionesService.editarCotizaciones(cotizaciones));    
    }
    
    // Eliminar venta
    @DeleteMapping("/cotizacion/{id}")
    public String eliminarCotizacion(@PathVariable Integer id) {
        cotizacionesService.eliminarCotizaciones(id);
        return "La cotizacion a sido eliminada con exito";
    }


}
