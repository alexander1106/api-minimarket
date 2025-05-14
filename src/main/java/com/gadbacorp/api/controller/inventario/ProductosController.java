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

import com.gadbacorp.api.entity.inventario.Productos;
import com.gadbacorp.api.service.inventario.IProductosService;

@RestController
@RequestMapping("/api/minimarket")
public class ProductosController { 
    @Autowired
    private IProductosService serviceProductos;

    @GetMapping("/productos")
    public List<Productos> buscarTodos() {
        return serviceProductos.buscarTodos();
    }

    @PostMapping("/productos")
    public Productos guardar(@RequestBody Productos producto) {
        serviceProductos.guardar(producto);
        return producto;
    }

    @PutMapping("/productos")
    public Productos modificar(@RequestBody Productos producto) {
        serviceProductos.modificar(producto);
        return producto;
    }

    @GetMapping("/productos/{id}")
    public Optional<Productos> buscarId(@PathVariable("id") Integer id) {
        return serviceProductos.buscarId(id);
    }

    @DeleteMapping("/productos/{id}")
    public String eliminar(@PathVariable Integer id) {
        serviceProductos.eliminar(id);
        return "Producto eliminado";
    }
}
