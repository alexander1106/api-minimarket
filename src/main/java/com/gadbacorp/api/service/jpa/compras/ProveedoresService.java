package com.gadbacorp.api.service.jpa.compras;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gadbacorp.api.entity.compras.Proveedores;
import com.gadbacorp.api.repository.compras.ProveedoresRepository;
import com.gadbacorp.api.service.compras.IProveedoresService;

@Service
public class ProveedoresService implements IProveedoresService{
    @Autowired
    private ProveedoresRepository repoProveedores;
    public List<Proveedores> buscarTodos(){
        return repoProveedores.findAll();
    }
    public void guardar(Proveedores proveedor){
        repoProveedores.save(proveedor);
    }

    public void modificar(Proveedores proveedor){
        repoProveedores.save(proveedor);
    }

    public Optional<Proveedores> buscarId(Integer id){
        return repoProveedores.findById(id);
    }

    public void eliminar(Integer id){
        repoProveedores.deleteById(id);
    }


}
