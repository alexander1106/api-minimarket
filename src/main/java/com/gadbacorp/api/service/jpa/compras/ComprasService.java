package com.gadbacorp.api.service.jpa.compras;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gadbacorp.api.entity.compras.Compras;
import com.gadbacorp.api.repository.compras.ComprasRepository;
import com.gadbacorp.api.service.compras.IComprasService;

@Service
public class ComprasService implements IComprasService{
    @Autowired
    private ComprasRepository repoCompras;
    public List<Compras> buscarTodos(){
        return repoCompras.findAll();
    }
    public void guardar(Compras compra){
        repoCompras.save(compra);
    }

    public void modificar(Compras compra){
        repoCompras.save(compra);
    }

    public Optional<Compras> buscarId(Integer id){
        return repoCompras.findById(id);
    }

    public void eliminar(Integer id){
        repoCompras.deleteById(id);
    }
}