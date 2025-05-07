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

import com.gadbacorp.api.entity.inventario.TipoProducto;
import com.gadbacorp.api.service.inventario.ITipoProductoService;

@RestController
@RequestMapping("/api/minimarket")
public class TipoProductoController {
    @Autowired
    private ITipoProductoService serviceTipoProducto;
    @GetMapping("/tipoproducto")
    public List<TipoProducto> buscartodos() {
        return serviceTipoProducto.buscarTodos();
    }

    @PostMapping("tipoproducto") 
    public TipoProducto guardar(@RequestBody TipoProducto tipoproducto) {
        serviceTipoProducto.guardar(tipoproducto);
        return tipoproducto;
    }

    @PutMapping("tipoproducto")
    public TipoProducto modificar(@RequestBody TipoProducto tipoproducto) {
        serviceTipoProducto.modificar(tipoproducto);
        return tipoproducto;
    }

    @GetMapping("/tipoproducto/{id}") 
    public Optional<TipoProducto> buscarId(@PathVariable("id") Integer id) {
        return serviceTipoProducto.buscarId(id);
    }

    @DeleteMapping("/tipoproducto/{id}")
    public String eliminar(@PathVariable Integer id) {
        serviceTipoProducto.eliminar(id);
        return "Tipo Producto eliminado";
    }
}
