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
    public void guardar(TipoProducto tipoproducto){
        repoTipoProducto.save(tipoproducto);
    }
    
    public void modificar(TipoProducto tipoproducto){
        repoTipoProducto.save(tipoproducto);
    }

    public Optional<TipoProducto> buscarId(Integer id){
        return repoTipoProducto.findById(id);
    }

    public void eliminar(Integer id){
        repoTipoProducto.deleteById(id);
    }
}
