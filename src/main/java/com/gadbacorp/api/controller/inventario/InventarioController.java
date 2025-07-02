// src/main/java/com/gadbacorp/api/controller/inventario/InventarioController.java
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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.gadbacorp.api.entity.inventario.Almacenes;
import com.gadbacorp.api.entity.inventario.Inventario;
import com.gadbacorp.api.entity.inventario.InventarioDTO;
import com.gadbacorp.api.repository.inventario.AlmacenesRepository;
import com.gadbacorp.api.service.inventario.IInventarioService;

@RestController
@RequestMapping("/api/minimarket")
@CrossOrigin(origins = "http://localhost:4200")
public class InventarioController {

    @Autowired private IInventarioService inventarioService;
    @Autowired private AlmacenesRepository     almacenesRepo;

    private InventarioDTO toDTO(Inventario inv) {
        InventarioDTO dto = new InventarioDTO();
        dto.setIdinventario(inv.getIdinventario());
        dto.setIdalmacen   (inv.getAlmacen().getIdalmacen());
        dto.setNombre      (inv.getNombre());
        dto.setDescripcion (inv.getDescripcion());
        return dto;
    }

    private Inventario toEntity(InventarioDTO dto) {
    Inventario inv = new Inventario();
    // Sólo le seteamos el id si viene un entero válido (>0):
    if (dto.getIdinventario() != null && dto.getIdinventario() > 0) {
        inv.setIdinventario(dto.getIdinventario());
    }
    inv.setNombre(dto.getNombre());
    inv.setDescripcion(dto.getDescripcion());
    Almacenes alm = almacenesRepo.findById(dto.getIdalmacen())
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.BAD_REQUEST, "Almacén no encontrado id=" + dto.getIdalmacen()));
    inv.setAlmacen(alm);
    return inv;
}


    @GetMapping("/inventario")
    public List<InventarioDTO> listar() {
        return inventarioService.buscarTodos()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/inventario/{id}")
    public ResponseEntity<InventarioDTO> obtener(@PathVariable Integer id) {
        Inventario inv = inventarioService.buscarId(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Inventario no encontrado id=" + id));
        return ResponseEntity.ok(toDTO(inv));
    }

    @PostMapping("/inventario")
    public ResponseEntity<InventarioDTO> crear(@RequestBody InventarioDTO dto) {
        Inventario entidad = toEntity(dto);
        Inventario guardado = inventarioService.guardar(entidad);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(guardado));
    }

    @PutMapping("/inventario")
    public ResponseEntity<InventarioDTO> actualizar(@RequestBody InventarioDTO dto) {
        // valida existencia
        inventarioService.buscarId(dto.getIdinventario())
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Inventario no encontrado id=" + dto.getIdinventario()));
        Inventario entidad = toEntity(dto);
        Inventario modificado = inventarioService.modificar(entidad);
        return ResponseEntity.ok(toDTO(modificado));
    }

    @DeleteMapping("/inventario/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Integer id) {
        // aquí invocamos la eliminación con validación en el service
        inventarioService.eliminar(id);
        return ResponseEntity.ok("Inventario eliminado correctamente");
    }
}
