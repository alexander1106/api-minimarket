package com.gadbacorp.api.controller.inventario;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
        entity.setImagen(dto.getImagen());
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

    // Crear nueva categoría SIN imagen (JSON)
    @PostMapping
    public ResponseEntity<?> guardar(@RequestBody CategoriasDTO dto) {
        try {
            Categorias guardada = serviceCategorias.guardar(toEntity(dto));
            return ResponseEntity.ok(toDTO(guardada));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Crear nueva categoría CON IMAGEN
    @PostMapping(path = "/imagen", consumes = "multipart/form-data")
    public ResponseEntity<?> guardarConImagen(
            @RequestParam("nombre") String nombre,
            @RequestParam(value = "imagen", required = false) MultipartFile imagen) {
        try {
            // Procesar imagen
            String nombreArchivo = null;
            if (imagen != null && !imagen.isEmpty()) {
                nombreArchivo = System.currentTimeMillis() + "_" + imagen.getOriginalFilename();
                String rutaCarpeta = "uploads/categorias/";
                File carpeta = new File(rutaCarpeta);
                if (!carpeta.exists()) carpeta.mkdirs();

                Path rutaArchivo = Paths.get(rutaCarpeta, nombreArchivo);
                Files.copy(imagen.getInputStream(), rutaArchivo, StandardCopyOption.REPLACE_EXISTING);
            }

            // Crear entidad
            Categorias categoria = new Categorias();
            categoria.setNombre(nombre);
            categoria.setImagen(nombreArchivo);

            Categorias guardada = serviceCategorias.guardar(categoria);
            return ResponseEntity.ok(toDTO(guardada));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error al crear categoría: " + e.getMessage());
        }
    }

    // Modificar categoría existente SIN imagen (JSON)
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

    // Modificar categoría existente CON IMAGEN
    @PutMapping(path = "/imagen", consumes = "multipart/form-data")
    public ResponseEntity<?> modificarConImagen(
        @RequestParam("idcategoria") Integer idcategoria,
        @RequestParam("nombre") String nombre,
        @RequestParam(value = "imagen", required = false) MultipartFile imagen) {
        try {
            Optional<Categorias> catOpt = serviceCategorias.buscarId(idcategoria);
            if (catOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Categorias categoria = catOpt.get();
            categoria.setNombre(nombre);

            if (imagen != null && !imagen.isEmpty()) {
                // Guardar la nueva imagen
                String nombreArchivo = System.currentTimeMillis() + "_" + imagen.getOriginalFilename();
                String rutaCarpeta = "uploads/categorias/";
                File carpeta = new File(rutaCarpeta);
                if (!carpeta.exists()) carpeta.mkdirs();

                Path rutaArchivo = Paths.get(rutaCarpeta, nombreArchivo);
                Files.copy(imagen.getInputStream(), rutaArchivo, StandardCopyOption.REPLACE_EXISTING);

                categoria.setImagen(nombreArchivo);
            }
            // Si no mandas imagen, deja la anterior

            Categorias guardada = serviceCategorias.modificar(categoria);
            return ResponseEntity.ok(new CategoriasDTO(
                guardada.getIdcategoria(),
                guardada.getNombre(),
                guardada.getImagen()
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error al modificar categoría: " + e.getMessage());
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
