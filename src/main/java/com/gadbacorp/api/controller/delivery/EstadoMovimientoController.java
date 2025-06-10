package com.gadbacorp.api.controller.delivery;

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

import com.gadbacorp.api.entity.delivery.EstadoMovimiento;
import com.gadbacorp.api.service.delivery.IEstadoMovimientoService;

@RestController
@RequestMapping("/api/minimarket")
public class EstadoMovimientoController {

    @Autowired
    private IEstadoMovimientoService service;

    @GetMapping("/estadomovimiento")
    public List<EstadoMovimiento> buscarTodos() {
        return service.buscarTodos();
    }

    @GetMapping("/estadomovimiento/{id}")
    public Optional<EstadoMovimiento> buscarId(@PathVariable Integer id) {
        return service.buscarId(id);
    }

    @PostMapping("/estadomovimiento")
    public EstadoMovimiento guardar(@RequestBody EstadoMovimiento estadoMovimiento) {
        return service.guardar(estadoMovimiento);
    }

    @PutMapping("/estadomovimiento")
    public EstadoMovimiento modificar(@RequestBody EstadoMovimiento estadoMovimiento) {
        return service.modificar(estadoMovimiento);
    }

    @DeleteMapping("/estadomovimiento/{id}")
    public String eliminar(@PathVariable Integer id) {
        service.eliminar(id);
        return "EstadoMovimiento eliminado";
    }
}
