package com.gadbacorp.api.controller.inventario;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.gadbacorp.api.entity.administrable.Sucursales;
import com.gadbacorp.api.entity.empleados.Empleado;
import com.gadbacorp.api.entity.inventario.Almacenes;
import com.gadbacorp.api.entity.inventario.AlmacenesDTO;
import com.gadbacorp.api.service.administrable.ISucursalesService;
import com.gadbacorp.api.service.empleados.IEmpleadoServices;
import com.gadbacorp.api.service.inventario.IAlmacenesService;

@RestController
@RequestMapping("/api/minimarket/almacenes")
@CrossOrigin("*")
public class AlmacenesController {

    @Autowired
    private IAlmacenesService serviceAlmacenes;

    @Autowired
    private ISucursalesService serviceSucursales;

    @Autowired
    private IEmpleadoServices serviceEmpleados;

    /** GET /api/minimarket/almacenes */
    @GetMapping
    public List<AlmacenesDTO> listarTodos() {
        return serviceAlmacenes.buscarTodos()
            .stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    /** GET /api/minimarket/almacenes/{id} */
    @GetMapping("/{id}")
    public AlmacenesDTO buscarPorId(@PathVariable Integer id) {
        Almacenes ent = serviceAlmacenes.buscarId(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Almacén no encontrado id=" + id
            ));
        return toDTO(ent);
    }

    /** POST /api/minimarket/almacenes */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AlmacenesDTO guardar(@RequestBody AlmacenesDTO dto) {
        // validar duplicado por nombre
        serviceAlmacenes.buscarPorNombre(dto.getNombre().trim())
            .ifPresent(a -> {
                throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Ya existe un almacén con nombre: " + dto.getNombre()
                );
            });

        Almacenes ent = toEntity(dto);
        Almacenes guardado = serviceAlmacenes.guardar(ent);
        return toDTO(guardado);
    }

    /** PUT /api/minimarket/almacenes */
    @PutMapping
    public AlmacenesDTO modificar(@RequestBody AlmacenesDTO dto) {
        Almacenes ent = toEntity(dto);
        Almacenes actualizado = serviceAlmacenes.modificar(ent);
        return toDTO(actualizado);
    }

    /** DELETE /api/minimarket/almacenes/{id} */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Integer id) {
        serviceAlmacenes.eliminar(id);
        return ResponseEntity.ok("Almacén eliminado correctamente");
    }

    // ——— Métodos privados de mapeo ———

    private AlmacenesDTO toDTO(Almacenes e) {
        AlmacenesDTO dto = new AlmacenesDTO();
        dto.setIdalmacen(e.getIdalmacen());
        dto.setNombre(e.getNombre());
        dto.setDescripcion(e.getDescripcion());
        dto.setDireccion(e.getDireccion());
        dto.setEstado(e.getEstado());
        dto.setIdSucursal(e.getSucursal().getIdSucursal());
        dto.setIdEmpleado(e.getEncargado().getIdEmpleado());
        return dto;
    }

    private Almacenes toEntity(AlmacenesDTO dto) {
        Almacenes e = new Almacenes();
        e.setIdalmacen(dto.getIdalmacen());
        e.setNombre(dto.getNombre().trim());
        e.setDescripcion(dto.getDescripcion());
        e.setDireccion(dto.getDireccion().trim());
        e.setEstado(dto.getEstado());

        // vincular sucursal
        Sucursales suc = serviceSucursales.buscarId(dto.getIdSucursal())
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Sucursal no encontrada id=" + dto.getIdSucursal()
            ));
        e.setSucursal(suc);

        // vincular empleado encargado
        Empleado emp = serviceEmpleados.buscarId(dto.getIdEmpleado())
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Empleado no encontrado id=" + dto.getIdEmpleado()
            ));
        e.setEncargado(emp);

        return e;
    }
}
