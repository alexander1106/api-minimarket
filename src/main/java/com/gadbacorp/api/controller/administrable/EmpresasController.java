package com.gadbacorp.api.controller.administrable;
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
import com.gadbacorp.api.entity.administrable.Empresas;
import com.gadbacorp.api.service.administrable.IEmpresasService;

@RestController
@RequestMapping("/api/minimarket/empresas")
public class EmpresasController {

    @Autowired
    private IEmpresasService serviceEmpresas;

    @GetMapping
    public List<Empresas> buscarTodos() {
        return serviceEmpresas.buscarTodos();
    }
    @PostMapping
    public Empresas guardar(@RequestBody Empresas empresa) {
        serviceEmpresas.guardar(empresa);        
        return empresa;
    }
    
    @PutMapping
    public Empresas modificar(@RequestBody Empresas empresa) {        
        serviceEmpresas.modificar(empresa);
        return empresa;
    }

    @GetMapping("/{id}")
    public Optional<Empresas> buscarId(@PathVariable("id") Integer id){
        return serviceEmpresas.buscarId(id);
    }

    @DeleteMapping("/{id}")
    public String eliminar(@PathVariable Integer id){
        serviceEmpresas.eliminar(id);
        return "Empresa elimnado";
    }
}
