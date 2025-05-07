package com.gadbacorp.api.service.jpa.inventario;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gadbacorp.api.entity.inventario.Almacenes;
import com.gadbacorp.api.repository.inventario.AlmacenesRepository;
import com.gadbacorp.api.service.inventario.IAlmacenesService;

@Service
public class AlmacenesService implements IAlmacenesService{
    @Autowired
    private AlmacenesRepository repoAlmacenes;
    public List<Almacenes> buscarTodos(){
        return repoAlmacenes.findAll();
    }
    public void guardar(Almacenes almacen){
        repoAlmacenes.save(almacen);
    }
    
    public void modificar(Almacenes almacen){
        repoAlmacenes.save(almacen);
    }

    public Optional<Almacenes> buscarId(Integer id){
        return repoAlmacenes.findById(id);
    }

    public void eliminar(Integer id){
        repoAlmacenes.deleteById(id);
    }
}
