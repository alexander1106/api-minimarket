package com.gadbacorp.api.service.jpa.inventario;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gadbacorp.api.entity.inventario.InventarioProducto;
import com.gadbacorp.api.repository.inventario.InventarioProductoRepository;
import com.gadbacorp.api.service.inventario.IInventarioProductoService;

@Service
public class InventarioProductoService implements IInventarioProductoService {

    @Autowired
    private InventarioProductoRepository repoInventarioProducto;

    @Override
    public List<InventarioProducto> buscarTodos() {
        return repoInventarioProducto.findAll();
    }

    @Override
    public Optional<InventarioProducto> buscarId(Integer id) {
        return repoInventarioProducto.findById(id);
    }

    @Override
    public InventarioProducto guardar(InventarioProducto inventarioProducto) {
        return repoInventarioProducto.save(inventarioProducto);
    }

    @Override
    public InventarioProducto modificar(InventarioProducto inventarioProducto) {
        return repoInventarioProducto.save(inventarioProducto);
    }

    @Override
    public void eliminar(Integer id) {
        repoInventarioProducto.deleteById(id);
    }
}
