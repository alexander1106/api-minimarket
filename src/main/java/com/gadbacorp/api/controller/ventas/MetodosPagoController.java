package com.gadbacorp.api.controller.ventas;

import java.util.List;
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

import com.gadbacorp.api.entity.ventas.MetodosPago;
import com.gadbacorp.api.service.ventas.IMetodosPagoService;

@RestController
@RequestMapping("/api/minimarket")
@CrossOrigin("*")
public class MetodosPagoController {

    @Autowired
    private IMetodosPagoService metodosPagoService;

    @GetMapping("/metodos-pago")
    public List<MetodosPago> buscarTodos() {
        return metodosPagoService.listarMetodosPago();
    }
   @PostMapping("/metodo-pago")
    public ResponseEntity<MetodosPago> crearMetodoPago(@RequestBody MetodosPago metodoPago) {
        MetodosPago nuevo = metodosPagoService.guardarMetodoPago(metodoPago);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

   
    @GetMapping("/metodo-pago/{id}")
    public Optional<MetodosPago> buscarId(@PathVariable("id") Integer id){
        return metodosPagoService.obtenerMetodoPago(id);
    }

    @PutMapping("/metodo-pago")
    public ResponseEntity<?> modificar(@RequestBody MetodosPago metodoPago) {
        if (metodosPagoService.tieneRelaciones(metodoPago.getIdMetodoPago())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("No se puede modificar: el método de pago tiene registros relacionados.");
        }

        MetodosPago actualizado = metodosPagoService.guardarMetodoPago(metodoPago);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/metodo-pago/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Integer id){
        if (metodosPagoService.tieneRelaciones(id)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("No se puede eliminar: el método de pago tiene registros relacionados.");
        }

        metodosPagoService.eliminarMetodoPago(id);
        return ResponseEntity.ok("Método de pago eliminado");
    }
    
}
