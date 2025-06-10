package com.gadbacorp.api.service.jpa.seguridad;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gadbacorp.api.entity.seguridad.Modulo;
import com.gadbacorp.api.repository.seguridad.MiduloRepository;
import com.gadbacorp.api.service.seguridad.IModuloService;
@Service
public class ModuloService implements  IModuloService{
    @Autowired
    private MiduloRepository moduloRepository;

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
    public Modulo actualizar(Long id, Modulo modulo) {
return moduloRepository.save(modulo);    }

    @Override
    public void eliminar(Long id) {
         moduloRepository.deleteById(id);
    }
    
}
