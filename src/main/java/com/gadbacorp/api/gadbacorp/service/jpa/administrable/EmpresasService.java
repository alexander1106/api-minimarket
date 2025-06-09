package com.gadbacorp.api.gadbacorp.service.jpa.administrable;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gadbacorp.api.gadbacorp.entity.administrable.Empresas;
import com.gadbacorp.api.gadbacorp.repository.administrable.EmpresasRepository;
import com.gadbacorp.api.gadbacorp.service.administrable.IEmpresasService;

import jakarta.persistence.EntityNotFoundException;

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

    // public void eliminar(Integer id) {
    //     Optional<Empresas> empresaOpt = buscarId(id);
    //     if (empresaOpt.isPresent()) {
    //         repoEmpresas.deleteById(id);
    //     } else {
    //         throw new EntityNotFoundException("La empresa con id " + id + " no existe.");
    //     }
    // }


}
