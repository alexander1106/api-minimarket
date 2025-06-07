package com.gadbacorp.api.service.jpa.inventario;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.gadbacorp.api.entity.administrable.Sucursales;
import com.gadbacorp.api.entity.empleados.Empleado;
import com.gadbacorp.api.entity.inventario.Almacenes;
import com.gadbacorp.api.repository.administrable.SucursalesRepository;
import com.gadbacorp.api.repository.empleados.EmpleadosRepository;
import com.gadbacorp.api.repository.inventario.AlmacenesRepository;
import com.gadbacorp.api.service.inventario.IAlmacenesService;

@Service
public class AlmacenesService implements IAlmacenesService {

    @Autowired
    private AlmacenesRepository repoAlmacenes;

    @Autowired
    private SucursalesRepository repoSucursales;

    @Autowired
    private EmpleadosRepository repoEmpleados;

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
        String nombre = almacen.getNombre().trim();
        String direccion = almacen.getDireccion().trim();

        // 1) Validar duplicado de nombre
        repoAlmacenes.findByNombreIgnoreCase(nombre)
            .ifPresent(a -> { throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Ya existe un almacén con el nombre '" + nombre + "'"
            ); });

        // 2) Validar duplicado de dirección
        repoAlmacenes.findByDireccionIgnoreCase(direccion)
            .ifPresent(a -> { throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Ya existe un almacén con la dirección '" + direccion + "'"
            ); });

        // 3) Asignar valores básicos
        almacen.setNombre(nombre);
        almacen.setDireccion(direccion);

        // 4) Vincular la sucursal
        Integer idSuc = almacen.getSucursal().getIdSucursal();
        Sucursales suc = repoSucursales.findById(idSuc)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Sucursal no encontrada id=" + idSuc
            ));
        almacen.setSucursal(suc);

        // 5) Vincular el empleado encargado
        Integer idEmp = almacen.getEncargado().getIdEmpleado();
        Empleado emp = repoEmpleados.findById(idEmp)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Empleado no encontrado id=" + idEmp
            ));
        almacen.setEncargado(emp);

        return repoAlmacenes.save(almacen);
    }

    @Override
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
            repoAlmacenes.findByNombreIgnoreCase(nuevoNombre)
                .filter(a -> !a.getIdalmacen().equals(id))
                .ifPresent(a -> { throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Ya existe un almacén con el nombre '" + nuevoNombre + "'"
                ); });
        }

        // 2) Validar duplicado de dirección si cambió
        if (!existente.getDireccion().equalsIgnoreCase(nuevaDireccion)) {
            repoAlmacenes.findByDireccionIgnoreCase(nuevaDireccion)
                .filter(a -> !a.getIdalmacen().equals(id))
                .ifPresent(a -> { throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Ya existe un almacén con la dirección '" + nuevaDireccion + "'"
                ); });
        }

        // 3) Asignar valores actualizados
        existente.setNombre(nuevoNombre);
        existente.setDescripcion(almacen.getDescripcion());
        existente.setDireccion(nuevaDireccion);

        // 4) Actualizar la sucursal
        Integer idSuc = almacen.getSucursal().getIdSucursal();
        Sucursales suc = repoSucursales.findById(idSuc)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Sucursal no encontrada id=" + idSuc
            ));
        existente.setSucursal(suc);

        // 5) Actualizar el empleado encargado
        Integer idEmp = almacen.getEncargado().getIdEmpleado();
        Empleado emp = repoEmpleados.findById(idEmp)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Empleado no encontrado id=" + idEmp
            ));
        existente.setEncargado(emp);

        return repoAlmacenes.save(existente);
    }

    @Override
    public Optional<Almacenes> buscarId(Integer id) {
        return repoAlmacenes.findById(id);
    }

    @Override
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
