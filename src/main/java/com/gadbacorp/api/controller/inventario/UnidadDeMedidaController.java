package com.gadbacorp.api.controller.inventario;

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

import com.gadbacorp.api.entity.inventario.UnidadDeMedida;
import com.gadbacorp.api.service.inventario.IUnidadDeMedidaService;

@RestController
@RequestMapping("/api/minimarket")
public class UnidadDeMedidaController {
    @Autowired
    private IUnidadDeMedidaService serviceUnidadDeMedida;

    @GetMapping("/unidad_medida")
    public List<UnidadDeMedida> buscartodos() {
        return serviceUnidadDeMedida.buscarTodos();
    }

    @PostMapping("unidad_medida") 
    public UnidadDeMedida guardar(@RequestBody UnidadDeMedida unidaddemedida) {
        serviceUnidadDeMedida.guardar(unidaddemedida);
        return unidaddemedida;
    }

    @PutMapping("unidad_medida")
    public UnidadDeMedida modificar(@RequestBody UnidadDeMedida unidaddemedida) {
        serviceUnidadDeMedida.modificar(unidaddemedida);
        return unidaddemedida;
    }

    @GetMapping("/unidad_medida/{id}") 
    public Optional<UnidadDeMedida> buscarId(@PathVariable("id") Integer id) {
        return serviceUnidadDeMedida.buscarId(id);
    }

    @DeleteMapping("/unidad_medida/{id}")
    public String eliminar(@PathVariable Integer id) {
        serviceUnidadDeMedida.eliminar(id);
        return "Unidad de medida eliminada";
    }
}
