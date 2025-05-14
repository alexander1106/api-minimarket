package com.gadbacorp.api.controller.ventas;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
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
public class MetodosPagoController {

    @Autowired
    private IMetodosPagoService metodosPagoService;

    @GetMapping("/metodos-pago")
    public List<MetodosPago> buscarTodos() {
        return metodosPagoService.listarMetodosPago();
    }
    @PostMapping("/metodos-pago")
    public MetodosPago guardar(@RequestBody MetodosPago metodoPago) {
        metodosPagoService.guardarMetodoPago(metodoPago);
        return metodoPago;
    }
    @PutMapping("/metodos-pago")
    public MetodosPago modificar(@RequestBody MetodosPago metodoPago) {
        metodosPagoService.guardarMetodoPago(metodoPago);
        return metodoPago;
    }
    @GetMapping("/metodos-pago/{id}")
    public Optional<MetodosPago> buscarId(@PathVariable("id") Integer id){
        return metodosPagoService.obtenerMetodoPago(id);
    }
    @DeleteMapping("/metodos-pago/{id}")
    public String eliminar(@PathVariable Integer id){
        metodosPagoService.eliminarMetodoPago(id);
        return "Metodo de pago eliminado";
    }

}
