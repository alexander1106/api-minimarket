package com.gadbacorp.api.service.jpa.inventario;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.gadbacorp.api.entity.inventario.TipoProducto;
import com.gadbacorp.api.repository.inventario.ProductosRepository;
import com.gadbacorp.api.repository.inventario.TipoProductoRepository;
import com.gadbacorp.api.service.inventario.ITipoProductoService;

@Service
public class TipoProductoService implements ITipoProductoService {

    @Autowired
    private TipoProductoRepository repoTipoProducto;

    @Autowired
    private ProductosRepository prodRepo;

    @Override
    public List<TipoProducto> buscarTodos() {
        return repoTipoProducto.findAll();
    }

    @Override
    public TipoProducto guardar(TipoProducto tipo) {
        // Validación de nombre duplicado (ignore case)
        repoTipoProducto.findByNombreIgnoreCase(tipo.getNombre())
            .ifPresent(t -> {
                throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ya existe un tipo de producto con ese nombre"
                );
            });
        return repoTipoProducto.save(tipo);
    }

    @Override
    public TipoProducto modificar(TipoProducto tipo) {
        // Validar duplicado excluyendo la misma entidad
        Optional<TipoProducto> dup = repoTipoProducto.findByNombreIgnoreCase(tipo.getNombre());
        if (dup.isPresent() && !dup.get().getIdtipoproducto().equals(tipo.getIdtipoproducto())) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Ya existe un tipo de producto con ese nombre"
            );
        }
        return repoTipoProducto.save(tipo);
    }

    @Override
    public Optional<TipoProducto> buscarId(Integer id) {
        return repoTipoProducto.findById(id);
    }

    @Override
    public void eliminar(Integer id) {
        // Validar que no esté en uso por productos
        if (prodRepo.existsByTipoProducto_Idtipoproducto(id)) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "No se puede eliminar: el tipo de producto está en uso por productos"
            );
        }
        repoTipoProducto.deleteById(id);
    }
}
