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

import io.swagger.v3.oas.annotations.tags.Tag;
@Tag(name = "Inventario - Almacenes")
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
        Almacenes e = service.buscarId(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Almacén no encontrado id=" + id
            ));
        return toDTO(e);
    }

    @GetMapping("/almacenes/sucursal/{idSucursal}")
    public List<AlmacenesDTO> listarPorSucursal(@PathVariable Integer idSucursal) {
        return service.buscarTodosPorSucursal(idSucursal)   // o como se llame tu service
              .stream()
              .map(this::toDTO)
              .collect(Collectors.toList());
    }

    @PostMapping("/almacenes")
    public ResponseEntity<AlmacenesDTO> crear(@RequestBody AlmacenesDTO dto) {
        Sucursales suc = sucursalesService.buscarId(dto.getIdSucursal())
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Sucursal no encontrada id=" + dto.getIdSucursal()
            ));

        Almacenes a = new Almacenes();
        a.setNombre(dto.getNombre());
        a.setDescripcion(dto.getDescripcion());
        a.setDireccion(dto.getDireccion());
        a.setEncargado(dto.getEncargado());
        a.setEstado(dto.getEstado());
        a.setSucursal(suc);

        Almacenes creado = service.guardar(a);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(creado));
    }

    @PutMapping("/almacenes")
    public AlmacenesDTO actualizar(@RequestBody AlmacenesDTO dto) {
        Almacenes a = new Almacenes();
        a.setIdalmacen(dto.getIdalmacen());
        a.setNombre(dto.getNombre());
        a.setDescripcion(dto.getDescripcion());
        a.setDireccion(dto.getDireccion());
        a.setEncargado(dto.getEncargado());
        a.setEstado(dto.getEstado());
        Sucursales suc = sucursalesService.buscarId(dto.getIdSucursal())
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Sucursal no encontrada id=" + dto.getIdSucursal()
            ));
        a.setSucursal(suc);

        return toDTO(service.modificar(a));
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
