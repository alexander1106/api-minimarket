package com.gadbacorp.api.service.jpa.inventario;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.gadbacorp.api.entity.inventario.UnidadDeMedida;
import com.gadbacorp.api.repository.inventario.ProductosRepository;
import com.gadbacorp.api.repository.inventario.UnidadDeMedidaRepository;
import com.gadbacorp.api.service.inventario.IUnidadDeMedidaService;

@Service
public class UnidadDeMedidaService implements IUnidadDeMedidaService {

    @Autowired
    private UnidadDeMedidaRepository unidadRepo;

    @Autowired
    private ProductosRepository prodRepo;

    @Override
    public List<UnidadDeMedida> buscarTodos() {
        return unidadRepo.findAll();
    }

    @Override
    public UnidadDeMedida guardar(UnidadDeMedida unidad) {
        unidadRepo.findByNombreIgnoreCase(unidad.getNombre())
            .ifPresent(u -> {
                throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ya existe una unidad de medida con ese nombre"
                );
            });
        return unidadRepo.save(unidad);
    }

    @Override
public UnidadDeMedida modificar(UnidadDeMedida unidad) {
    // ① Validar uso en Productos: si existe, lanzamos 400
    if (prodRepo.existsByUnidadMedida_Idunidadmedida(unidad.getIdunidadmedida())) {
        throw new ResponseStatusException(
            HttpStatus.BAD_REQUEST,
            "No se puede modificar: la unidad está en uso por productos"
        );
    }
    // ② Luego validamos duplicados de nombre (409)
    Optional<UnidadDeMedida> dup = unidadRepo.findByNombreIgnoreCase(unidad.getNombre());
    if (dup.isPresent() && !dup.get().getIdunidadmedida().equals(unidad.getIdunidadmedida())) {
        throw new ResponseStatusException(
            HttpStatus.CONFLICT,
            "Ya existe una unidad de medida con ese nombre"
        );
    }
    // ③ Si pasa ambas, actualizamos
    return unidadRepo.save(unidad);
}

    @Override
    public Optional<UnidadDeMedida> buscarId(Integer id) {
        return unidadRepo.findById(id);
    }

    @Override
    public void eliminar(Integer id) {
        if (prodRepo.existsByUnidadMedida_Idunidadmedida(id)) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "No se puede eliminar: la unidad está en uso por productos"
            );
        }
        unidadRepo.deleteById(id);
    }
}
