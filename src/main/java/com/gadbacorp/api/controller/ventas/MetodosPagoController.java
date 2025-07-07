package com.gadbacorp.api.controller.ventas;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

import com.gadbacorp.api.entity.administrable.Sucursales;
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
    @PostMapping("/metodos-pago")
    public ResponseEntity<?> guardarMetodoPago(@RequestBody MetodosPago metodoPago) {
        if (metodosPagoService.existeMetodoConNombre(metodoPago.getNombre())) {
            return ResponseEntity.badRequest().body("Ya existe un método de pago con ese nombre.");
        }
        return ResponseEntity.ok(metodosPagoService.guardarMetodoPago(metodoPago));
    }

    @GetMapping("/metodos-pago/{id}")
    public Optional<MetodosPago> buscarId(@PathVariable("id") Integer id){
        return metodosPagoService.obtenerMetodoPago(id);
    }

@PutMapping("/metodos-pago")
public ResponseEntity<Map<String, Object>> modificar(@RequestBody MetodosPago metodoPago) {
    metodosPagoService.editarMetodosPago(metodoPago);
    
    Map<String, Object> respuesta = new HashMap<>();
    respuesta.put("status", 200);
    respuesta.put("Detalle", "Método de pago actualizado");
    
    return ResponseEntity.ok(respuesta);
}

   @DeleteMapping("/metodos-pago/{id}")
public ResponseEntity<Map<String, Object>> eliminar(@PathVariable Integer id) {
    metodosPagoService.eliminarMetodoPago(id);
    
    Map<String, Object> respuesta = new HashMap<>();
    respuesta.put("status", 200);
    respuesta.put("Detalle", "Método de pago eliminado");
    
    return ResponseEntity.ok(respuesta);
}

    @GetMapping("/sucursales/{idSucursal}/metodos-pago")
public List<MetodosPago> listarPorSucursal(@PathVariable Integer idSucursal) {
    return metodosPagoService.listarPorSucursal(idSucursal);
}

@PostMapping("/sucursales/{idSucursal}/metodos-pago")
public ResponseEntity<Map<String, Object>> guardarMetodoPagoEnSucursal(
        @PathVariable Integer idSucursal,
        @RequestBody MetodosPago metodoPago) {

    // Asignar la sucursal al método
    Sucursales sucursal = new Sucursales();
    sucursal.setIdSucursal(idSucursal);
    metodoPago.setSucursal(sucursal);

    Map<String, Object> respuesta = new HashMap<>();
    try {
        metodosPagoService.guardarMetodoPago(metodoPago);
        respuesta.put("status", 200);
        respuesta.put("Detalle", "Método de pago guardado");
        return ResponseEntity.ok(respuesta);
    } catch (IllegalArgumentException e) {
        respuesta.put("status", 400);
        respuesta.put("Detalle", e.getMessage());
        return ResponseEntity.badRequest().body(respuesta);
    } catch (Exception e) {
        respuesta.put("status", 500);
        respuesta.put("Detalle", "Error inesperado al guardar el método de pago.");
        return ResponseEntity.status(500).body(respuesta);
    }
}

}
