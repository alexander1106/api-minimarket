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

import com.gadbacorp.api.entity.inventario.AlmacenProducto;
import com.gadbacorp.api.service.inventario.IAlmacenProductoService;

@RestController
@RequestMapping("/api/minimarket")
public class AlmacenProductoController {
    @Autowired
    private IAlmacenProductoService serviceAlmacenProducto;

    @GetMapping("/almacenproducto")
    public List<AlmacenProducto> buscarTodos() {
        return serviceAlmacenProducto.buscarTodos();
    }

    @PostMapping("/almacenproducto")
    public AlmacenProducto guardar(@RequestBody AlmacenProducto almacenproducto) {
        serviceAlmacenProducto.guardar(almacenproducto);
        return almacenproducto;
    }

    @PutMapping("/almacenproducto")
    public AlmacenProducto modificar(@RequestBody AlmacenProducto almacenproducto) {
        serviceAlmacenProducto.modificar(almacenproducto);
        return almacenproducto;
    }

    @GetMapping("/almacenproducto/{id}")
    public Optional<AlmacenProducto> buscarId(@PathVariable("id") Integer id) {
        return serviceAlmacenProducto.buscarId(id);
    }

    @DeleteMapping("/almacenproducto/{id}")
    public String eliminar(@PathVariable Integer id) {
        serviceAlmacenProducto.eliminar(id);
        return "AlmacenProducto eliminado";
    }
}
