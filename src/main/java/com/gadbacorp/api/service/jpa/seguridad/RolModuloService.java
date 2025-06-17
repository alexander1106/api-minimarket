package com.gadbacorp.api.service.jpa.seguridad;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gadbacorp.api.entity.seguridad.Rol;
import com.gadbacorp.api.entity.seguridad.RolModulo;
import com.gadbacorp.api.repository.seguridad.RolModuloRepository;
import com.gadbacorp.api.service.seguridad.IRolModuloService;

@Service
public class RolModuloService implements IRolModuloService{

    @Autowired
    private RolModuloRepository rolModuloRepository;
    
    @Override
    public List<RolModulo> listarTodos() {
        return rolModuloRepository.findAll();
    }

    @Override
    public Optional<RolModulo> obtenerPorId(Integer id) {
        return rolModuloRepository.findById(id);
    }

    @Override
    public RolModulo guardar(RolModulo rol) {
        return rolModuloRepository.save(rol);
    }

    @Override
    public RolModulo actualizar(RolModulo rol) {
        return rolModuloRepository.save(rol);
    }

    @Override
    public void eliminar(Integer id) {
        rolModuloRepository.deleteById(id);
    }
    
}
