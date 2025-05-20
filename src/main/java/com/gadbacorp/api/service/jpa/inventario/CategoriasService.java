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

    @Override
    public Categorias guardar(Categorias categoria) {
    Optional<Categorias> existente = repoCategorias.findByNombreIgnoreCase(categoria.getNombre());

    if (existente.isPresent()) {
        throw new IllegalArgumentException("Ya existe una categoría con ese nombre.");
    }

    return repoCategorias.save(categoria);
    }

    
    @Override
    public Categorias modificar(Categorias categoria) {
    Optional<Categorias> existente = repoCategorias.findByNombreIgnoreCase(categoria.getNombre());

    if (existente.isPresent() && !existente.get().getIdcategoria().equals(categoria.getIdcategoria())) {
        throw new IllegalArgumentException("Ya existe una categoría con ese nombre.");
    }

    return repoCategorias.save(categoria);
    }


    public Optional<Categorias> buscarId(Integer id){
        return repoCategorias.findById(id);
    }

    public void eliminar(Integer id){
        repoCategorias.deleteById(id);
    }
}
