package com.gadbacorp.api.gadbacorp.service.security.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gadbacorp.api.gadbacorp.entity.security.Rol;
import com.gadbacorp.api.gadbacorp.repository.security.RolRepository;
import com.gadbacorp.api.gadbacorp.service.security.RolService;

import java.util.List;
import java.util.Optional;

@Service
public class RolServiceImpl implements RolService {

    @Autowired
    private RolRepository rolRepository;

    @Override
    public List<Rol> listarTodos() {
        return rolRepository.findAll();
    }

    @Override
    public Optional<Rol> obtenerPorId(Long id) {
        return rolRepository.findById(id);
    }

    @Override
    public Optional<Rol> obtenerPorNombre(String nombre) {
        return rolRepository.findByNombre(nombre);
    }

    @Override
    public Rol guardar(Rol rol) {
        return rolRepository.save(rol);
    }

    @Override
    public Rol actualizar(Long id, Rol rolActualizado) {
        Rol rolExistente = rolRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado con ID: " + id));
        rolExistente.setNombre(rolActualizado.getNombre());
        /* rolExistente.setDescripcion(rolActualizado.getDescripcion()); */
        // Aquí puedes actualizar más campos si los tienes
        return rolRepository.save(rolExistente);
    }

    @Override
    public void eliminar(Long id) {
        rolRepository.deleteById(id);
    }
}
