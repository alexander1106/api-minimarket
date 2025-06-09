package com.gadbacorp.api.gadbacorp.controller.administrable;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gadbacorp.api.gadbacorp.entity.administrable.Empresas;
import com.gadbacorp.api.gadbacorp.service.administrable.IEmpresasService;

@RestController
@RequestMapping("/api/minimarket")
public class EmpresasController {
    
    @Autowired
    private IEmpresasService serviceEmpresas;

    
    @GetMapping("/empresas")
    public List<Empresas> buscarTodos() {
        return serviceEmpresas.buscarTodos();
    }
    @PostMapping("/empresas")
    public Empresas guardar(@RequestBody Empresas empresa) {
        serviceEmpresas.guardar(empresa);        
        return empresa;
    }
    
    @PutMapping("/empresas") //La diferencia acá es que subiremos el id, no ponemos "/clientes/id" por temas de seguridad, acuérdate de la caja de la unidad 2, eso no era correcto
    public Empresas modificar(@RequestBody Empresas empresa) {        
        serviceEmpresas.modificar(empresa);
        return empresa;
    }

    @GetMapping("/empresas/{id}")
    public Optional<Empresas> buscarId(@PathVariable("id") Integer id){
        return serviceEmpresas.buscarId(id);
    }

    @DeleteMapping("/empresas/{id}")
    public String eliminar(@PathVariable Integer id){
        serviceEmpresas.eliminar(id);
        return "Empresa elimnado";
    }

}
