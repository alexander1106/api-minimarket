package com.gadbacorp.api.service.jpa.inventario;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gadbacorp.api.entity.inventario.TrasladoInventarioProducto;
import com.gadbacorp.api.repository.inventario.TrasladoInventarioProductoRepository;
import com.gadbacorp.api.service.inventario.ITrasladoInventarioProductoService;

@Service
public class TrasladoInventarioProductoService implements ITrasladoInventarioProductoService {

    @Autowired
    private TrasladoInventarioProductoRepository trasladoRepo;

    @Override
    public List<TrasladoInventarioProducto> buscarTodos() {
        return trasladoRepo.findAll();
    }

    @Override
    public Optional<TrasladoInventarioProducto> buscarId(Integer id) {
        return trasladoRepo.findById(id);
    }

    @Override
    public TrasladoInventarioProducto guardar(TrasladoInventarioProducto traslado) {
        return trasladoRepo.save(traslado);
    }

    @Override
    public TrasladoInventarioProducto modificar(TrasladoInventarioProducto traslado) {
        return trasladoRepo.save(traslado);
    }

    @Override
    public void eliminar(Integer id) {
        trasladoRepo.deleteById(id);
    }
}
