package com.gadbacorp.api.service.jpa.inventario;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gadbacorp.api.entity.inventario.TipoProducto;
import com.gadbacorp.api.repository.inventario.TipoProductoRepository;
import com.gadbacorp.api.service.inventario.ITipoProductoService;

@Service
public class TipoProductoService implements ITipoProductoService {

    @Autowired
    private TipoProductoRepository repoTipoProducto;

    @Override
    public List<TipoProducto> buscarTodos() {
        return repoTipoProducto.findAll();
    }

    @Override
    public TipoProducto guardar(TipoProducto tipoproducto) {
        return repoTipoProducto.save(tipoproducto);
    }

    @Override
    public TipoProducto modificar(TipoProducto tipoproducto) {
        return repoTipoProducto.save(tipoproducto);
    }

    @Override
    public Optional<TipoProducto> buscarId(Integer id) {
        return repoTipoProducto.findById(id);
    }

    @Override
    public void eliminar(Integer id) {
        repoTipoProducto.deleteById(id);
    }


}
