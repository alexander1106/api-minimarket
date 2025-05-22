package com.gadbacorp.api.service.jpa.inventario;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gadbacorp.api.entity.inventario.AlmacenProducto;
import com.gadbacorp.api.repository.inventario.AlmacenProductoRepository;
import com.gadbacorp.api.service.inventario.IAlmacenProductoService;

@Service
public class AlmacenProductoService implements IAlmacenProductoService{
    @Autowired
    private AlmacenProductoRepository repoAlmacenProducto;
    public List<AlmacenProducto> buscarTodos(){
        return repoAlmacenProducto.findAll();
    }

    public void guardar(AlmacenProducto almacenproducto){
        repoAlmacenProducto.save(almacenproducto);
    }
    
    public void modificar(AlmacenProducto almacenproducto){
        repoAlmacenProducto.save(almacenproducto);
    }

    public Optional<AlmacenProducto> buscarId(Integer id){
        return repoAlmacenProducto.findById(id);
    }

    public void eliminar(Integer id){
        repoAlmacenProducto.deleteById(id);
    }
}
