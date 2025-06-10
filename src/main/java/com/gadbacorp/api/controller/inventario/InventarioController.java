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

import com.gadbacorp.api.entity.inventario.Almacenes;
import com.gadbacorp.api.entity.inventario.Inventario;
import com.gadbacorp.api.entity.inventario.InventarioDTO;
import com.gadbacorp.api.repository.inventario.AlmacenesRepository;
import com.gadbacorp.api.repository.inventario.InventarioRepository;

@RestController
@RequestMapping("/api/minimarket")
public class InventarioController {

    @Autowired private InventarioRepository inventarioRepo;
    @Autowired private AlmacenesRepository almacenesRepo;

    private InventarioDTO toDTO(Inventario inv) {
        InventarioDTO dto = new InventarioDTO();
        dto.setIdinventario(inv.getIdinventario());
        dto.setIdalmacen(inv.getAlmacen().getIdalmacen());
        dto.setNombre(inv.getNombre());
        dto.setDescripcion(inv.getDescripcion());
        return dto;
    }

    private Inventario toEntity(InventarioDTO dto) {
        Inventario inv = new Inventario();
        if (dto.getIdinventario() != null) inv.setIdinventario(dto.getIdinventario());
        inv.setNombre(dto.getNombre());
        inv.setDescripcion(dto.getDescripcion());
        Almacenes almacen = new Almacenes();
        almacen.setIdalmacen(dto.getIdalmacen());
        inv.setAlmacen(almacen);
        return inv;
    }

    @GetMapping("/inventario")
    public List<InventarioDTO> listar() {
        return inventarioRepo.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @GetMapping("/inventario/{id}")
    public ResponseEntity<InventarioDTO> obtener(@PathVariable Integer id) {
        Inventario inventario = inventarioRepo.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Inventario no encontrado id=" + id));

        return ResponseEntity.ok(toDTO(inventario));
    }

    @PostMapping("/inventario")
    public ResponseEntity<?> crear(@RequestBody InventarioDTO dto) {
        Integer idAlmacen = dto.getIdalmacen();

        Almacenes almacen = almacenesRepo.findById(idAlmacen)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Almacén no encontrado id=" + idAlmacen));

        Inventario entidad = toEntity(dto);
        entidad.setAlmacen(almacen);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(inventarioRepo.save(entidad)));
    }

    @PutMapping("/inventario")
    public ResponseEntity<?> actualizar(@RequestBody InventarioDTO dto) {
        Integer idInv = dto.getIdinventario();
        Integer idAlmacenNuevo = dto.getIdalmacen();

        Inventario existente = inventarioRepo.findById(idInv)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Inventario no encontrado id=" + idInv));

        Almacenes nuevoAlmacen = almacenesRepo.findById(idAlmacenNuevo)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Almacén no encontrado id=" + idAlmacenNuevo));

        existente.setAlmacen(nuevoAlmacen);
        existente.setNombre(dto.getNombre().trim());
        existente.setDescripcion(dto.getDescripcion());

        return ResponseEntity.ok(toDTO(inventarioRepo.save(existente)));
    }

    @DeleteMapping("/inventario/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Integer id) {
        inventarioRepo.deleteById(id);
        return ResponseEntity.ok("Inventario eliminado correctamente");
    }
}
