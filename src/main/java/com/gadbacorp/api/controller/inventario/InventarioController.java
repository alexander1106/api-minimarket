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

import com.gadbacorp.api.entity.inventario.Inventario;
import com.gadbacorp.api.service.inventario.IInventarioService;

@RestController
@RequestMapping("/api/minimarket")
public class InventarioController {
    @Autowired
    private IInventarioService serviceInventario;

    @GetMapping("/inventario")
    public List<Inventario> buscarTodos() {
        return serviceInventario.buscarTodos();
    }

    @PostMapping("/inventario")
    public Inventario guardar(@RequestBody Inventario inventario) {
        serviceInventario.guardar(inventario);
        return inventario;
    }

    @PutMapping("/inventario")
    public Inventario modificar(@RequestBody Inventario inventario) {
        serviceInventario.modificar(inventario);
        return inventario;
    }

    @GetMapping("/inventario/{id}")
    public Optional<Inventario> buscarId(@PathVariable("id") Integer id) {
        return serviceInventario.buscarId(id);
    }

    @DeleteMapping("/inventario/{id}")
    public String eliminar(@PathVariable Integer id) {
        serviceInventario.eliminar(id);
        return "Producto eliminado de inventario";
    }
}
