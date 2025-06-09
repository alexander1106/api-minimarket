package com.gadbacorp.api.gadbacorp.controller.compras;

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

import com.gadbacorp.api.gadbacorp.entity.compras.Compras;

import com.gadbacorp.api.gadbacorp.service.compras.IComprasService;


@RestController
@RequestMapping("/api/minimarket")
public class ComprasController {
    @Autowired
    private IComprasService serviceCompras;

    @GetMapping("/compras")
    public List<Compras> buscarTodos() {
        return serviceCompras.buscarTodos();
    }

    @PostMapping("/compras")
    public Compras guardar(@RequestBody Compras compra) {
        serviceCompras.guardar(compra);
        return compra;
    }

    @PutMapping("/compras")
    public Compras modificar(@RequestBody Compras compra) {
        serviceCompras.modificar(compra);
        return compra;
    }

    @GetMapping("/compras/{id}")
    public Optional<Compras> buscarId(@PathVariable("id") Integer id) {
        return serviceCompras.buscarId(id);
    }

    @DeleteMapping("/compras/{id}")
    public String eliminar(@PathVariable Integer id) {
        serviceCompras.eliminar(id);
        return "Compra eliminada";
    }

}
