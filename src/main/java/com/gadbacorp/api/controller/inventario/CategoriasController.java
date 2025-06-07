package com.gadbacorp.api.controller.inventario;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
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

    private CategoriasDTO toDTO(Categorias c) {
        CategoriasDTO dto = new CategoriasDTO(
            c.getIdcategoria(),
            c.getNombre(),
            c.getImagen()
        );
        List<Integer> ids = c.getProductos()
                              .stream()
                              .map(p -> p.getIdproducto())
                              .collect(Collectors.toList());
        dto.setProductos(ids);
        return dto;
    }

    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<List<CategoriasDTO>> listarTodas() {
        // usamos el método del servicio que ya devuelve DTOs
        List<CategoriasDTO> lista = serviceCategorias.listarConProductos();
        return ResponseEntity.ok(lista);
    }


    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<CategoriasDTO> obtenerPorId(@PathVariable Integer id) {
        Optional<Categorias> opt = serviceCategorias.buscarId(id);
        return opt
            .map(c -> ResponseEntity.ok(toDTO(c)))
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> guardar(@RequestBody CategoriasDTO dto) {
        try {
            Categorias ent = new Categorias();
            ent.setNombre(dto.getNombre());
            ent.setImagen(dto.getImagen());
            Categorias saved = serviceCategorias.guardar(ent);
            return ResponseEntity.ok(toDTO(saved));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping(path = "/imagen", consumes = "multipart/form-data")
    public ResponseEntity<?> guardarConImagen(
        @RequestParam("nombre") String nombre,
        @RequestParam(value = "imagen", required = false) MultipartFile imagen
    ) {
        try {
            String archivo = null;
            if (imagen != null && !imagen.isEmpty()) {
                archivo = System.currentTimeMillis() + "_" + imagen.getOriginalFilename();
                Path target = Paths.get("uploads/categorias/", archivo);
                Files.createDirectories(target.getParent());
                Files.copy(imagen.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            }
            Categorias ent = new Categorias();
            ent.setNombre(nombre);
            ent.setImagen(archivo);
            Categorias saved = serviceCategorias.guardar(ent);
            return ResponseEntity.ok(toDTO(saved));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error al crear categoría: " + e.getMessage());
        }
    }


    @PutMapping
    public ResponseEntity<?> modificar(@RequestBody CategoriasDTO dto) {
        try {
            Optional<Categorias> opt = serviceCategorias.buscarId(dto.getIdcategoria());
            if (opt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            Categorias ent = opt.get();
            ent.setNombre(dto.getNombre());
            ent.setImagen(dto.getImagen());
            Categorias updated = serviceCategorias.modificar(ent);
            return ResponseEntity.ok(toDTO(updated));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping(path = "/imagen", consumes = "multipart/form-data")
    public ResponseEntity<?> modificarConImagen(
        @RequestParam("idcategoria") Integer idcategoria,
        @RequestParam("nombre") String nombre,
        @RequestParam(value = "imagen", required = false) MultipartFile imagen
    ) {
        try {
            Optional<Categorias> opt = serviceCategorias.buscarId(idcategoria);
            if (opt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            Categorias ent = opt.get();
            ent.setNombre(nombre);

            if (imagen != null && !imagen.isEmpty()) {
                String archivo = System.currentTimeMillis() + "_" + imagen.getOriginalFilename();
                Path target = Paths.get("uploads/categorias/", archivo);
                Files.createDirectories(target.getParent());
                Files.copy(imagen.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
                ent.setImagen(archivo);
            }

            Categorias updated = serviceCategorias.modificar(ent);
            return ResponseEntity.ok(toDTO(updated));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error al modificar categoría: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Integer id) {
        Optional<Categorias> opt = serviceCategorias.buscarId(id);
        if (opt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        serviceCategorias.eliminar(id);
        return ResponseEntity.ok("Categoría eliminada correctamente");
    }
}
