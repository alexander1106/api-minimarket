package com.gadbacorp.api.controller.ventas;

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

import com.gadbacorp.api.entity.ventas.Clientes;
import com.gadbacorp.api.entity.ventas.Ventas;
import com.gadbacorp.api.entity.ventas.VentasDTO;
import com.gadbacorp.api.repository.ventas.ClientesRepository;
import com.gadbacorp.api.service.ventas.IVentasService;


@RestController
@RequestMapping("/api/minimarket")
@CrossOrigin("*")
public class VentasController {

    @Autowired
    private IVentasService ventasService;


    @Autowired
    private ClientesRepository clientesRepository;
     @GetMapping("/ventas")
    public List<Ventas> buscarTodos() {
        return ventasService.listarVentaas();
    }
    @PostMapping("/venta")
    public ResponseEntity<?> guardarVenta(@RequestBody VentasDTO dto) {
        Clientes cliente = clientesRepository.findById(dto.getId_cliente()).orElse(null);

        if (cliente == null) {
            return ResponseEntity.badRequest().body("Cliente no encontrado con ID: " + dto.getId_cliente());
        }
        Ventas venta = new Ventas();
        venta.setTotal_venta(dto.getTotal_venta());
        venta.setTipo_comprobante(dto.getTipo_comprobante());
        venta.setNro_comrprobante(dto.getNro_comrprobante());
        venta.setFecha_venta(dto.getFecha_venta());    
        venta.setCliente(cliente); // Primero asigna el cliente
        return ResponseEntity.ok(ventasService.guardarVenta(venta));
    }

    @PutMapping("/venta")
    public ResponseEntity <?> modificar(@RequestBody VentasDTO dto) {
        if(dto.getIdVenta() == null){
            return ResponseEntity.badRequest().body("Id no existe");
        }
        Ventas venta = new Ventas();
        venta.setIdVenta(dto.getIdVenta());

                venta.setTotal_venta(dto.getTotal_venta());
        venta.setTipo_comprobante(dto.getTipo_comprobante());
        venta.setNro_comrprobante(dto.getNro_comrprobante());
        venta.setFecha_venta(dto.getFecha_venta());    
        venta.setCliente(new Clientes(dto.getId_cliente())); // Primero asigna el cliente
        return ResponseEntity.ok(ventasService.editarVenta(venta));    
    }
    
   

    // Eliminar venta
    @DeleteMapping("/venta/{id}")
    public String eliminarVenta(@PathVariable Integer id) {
        ventasService.eliminarVenta(id);
        return "La venta a sido eliminada con exito";
    }

    // Buscar venta por ID
    @GetMapping("/venta/{id}")
    public Optional<Ventas> buscarVenta(@PathVariable Integer id) {
        return ventasService.buscarVenta(id);
    }
   
    
}