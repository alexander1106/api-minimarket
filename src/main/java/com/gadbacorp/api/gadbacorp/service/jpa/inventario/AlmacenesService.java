package com.gadbacorp.api.gadbacorp.service.jpa.inventario;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gadbacorp.api.gadbacorp.entity.inventario.Almacenes;
import com.gadbacorp.api.gadbacorp.repository.inventario.AlmacenesRepository;
import com.gadbacorp.api.gadbacorp.service.inventario.IAlmacenesService;

@Service
public class AlmacenesService implements IAlmacenesService{
    @Autowired
    private AlmacenesRepository repoAlmacenes;

    @Override
    public Optional<Almacenes> buscarPorNombre(String nombre) {
        return repoAlmacenes.findByNombreIgnoreCase(nombre);
    }
    public List<Almacenes> buscarTodos(){
        return repoAlmacenes.findAll();
    }
    public Almacenes guardar(Almacenes almacen){
        return repoAlmacenes.save(almacen);
    }
    
    public Almacenes modificar(Almacenes almacen){
        return repoAlmacenes.save(almacen);
    }

    public Optional<Almacenes> buscarId(Integer id){
        return repoAlmacenes.findById(id);
    }

    public void eliminar(Integer id){
        repoAlmacenes.deleteById(id);
    }

}
