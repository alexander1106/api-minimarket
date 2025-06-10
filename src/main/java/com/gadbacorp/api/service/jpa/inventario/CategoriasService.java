package com.gadbacorp.api.service.jpa.inventario;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gadbacorp.api.entity.inventario.Categorias;
import com.gadbacorp.api.repository.inventario.CategoriasRepository;
import com.gadbacorp.api.service.inventario.ICategoriasService;

@Service
public class CategoriasService implements ICategoriasService {

    @Autowired
    private CategoriasRepository repoCategorias;

    @Override
    public List<Categorias> buscarTodos() {
        return repoCategorias.findAll();
    }

    @Override
    public Categorias guardar(Categorias categoria) {
        return repoCategorias.save(categoria);
    }

    @Override
    public Categorias modificar(Categorias categoria) {
        return repoCategorias.save(categoria);
    }

    @Override
    public Optional<Categorias> buscarId(Integer id) {
        return repoCategorias.findById(id);
    }

    @Override
    public void eliminar(Integer id) {
        boolean existe = repoCategorias.existsById(id);
        if (!existe) {
            throw new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.NOT_FOUND,
                "Categoría no encontrada id=" + id
            );
        }
        repoCategorias.deleteById(id);
    }

}
