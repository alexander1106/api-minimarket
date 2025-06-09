package com.gadbacorp.api.gadbacorp.service.security.impl;

import com.gadbacorp.api.gadbacorp.entity.security.Modulo;
import com.gadbacorp.api.gadbacorp.repository.security.ModuloRepository;
import com.gadbacorp.api.gadbacorp.service.security.ModuloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ModuloServiceImpl implements ModuloService {

    @Autowired
    private ModuloRepository moduloRepository;

    @Override
    public List<Modulo> listarTodos() {
        return moduloRepository.findAll();
    }

    @Override
    public Optional<Modulo> obtenerPorId(Long id) {
        return moduloRepository.findById(id);
    }

    @Override
    public Modulo guardar(Modulo modulo) {
        return moduloRepository.save(modulo);
    }

    @Override
    public Modulo actualizar(Long id, Modulo moduloActualizado) {
        Modulo moduloExistente = moduloRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Módulo no encontrado con ID: " + id));

        moduloExistente.setNombre(moduloActualizado.getNombre());
        moduloExistente.setUrl(moduloActualizado.getUrl());
        moduloExistente.setPadre(moduloActualizado.getPadre());

        return moduloRepository.save(moduloExistente);
    }

    @Override
    public void eliminar(Long id) {
        moduloRepository.deleteById(id);
    }
}
