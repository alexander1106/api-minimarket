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

import com.gadbacorp.api.entity.inventario.Almacenes;
import com.gadbacorp.api.service.inventario.IAlmacenesService;

@RestController
@RequestMapping("/api/minimarket")
public class AlmacenesController {  
    @Autowired 
     private IAlmacenesService serviceAlmacenes;

    @GetMapping("/almacenes")
    public List<Almacenes> buscartodos() {
        return serviceAlmacenes.buscarTodos();
    }

    @PostMapping("almacenes") 
    public Almacenes guardar(@RequestBody Almacenes almacen) {
        serviceAlmacenes.guardar(almacen);
        return almacen;
    }

    @PutMapping("almacenes")
    public Almacenes modificar(@RequestBody Almacenes almacen) {
        serviceAlmacenes.modificar(almacen);
        return almacen;
    }

    @GetMapping("/almacenes/{id}") 
    public Optional<Almacenes> buscarId(@PathVariable("id") Integer id) {
        return serviceAlmacenes.buscarId(id);
    }

    @DeleteMapping("/almacenes/{id}")
    public String eliminar(@PathVariable Integer id) {
        serviceAlmacenes.eliminar(id);
        return "Almacen eliminado";
    }
}
