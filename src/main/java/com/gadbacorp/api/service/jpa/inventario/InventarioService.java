package com.gadbacorp.api.service.jpa.inventario;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gadbacorp.api.entity.inventario.Inventario;
import com.gadbacorp.api.repository.inventario.InventarioRepository;
import com.gadbacorp.api.service.inventario.IInventarioService;

@Service
public class InventarioService implements IInventarioService{
    @Autowired
    private InventarioRepository repoInventario;
    public List<Inventario> buscarTodos(){
        return repoInventario.findAll();
    }

    public Inventario guardar(Inventario inventario){
        return repoInventario.save(inventario);
    }
    
    public Inventario modificar(Inventario inventario){
        return repoInventario.save(inventario);
    }

    public Optional<Inventario> buscarId(Integer id){
        return repoInventario.findById(id);
    }

    public void eliminar(Integer id){
        repoInventario.deleteById(id);
    }
}
