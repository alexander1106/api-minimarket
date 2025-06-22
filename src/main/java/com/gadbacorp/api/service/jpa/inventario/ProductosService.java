package com.gadbacorp.api.service.jpa.inventario;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gadbacorp.api.entity.inventario.Productos;
import com.gadbacorp.api.repository.inventario.ProductosRepository;
import com.gadbacorp.api.service.inventario.IProductosService;

@Service
public class ProductosService implements IProductosService {

    @Autowired
    private ProductosRepository repoProductos;

    @Override
    public List<Productos> buscarTodos() {
        return repoProductos.findAll();
    }

    @Override
    public Optional<Productos> buscarId(Integer id) {
        return repoProductos.findById(id);
    }

    @Override
    public Productos guardar(Productos producto) {
        return repoProductos.save(producto);
    }

    @Override
    public Productos modificar(Productos producto) {
        return repoProductos.save(producto);
    }

    @Override
    public void eliminar(Integer id) {
        repoProductos.deleteById(id);
    }

    @Override
    public List<Productos> listarProductosPorCategoria(Integer id) {
        return repoProductos.findByCategoria_Idcategoria(id);
    }
}
