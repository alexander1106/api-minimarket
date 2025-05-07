package com.gadbacorp.api.service.jpa.inventario;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gadbacorp.api.entity.inventario.Productos;
import com.gadbacorp.api.repository.inventario.ProductosRepository;
import com.gadbacorp.api.service.inventario.IProductosService;

@Service
public class ProductosService implements IProductosService{
@Autowired
    private ProductosRepository repoProductos;
    public List<Productos> buscarTodos(){
        return repoProductos.findAll();
    }

    public void guardar(Productos producto){
        repoProductos.save(producto);
    }
    
    public void modificar(Productos producto){
        repoProductos.save(producto);
    }

    public Optional<Productos> buscarId(Integer id){
        return repoProductos.findById(id);
    }

    public void eliminar(Integer id){
        repoProductos.deleteById(id);
    }
}
