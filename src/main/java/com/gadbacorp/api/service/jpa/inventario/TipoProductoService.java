package com.gadbacorp.api.service.jpa.inventario;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gadbacorp.api.entity.inventario.TipoProducto;
import com.gadbacorp.api.repository.inventario.TipoProductoRepository;
import com.gadbacorp.api.service.inventario.ITipoProductoService;

@Service
public class TipoProductoService implements ITipoProductoService{
    @Autowired
    private TipoProductoRepository repoTipoProducto;
    public List<TipoProducto> buscarTodos(){
        return repoTipoProducto.findAll();
    }
    @Override
    public TipoProducto guardar(TipoProducto tipoproducto) {
    Optional<TipoProducto> existente = repoTipoProducto.findByNombreIgnoreCase(tipoproducto.getNombre());

    if (existente.isPresent()) {
        throw new IllegalArgumentException("Ya existe un tipo de producto con ese nombre.");
        }

        return repoTipoProducto.save(tipoproducto);
    }

    
    @Override
    public TipoProducto modificar(TipoProducto tipoproducto) {
    Optional<TipoProducto> existente = repoTipoProducto.findByNombreIgnoreCase(tipoproducto.getNombre());

    if (existente.isPresent() && !existente.get().getIdtipoproducto().equals(tipoproducto.getIdtipoproducto())) {
        throw new IllegalArgumentException("Ya existe un tipo de producto con ese nombre.");
        }

        return repoTipoProducto.save(tipoproducto);
    }


    public Optional<TipoProducto> buscarId(Integer id){
        return repoTipoProducto.findById(id);
    }

    public void eliminar(Integer id){
        repoTipoProducto.deleteById(id);
    }
}
