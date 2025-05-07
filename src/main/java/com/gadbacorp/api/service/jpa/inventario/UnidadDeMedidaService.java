package com.gadbacorp.api.service.jpa.inventario;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gadbacorp.api.entity.inventario.UnidadDeMedida;
import com.gadbacorp.api.repository.inventario.UnidadDeMedidaRepository;
import com.gadbacorp.api.service.inventario.IUnidadDeMedidaService;

@Service
public class UnidadDeMedidaService implements IUnidadDeMedidaService{
@Autowired
    private UnidadDeMedidaRepository repoUnidadDeMedida;
    public List<UnidadDeMedida> buscarTodos(){
        return repoUnidadDeMedida.findAll();
    }
    public void guardar(UnidadDeMedida unidaddemedida){
        repoUnidadDeMedida.save(unidaddemedida);
    }
    
    public void modificar(UnidadDeMedida unidaddemedida){
        repoUnidadDeMedida.save(unidaddemedida);
    }

    public Optional<UnidadDeMedida> buscarId(Integer id){
        return repoUnidadDeMedida.findById(id);
    }

    public void eliminar(Integer id){
        repoUnidadDeMedida.deleteById(id);
    }
}
