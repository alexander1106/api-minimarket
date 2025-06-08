package com.gadbacorp.api.service.jpa.inventario;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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

    public List<Almacenes> buscarTodos() {
        return repoAlmacenes.findAll();
    }

    public Almacenes guardar(Almacenes almacen) {
        String nombre = almacen.getNombre().trim();
        String direccion = almacen.getDireccion().trim();

        // 1) Validar que no exista otro almacén con el mismo nombre
        Optional<Almacenes> porNombre = repoAlmacenes.findByNombreIgnoreCase(nombre);
        if (porNombre.isPresent()) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Ya existe un almacén con el nombre '" + nombre + "'"
            );
        }

        // 2) Validar que no exista otro almacén con la misma dirección
        Optional<Almacenes> porDireccion = repoAlmacenes.findByDireccionIgnoreCase(direccion);
        if (porDireccion.isPresent()) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Ya existe un almacén con la dirección '" + direccion + "'"
            );
        }

        almacen.setNombre(nombre);
        almacen.setDireccion(direccion);
        return repoAlmacenes.save(almacen);
    }

    public Almacenes modificar(Almacenes almacen) {
        Integer id = almacen.getIdalmacen();
        Almacenes existente = repoAlmacenes.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Almacén no encontrado id=" + id
            ));

        String nuevoNombre = almacen.getNombre().trim();
        String nuevaDireccion = almacen.getDireccion().trim();

        // 1) Validar duplicado de nombre si cambió
        if (!existente.getNombre().equalsIgnoreCase(nuevoNombre)) {
            Optional<Almacenes> otroPorNombre = repoAlmacenes.findByNombreIgnoreCase(nuevoNombre);
            if (otroPorNombre.isPresent() && !otroPorNombre.get().getIdalmacen().equals(id)) {
                throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Ya existe un almacén con el nombre '" + nuevoNombre + "'"
                );
            }
        }

        // 2) Validar duplicado de dirección si cambió
        if (!existente.getDireccion().equalsIgnoreCase(nuevaDireccion)) {
            Optional<Almacenes> otroPorDireccion = repoAlmacenes.findByDireccionIgnoreCase(nuevaDireccion);
            if (otroPorDireccion.isPresent() && !otroPorDireccion.get().getIdalmacen().equals(id)) {
                throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Ya existe un almacén con la dirección '" + nuevaDireccion + "'"
                );
            }
        }

        // 3) Asignar valores actualizados
        existente.setNombre(nuevoNombre);
        existente.setDescripcion(almacen.getDescripcion());
        existente.setDireccion(nuevaDireccion);
        existente.setEncargado(almacen.getEncargado());
        // El campo estado se maneja vía soft‐delete; no lo modificamos aquí

        return repoAlmacenes.save(existente);
    }

    public Optional<Almacenes> buscarId(Integer id) {
        return repoAlmacenes.findById(id);
    }

    public void eliminar(Integer id) {
        if (!repoAlmacenes.existsById(id)) {
            
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Almacén no encontrado id=" + id
            );
        }
        repoAlmacenes.deleteById(id);
    }
}
