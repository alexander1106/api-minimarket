package com.gadbacorp.api.controller.inventario;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.gadbacorp.api.entity.inventario.Categorias;
import com.gadbacorp.api.entity.inventario.CategoriasDTO;
import com.gadbacorp.api.service.inventario.ICategoriasService;

@RestController
@RequestMapping("/api/minimarket/categorias")
@CrossOrigin("*")
public class CategoriasController {

    @Autowired
    private ICategoriasService serviceCategorias;

    // Conversión Entity -> DTO
    private CategoriasDTO toDTO(Categorias entity) {
        return new CategoriasDTO(entity.getIdcategoria(), entity.getNombre(), entity.getImagen());
    }

    // Conversión DTO -> Entity
    private Categorias toEntity(CategoriasDTO dto) {
        Categorias entity = new Categorias();
        entity.setIdcategoria(dto.getIdcategoria());
        entity.setNombre(dto.getNombre());
        return entity;
    }

    // Listar todas las categorías
    @GetMapping
    public List<CategoriasDTO> listarTodos() {
        return serviceCategorias.buscarTodos()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // Buscar categoría por ID
    @GetMapping("/{id}")
    public ResponseEntity<CategoriasDTO> buscarPorId(@PathVariable Integer id) {
        Optional<Categorias> categoria = serviceCategorias.buscarId(id);
        return categoria.map(value -> ResponseEntity.ok(toDTO(value)))
                        .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Crear nueva categoría
    @PostMapping
    public ResponseEntity<?> guardar(@RequestBody CategoriasDTO dto) {
    try {
        Categorias guardada = serviceCategorias.guardar(toEntity(dto));
        return ResponseEntity.ok(toDTO(guardada));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Modificar categoría existente
    @PutMapping
    public ResponseEntity<?> modificar(@RequestBody CategoriasDTO dto) {
    try {
        Optional<Categorias> existente = serviceCategorias.buscarId(dto.getIdcategoria());

        if (existente.isPresent()) {
            Categorias actualizada = serviceCategorias.modificar(toEntity(dto));
            return ResponseEntity.ok(toDTO(actualizada));
        } else {
            return ResponseEntity.notFound().build();
        }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    // Eliminar (lógico) por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Integer id) {
        Optional<Categorias> existente = serviceCategorias.buscarId(id);
        if (existente.isPresent()) {
            serviceCategorias.eliminar(id);
            return ResponseEntity.ok("Categoría eliminada correctamente");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
