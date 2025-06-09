package com.gadbacorp.api.gadbacorp.service.jpa.inventario;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gadbacorp.api.gadbacorp.entity.inventario.AlmacenProducto;
import com.gadbacorp.api.gadbacorp.repository.inventario.AlmacenProductoRepository;
import com.gadbacorp.api.gadbacorp.service.inventario.IAlmacenProductoService;

@Service
public class AlmacenProductoService implements IAlmacenProductoService{
    @Autowired
    private AlmacenProductoRepository repoAlmacenProducto;

    @Override
    public Optional<AlmacenProducto> buscarPorProductoYAlmacen(Integer idProd, Integer idAlm) {
        return repoAlmacenProducto.findByProducto_IdproductoAndAlmacen_Idalmacen(idProd, idAlm);
    }
    public List<AlmacenProducto> buscarTodos(){
        return repoAlmacenProducto.findAll();
    }

    public AlmacenProducto guardar(AlmacenProducto almacenproducto){
        return repoAlmacenProducto.save(almacenproducto);
    }
    
    public AlmacenProducto modificar(AlmacenProducto almacenproducto){
        return repoAlmacenProducto.save(almacenproducto);
    }

    public Optional<AlmacenProducto> buscarId(Integer id){
        return repoAlmacenProducto.findById(id);
    }

    public void eliminar(Integer id){
        repoAlmacenProducto.deleteById(id);
    }
}
