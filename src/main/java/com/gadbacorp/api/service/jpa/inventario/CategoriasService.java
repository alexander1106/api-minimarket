package com.gadbacorp.api.service.jpa.inventario;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gadbacorp.api.entity.inventario.Categorias;
import com.gadbacorp.api.repository.inventario.CategoriasRepository;
import com.gadbacorp.api.service.inventario.ICategoriasService;

@Service
public class CategoriasService implements ICategoriasService{
@Autowired
    private CategoriasRepository repoCategorias;
    
    public List<Categorias> buscarTodos(){
        return repoCategorias.findAll();
    }

    public void guardar(Categorias categoria){
        repoCategorias.save(categoria);
    }
    
    public void modificar(Categorias categoria){
        repoCategorias.save(categoria);
    }

    public Optional<Categorias> buscarId(Integer id){
        return repoCategorias.findById(id);
    }

    public void eliminar(Integer id){
        repoCategorias.deleteById(id);
    }
}
