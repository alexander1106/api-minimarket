package com.gadbacorp.api.service.jpa.inventario;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gadbacorp.api.entity.inventario.AjusteInventario;
import com.gadbacorp.api.repository.inventario.AjusteInventarioRepository;
import com.gadbacorp.api.service.inventario.IAjusteInventarioService;

@Service
public class AjusteInventarioService implements IAjusteInventarioService {

    @Autowired
    private AjusteInventarioRepository repoAjuste;

    @Override
    public List<AjusteInventario> buscarTodos() {
        return repoAjuste.findAll();
    }

    @Override
    public Optional<AjusteInventario> buscarId(Integer id) {
        return repoAjuste.findById(id);
    }

    @Override
    public AjusteInventario guardar(AjusteInventario ajuste) {
        return repoAjuste.save(ajuste);
    }

    @Override
    public AjusteInventario modificar(AjusteInventario ajuste) {
        return repoAjuste.save(ajuste);
    }

    @Override
    public void eliminar(Integer id) {
        repoAjuste.deleteById(id);
    }
}
