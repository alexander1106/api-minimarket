package com.gadbacorp.api.gadbacorp.controller.inventario;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

import com.gadbacorp.api.gadbacorp.entity.administrable.Sucursales;
import com.gadbacorp.api.gadbacorp.entity.inventario.Almacenes;
import com.gadbacorp.api.gadbacorp.entity.inventario.AlmacenesDTO;
import com.gadbacorp.api.gadbacorp.service.administrable.ISucursalesService;
import com.gadbacorp.api.gadbacorp.service.inventario.IAlmacenesService;

@RestController
@RequestMapping("/api/minimarket/almacenes")
public class AlmacenesController {

    @Autowired 
    private IAlmacenesService serviceAlmacenes;

    @Autowired
    private ISucursalesService serviceSucursales;

    @GetMapping
    public List<AlmacenesDTO> buscartodos() {
        return serviceAlmacenes.buscarTodos().stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public AlmacenesDTO buscarId(@PathVariable Integer id) {
        Almacenes entidad = serviceAlmacenes.buscarId(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, 
                "Almacén no encontrado id=" + id
            ));
        return toDTO(entidad);
    }

    @PostMapping
@ResponseStatus(HttpStatus.CREATED)
public AlmacenesDTO guardar(@RequestBody AlmacenesDTO dto) {
    // 4.1 Comprueba duplicado
    serviceAlmacenes.buscarPorNombre(dto.getNombre())
        .ifPresent(a -> {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Ya existe un almacén con nombre: " + dto.getNombre()
            );
        });

    // 4.2 Valida sucursal
    Sucursales suc = serviceSucursales.buscarId(dto.getIdSucursal())
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.BAD_REQUEST,
            "Sucursal no encontrada id=" + dto.getIdSucursal()
        ));

    // 4.3 Mapear DTO→Entidad y guardar
    Almacenes ent = new Almacenes();
    ent.setNombre(dto.getNombre());
    ent.setDescripcion(dto.getDescripcion());
    ent.setEstado(dto.getEstado());
    ent.setSucursal(suc);

    serviceAlmacenes.guardar(ent);
    return toDTO(ent);
}

    @PutMapping
    public AlmacenesDTO modificar(@RequestBody AlmacenesDTO dto) {
        // validar existencia de sucursal
        Sucursales suc = serviceSucursales.buscarId(dto.getIdSucursal())
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.BAD_REQUEST, 
                "Sucursal no encontrada id=" + dto.getIdSucursal()
            ));

        // mapear DTO → entidad (incluyendo el id para update)
        Almacenes ent = new Almacenes();
        ent.setIdalmacen(dto.getIdalmacen());
        ent.setNombre(dto.getNombre());
        ent.setDescripcion(dto.getDescripcion());
        ent.setEstado(dto.getEstado());
        ent.setSucursal(suc);

        serviceAlmacenes.modificar(ent);
        return toDTO(ent);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Integer id) {
        serviceAlmacenes.eliminar(id);
        return ResponseEntity
            .ok("Almacén eliminado correctamente");
    }

    // --- métodos privados de mapeo ---
    private AlmacenesDTO toDTO(Almacenes e) {
        AlmacenesDTO dto = new AlmacenesDTO();
        dto.setIdalmacen(e.getIdalmacen());
        dto.setNombre(e.getNombre());
        dto.setDescripcion(e.getDescripcion());
        dto.setEstado(e.getEstado());
        dto.setIdSucursal(e.getSucursal().getIdSucursal());
        return dto;
    }
}
