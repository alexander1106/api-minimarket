// src/main/java/com/gadbacorp/api/service/jpa/inventario/AlmacenesService.java
package com.gadbacorp.api.service.jpa.inventario;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.gadbacorp.api.entity.inventario.Almacenes;
import com.gadbacorp.api.repository.inventario.AlmacenesRepository;
import com.gadbacorp.api.repository.inventario.InventarioRepository;
import com.gadbacorp.api.service.inventario.IAlmacenesService;

@Service
public class AlmacenesService implements IAlmacenesService {

    @Autowired
    private AlmacenesRepository repoAlmacenes;

    @Autowired
    private InventarioRepository inventarioRepo;

    @Override
    public Optional<Almacenes> buscarPorNombre(String nombre) {
        return repoAlmacenes.findByNombreIgnoreCase(nombre.trim());
    }

    @Override
    public List<Almacenes> buscarTodos() {
        return repoAlmacenes.findAll();
    }

    @Override
    public Optional<Almacenes> buscarId(Integer id) {
        return repoAlmacenes.findById(id);
    }

    @Override
    public Almacenes guardar(Almacenes almacen) {
        // 1) Validar nombre no vacío
        if (almacen.getNombre()==null || almacen.getNombre().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre es obligatorio");
        }
        // 2) Validar duplicado
        repoAlmacenes.findByNombreIgnoreCase(almacen.getNombre().trim())
            .ifPresent(existing -> {
                throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ya existe un almacén con ese nombre"
                );
            });
        return repoAlmacenes.save(almacen);
    }

    @Override
    public Almacenes modificar(Almacenes almacen) {
        // 1) Existe?
        Almacenes existente = repoAlmacenes.findById(almacen.getIdalmacen())
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Almacén no encontrado id=" + almacen.getIdalmacen()
            ));
        // 2) Nombre no vacío
        if (almacen.getNombre()==null || almacen.getNombre().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre es obligatorio");
        }
        // 3) Duplicado distinto
        repoAlmacenes.findByNombreIgnoreCase(almacen.getNombre().trim())
            .filter(other -> !other.getIdalmacen().equals(almacen.getIdalmacen()))
            .ifPresent(other -> {
                throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ya existe un almacén con ese nombre"
                );
            });
        // 4) Aplicar cambios
        existente.setNombre(almacen.getNombre().trim());
        existente.setDescripcion(almacen.getDescripcion());
        existente.setDireccion(almacen.getDireccion());
        existente.setEncargado(almacen.getEncargado());
        existente.setEstado(almacen.getEstado());
        existente.setSucursal(almacen.getSucursal());
        return repoAlmacenes.save(existente);
    }

    @Override
    public void eliminar(Integer id) {
        // 1) Existe?
        Almacenes existente = repoAlmacenes.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Almacén no encontrado id=" + id
            ));
        // 2) Validar inventario asociado
        if (inventarioRepo.existsByAlmacen_Idalmacen(id)) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "No se puede eliminar: el almacén tiene inventario asociado"
            );
        }
        // 3) Eliminar
        repoAlmacenes.delete(existente);
    }
}
