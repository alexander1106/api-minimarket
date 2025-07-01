package com.gadbacorp.api.service.jpa.inventario;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.gadbacorp.api.entity.inventario.Categorias;
import com.gadbacorp.api.repository.inventario.CategoriasRepository;
import com.gadbacorp.api.repository.inventario.ProductosRepository;
import com.gadbacorp.api.service.inventario.ICategoriasService;

@Service
public class CategoriasService implements ICategoriasService {

    @Autowired
    private CategoriasRepository repo;

    @Autowired
    private ProductosRepository prodRepo;

    @Override
    public List<Categorias> buscarTodos() {
        return repo.findAll();
    }

    @Override
    public Optional<Categorias> buscarId(Integer id) {
        return repo.findById(id);
    }

    @Override
    public Categorias guardar(Categorias c) {
        // duplicado?
        repo.findByNombreIgnoreCase(c.getNombre())
            .ifPresent(x -> {
                throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ya existe una categoría con ese nombre"
                );
            });
        return repo.save(c);
    }

    @Override
    public Categorias modificar(Categorias c) {
        // ① NO permitir editar si ya está en uso
        if ( prodRepo.existsByCategoria_Idcategoria(c.getIdcategoria()) ) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "No se puede modificar: la categoría está en uso por productos"
            );
        }
        // ② duplicado en otra entidad?
        Optional<Categorias> dup = repo.findByNombreIgnoreCase(c.getNombre());
        if (dup.isPresent() && !dup.get().getIdcategoria().equals(c.getIdcategoria())) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Ya existe una categoría con ese nombre"
            );
        }
        // ③ todo ok → guardamos
        return repo.save(c);
    }

    @Override
    public void eliminar(Integer id) {
        // en uso?
        if (prodRepo.existsByCategoria_Idcategoria(id)) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "No se puede eliminar: la categoría está en uso por productos"
            );
        }
        repo.deleteById(id);
    }
}
