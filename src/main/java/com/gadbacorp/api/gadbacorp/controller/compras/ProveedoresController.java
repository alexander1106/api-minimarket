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

import com.gadbacorp.api.gadbacorp.entity.compras.Proveedores;
import com.gadbacorp.api.gadbacorp.service.compras.IProveedoresService;

@RestController
@RequestMapping("/api/minimarket")
public class ProveedoresController {
    @Autowired
    private IProveedoresService serviceProveedores;

    @GetMapping("/proveedores")
    public List<Proveedores> buscarTodos() {
        return serviceProveedores.buscarTodos();
    }
    @PostMapping("/proveedores")
    public Proveedores guardar(@RequestBody Proveedores proveedor) {
        serviceProveedores.guardar(proveedor);        
        return proveedor;
    }
    
    @PutMapping("/proveedores") //La diferencia acá es que subiremos el id, no ponemos "/clientes/id" por temas de seguridad, acuérdate de la caja de la unidad 2, eso no era correcto
    public Proveedores modificar(@RequestBody Proveedores proveedor) {        
        serviceProveedores.modificar(proveedor);
        return proveedor;
    }

    @GetMapping("/proveedores/{id}")
    public Optional<Proveedores> buscarId(@PathVariable("id") Integer id){
        return serviceProveedores.buscarId(id);
    }

    @DeleteMapping("/proveedores/{id}")
    public String eliminar(@PathVariable Integer id){
        serviceProveedores.eliminar(id);
        return "Proveedor elimnado";
    }

}
