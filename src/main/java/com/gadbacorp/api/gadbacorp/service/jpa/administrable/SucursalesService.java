package com.gadbacorp.api.gadbacorp.service.jpa.administrable;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gadbacorp.api.gadbacorp.entity.administrable.Sucursales;
import com.gadbacorp.api.gadbacorp.repository.administrable.SucursalesRepository;
import com.gadbacorp.api.gadbacorp.service.administrable.ISucursalesService;

@Service
public class SucursalesService implements ISucursalesService{
    @Autowired
    private SucursalesRepository repoSucursales;
    public List<Sucursales> buscarTodos(){
        return repoSucursales.findAll();
    }
    public void guardar(Sucursales sucursal){
        repoSucursales.save(sucursal);
    }

    public void modificar(Sucursales sucursal){
        repoSucursales.save(sucursal);
    }

    public Optional<Sucursales> buscarId(Integer id){
        return repoSucursales.findById(id);
    }

    public void eliminar(Integer id){
        repoSucursales.deleteById(id);
    }

}
