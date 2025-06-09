package com.gadbacorp.api.gadbacorp.service.jpa.inventario;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gadbacorp.api.gadbacorp.entity.inventario.UnidadDeMedida;
import com.gadbacorp.api.gadbacorp.repository.inventario.UnidadDeMedidaRepository;
import com.gadbacorp.api.gadbacorp.service.inventario.IUnidadDeMedidaService;

@Service
public class UnidadDeMedidaService implements IUnidadDeMedidaService{
@Autowired
    private UnidadDeMedidaRepository repoUnidadDeMedida;
    public List<UnidadDeMedida> buscarTodos(){
        return repoUnidadDeMedida.findAll();
    }
    @Override
    public UnidadDeMedida guardar(UnidadDeMedida unidad) {
    Optional<UnidadDeMedida> existente = repoUnidadDeMedida.findByNombreIgnoreCase(unidad.getNombre());

    if (existente.isPresent()) {
        throw new IllegalArgumentException("Ya existe una unidad de medida con ese nombre.");
    }

    return repoUnidadDeMedida.save(unidad);
    }
    
    @Override
    public UnidadDeMedida modificar(UnidadDeMedida unidad) {
    Optional<UnidadDeMedida> existente = repoUnidadDeMedida.findByNombreIgnoreCase(unidad.getNombre());

    // Validar si ya existe otra unidad con ese nombre y es diferente al que estamos modificando
    if (existente.isPresent() && !existente.get().getIdunidadmedida().equals(unidad.getIdunidadmedida())) {
        throw new IllegalArgumentException("Ya existe una unidad de medida con ese nombre.");
    }

    return repoUnidadDeMedida.save(unidad);
}

    public Optional<UnidadDeMedida> buscarId(Integer id){
        return repoUnidadDeMedida.findById(id);
    }

    public void eliminar(Integer id){
        repoUnidadDeMedida.deleteById(id);
    }
}
