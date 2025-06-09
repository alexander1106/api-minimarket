package com.gadbacorp.api.gadbacorp.controller.administrable;

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

import com.gadbacorp.api.gadbacorp.entity.administrable.Sucursales;
import com.gadbacorp.api.gadbacorp.service.administrable.ISucursalesService;

@RestController
@RequestMapping("/api/minimarket")
public class SucursalesController {
    @Autowired
    private ISucursalesService serviceSucursales;
 
    @GetMapping("/sucursales")
    public List<Sucursales> buscarTodos() {
        return serviceSucursales.buscarTodos();
    }
    @PostMapping("/sucursales")
    public Sucursales guardar(@RequestBody Sucursales sucursal) {
        serviceSucursales.guardar(sucursal);        
        return sucursal;
    }
    
    @PutMapping("/sucursales") //La diferencia acá es que subiremos el id, no ponemos "/clientes/id" por temas de seguridad, acuérdate de la caja de la unidad 2, eso no era correcto
    public Sucursales modificar(@RequestBody Sucursales sucursal) {        
        serviceSucursales.modificar(sucursal);
        return sucursal;
    }

    @GetMapping("/sucursales/{id}")
    public Optional<Sucursales> buscarId(@PathVariable("id") Integer id){
        return serviceSucursales.buscarId(id);
    }

    @DeleteMapping("/sucursales/{id}")
    public String eliminar(@PathVariable Integer id){
        serviceSucursales.eliminar(id);
        return "Sucursal elimnado";
    }

}
