package com.gadbacorp.api.service.jpa.administrable;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gadbacorp.api.entity.administrable.Empresas;
import com.gadbacorp.api.repository.administrable.EmpresasRepository;
import com.gadbacorp.api.service.administrable.IEmpresasService;

@Service
public class EmpresasService implements IEmpresasService{
    
    @Autowired
    private EmpresasRepository repoEmpresas;
    public List<Empresas> buscarTodos(){
        return repoEmpresas.findAll();
    }
    public void guardar(Empresas empresa){
        repoEmpresas.save(empresa);
    }

    public void modificar(Empresas empresa){
        repoEmpresas.save(empresa);
    }

    public Optional<Empresas> buscarId(Integer id){
        return repoEmpresas.findById(id);
    }

    public void eliminar(Integer id){
        repoEmpresas.deleteById(id);
    }



}
