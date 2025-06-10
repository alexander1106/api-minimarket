package com.gadbacorp.api.service.jpa.administrable;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gadbacorp.api.entity.administrable.Sucursales;
import com.gadbacorp.api.repository.administrable.SucursalesRepository;
import com.gadbacorp.api.service.administrable.ISucursalesService;

@Service
public class SucursalesService implements ISucursalesService {
    
    @Autowired
    private SucursalesRepository sucursalesRepository;

    @Override
    public List<Sucursales> buscarTodos() {
        return sucursalesRepository.findAll();
    }

    @Override
    public Sucursales guardar(Sucursales sucursal) {
        return sucursalesRepository.save(sucursal);
    }

    @Override
    public Sucursales modificar(Sucursales sucursal) {
        return sucursalesRepository.save(sucursal);
    }

    @Override
    public Optional<Sucursales> buscarId(Integer id) {
        return sucursalesRepository.findById(id);
    }
  
    @Override
    public void eliminar(Integer id) {
        sucursalesRepository.deleteById(id);
    }
}
