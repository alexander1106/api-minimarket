package com.gadbacorp.api.service.jpa.seguridad;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gadbacorp.api.entity.seguridad.Registros;
import com.gadbacorp.api.repository.seguridad.RegistrosRepository;
import com.gadbacorp.api.service.seguridad.IRegistrosService;
@Service
public class RegistrosService implements IRegistrosService {
    @Autowired
    private RegistrosRepository repoRegistros;
    @Override
    public List<Registros> buscarTodos(){
        return repoRegistros.findAll();
    }
    
    @Override
    public void guardar(Registros registro){
        repoRegistros.save(registro);
    }

    @Override
    public void modificar(Registros registro){
        repoRegistros.save(registro);
    }
    
    @Override
    public Optional<Registros> buscarId(Integer id){
        return repoRegistros.findById(id);
    }
    @Override
    public void eliminar(Integer id){
        repoRegistros.deleteById(id);
    }
    
}