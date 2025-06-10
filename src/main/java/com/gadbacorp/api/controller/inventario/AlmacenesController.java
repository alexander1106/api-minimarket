package com.gadbacorp.api.controller.inventario;

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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.gadbacorp.api.entity.administrable.Sucursales;
import com.gadbacorp.api.entity.inventario.Almacenes;
import com.gadbacorp.api.entity.inventario.AlmacenesDTO;
import com.gadbacorp.api.service.administrable.ISucursalesService;
import com.gadbacorp.api.service.inventario.IAlmacenesService;

@RestController
@RequestMapping("/api/minimarket")
public class AlmacenesController {

    @Autowired private IAlmacenesService service;
    @Autowired private ISucursalesService sucursalesService;

    @GetMapping("/almacenes")
    public List<AlmacenesDTO> listar() {
        return service.buscarTodos()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/almacenes/{id}")
    public AlmacenesDTO obtener(@PathVariable Integer id) {
        Almacenes entidad = service.buscarId(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Almacén no encontrado id=" + id));
        return toDTO(entidad);
    }

    @PostMapping("/almacenes")
    public ResponseEntity<AlmacenesDTO> crear(@RequestBody AlmacenesDTO dto) {
        Sucursales sucursal = sucursalesService.buscarId(dto.getIdSucursal())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Sucursal no encontrada con ID: " + dto.getIdSucursal()));

        Almacenes entidad = new Almacenes();
        entidad.setIdalmacen(dto.getIdalmacen());
        entidad.setNombre(dto.getNombre() != null ? dto.getNombre().trim() : null);
        entidad.setDescripcion(dto.getDescripcion());
        entidad.setEncargado(dto.getEncargado() != null? dto.getEncargado().trim() : null);
        entidad.setDireccion(dto.getDireccion() != null ? dto.getDireccion().trim() : null);
        entidad.setEstado(dto.getEstado());
        entidad.setSucursal(sucursal);

        Almacenes almacenGuardado = service.guardar(entidad);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(almacenGuardado));
    }

    @PutMapping("/almacenes")
    public AlmacenesDTO actualizar(@RequestBody AlmacenesDTO dto) {
        Almacenes existente = service.buscarId(dto.getIdalmacen())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Almacén no encontrado id=" + dto.getIdalmacen()));

        Sucursales sucursal = sucursalesService.buscarId(dto.getIdSucursal())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Sucursal no encontrada con ID: " + dto.getIdSucursal()));

        existente.setNombre(dto.getNombre() != null ? dto.getNombre().trim() : null);
        existente.setDescripcion(dto.getDescripcion());
        existente.setEncargado(dto.getEncargado()!= null? dto.getEncargado().trim() : null);
        existente.setDireccion(dto.getDireccion() != null ? dto.getDireccion().trim() : null);
        existente.setEstado(dto.getEstado());
        existente.setSucursal(sucursal);

        return toDTO(service.modificar(existente));
    }

    @DeleteMapping("/almacenes/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Integer id) {
        service.eliminar(id);
        return ResponseEntity.ok("Almacén eliminado correctamente");
    }

    private AlmacenesDTO toDTO(Almacenes e) {
        AlmacenesDTO dto = new AlmacenesDTO();
        dto.setIdalmacen(e.getIdalmacen());
        dto.setNombre(e.getNombre());
        dto.setDescripcion(e.getDescripcion());
        dto.setDireccion(e.getDireccion());
        dto.setEncargado(e.getEncargado());
        dto.setEstado(e.getEstado());
        dto.setIdSucursal(e.getSucursal().getIdSucursal());
        return dto;
    }
}
