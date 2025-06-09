package com.gadbacorp.api.service.jpa.inventario;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gadbacorp.api.entity.inventario.Almacenes;
import com.gadbacorp.api.repository.inventario.AlmacenesRepository;
import com.gadbacorp.api.service.inventario.IAlmacenesService;

@Service
public class AlmacenesService implements IAlmacenesService {

    @Autowired
    private AlmacenesRepository repoAlmacenes;

    @Override
    public Optional<Almacenes> buscarPorNombre(String nombre) {
        return repoAlmacenes.findByNombreIgnoreCase(nombre.trim());
    }

    @Override
    public List<Almacenes> buscarTodos() {
        return repoAlmacenes.findAll();
    }

    @Override
    public Almacenes guardar(Almacenes almacen) {
        return repoAlmacenes.save(almacen);
    }

    @Override
    public Almacenes modificar(Almacenes almacen) {
        return repoAlmacenes.save(almacen);
    }

    @Override
    public Optional<Almacenes> buscarId(Integer id) {
        return repoAlmacenes.findById(id);
    }

    @Override
    public void eliminar(Integer id) {
        repoAlmacenes.deleteById(id);
    }
}
