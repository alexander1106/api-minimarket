package com.gadbacorp.api.service.jpa.inventario;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gadbacorp.api.entity.inventario.UnidadDeMedida;
import com.gadbacorp.api.repository.inventario.UnidadDeMedidaRepository;
import com.gadbacorp.api.service.inventario.IUnidadDeMedidaService;

@Service
public class UnidadDeMedidaService implements IUnidadDeMedidaService {

    @Autowired
    private UnidadDeMedidaRepository repoUnidadDeMedida;

    @Override
    public List<UnidadDeMedida> buscarTodos() {
        return repoUnidadDeMedida.findAll();
    }

    @Override
    public UnidadDeMedida guardar(UnidadDeMedida unidad) {
        return repoUnidadDeMedida.save(unidad);
    }

    @Override
    public UnidadDeMedida modificar(UnidadDeMedida unidad) {
        return repoUnidadDeMedida.save(unidad);
    }

    @Override
    public Optional<UnidadDeMedida> buscarId(Integer id) {
        return repoUnidadDeMedida.findById(id);
    }

    @Override
    public void eliminar(Integer id) {
        repoUnidadDeMedida.deleteById(id);
    }


}
