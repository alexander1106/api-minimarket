package com.gadbacorp.api.service.jpa.inventario;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.gadbacorp.api.entity.inventario.Almacenes;
import com.gadbacorp.api.entity.inventario.Inventario;
import com.gadbacorp.api.repository.inventario.AlmacenesRepository;
import com.gadbacorp.api.repository.inventario.InventarioRepository;
import com.gadbacorp.api.service.inventario.IInventarioService;

@Service
public class InventarioService implements IInventarioService {

    @Autowired
    private InventarioRepository repoInventario;

    @Autowired
    private AlmacenesRepository repoAlmacenes;

    @Override
    public List<Inventario> buscarTodos() {
        return repoInventario.findAll();
    }

    @Override
    public Optional<Inventario> buscarId(Integer id) {
        return repoInventario.findById(id);
    }

    @Override
    public Inventario guardar(Inventario inventario) {
        // 1) Verificar que exista el almacén antes de guardar
        Integer idAlmacen = inventario.getAlmacen().getIdalmacen();
        Almacenes almacen = repoAlmacenes.findById(idAlmacen)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Almacén no encontrado id=" + idAlmacen
            ));
        inventario.setAlmacen(almacen);

        // 2) Verificar que NO exista otro inventario con el mismo almacen + nombre
        String nombre = inventario.getNombre().trim();
        Optional<Inventario> existente = repoInventario
            .findByAlmacenIdalmacenAndNombreIgnoreCase(idAlmacen, nombre);
        if (existente.isPresent()) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Ya existe un inventario con nombre '" + nombre +
                "' en el almacén id=" + idAlmacen
            );
        }
        // 3) Si no existe duplicado, guardar normalmente
        return repoInventario.save(inventario);
    }


    @Override
    public Inventario modificar(Inventario inventario) {
        // 1) Verificar que el inventario exista
        Integer idInv = inventario.getIdinventario();
        Inventario existente = repoInventario.findById(idInv)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Inventario no encontrado id=" + idInv
            ));

        // 2) Verificar/actualizar almacén si cambió
        Integer idAlmacenNuevo = inventario.getAlmacen().getIdalmacen();
        Almacenes almacenNuevo = repoAlmacenes.findById(idAlmacenNuevo)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Almacén no encontrado id=" + idAlmacenNuevo
            ));

        // 3) Validar duplicados nombre + almacén si cambió alguno
        String nuevoNombre = inventario.getNombre().trim();
        boolean cambióNombre = !existente.getNombre().equalsIgnoreCase(nuevoNombre);
        boolean cambióAlmacen = !existente.getAlmacen().getIdalmacen().equals(idAlmacenNuevo);

        if (cambióNombre || cambióAlmacen) {
            Optional<Inventario> otro = repoInventario
                .findByAlmacenIdalmacenAndNombreIgnoreCase(idAlmacenNuevo, nuevoNombre);
            if (otro.isPresent() && !otro.get().getIdinventario().equals(idInv)) {
                throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Ya existe un inventario con nombre '" + nuevoNombre +
                    "' en el almacén id=" + idAlmacenNuevo
                );
            }
        }

        // 4) Asignar valores nuevos
        existente.setAlmacen(almacenNuevo);
        existente.setNombre(nuevoNombre);
        existente.setDescripcion(inventario.getDescripcion());
        // El campo estado se maneja vía soft-delete; no lo tocamos aquí

        return repoInventario.save(existente);
    }


    @Override
    public void eliminar(Integer id) {
        // Soft-delete: actualizará estado = 0 gracias a @SQLDelete en la entidad
        if (!repoInventario.existsById(id)) {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Inventario no encontrado id=" + id
            );
        }
        repoInventario.deleteById(id);
    }
}
