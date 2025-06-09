package com.gadbacorp.api.controller.inventario;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
import com.gadbacorp.api.entity.inventario.Productos;
import com.gadbacorp.api.repository.inventario.ProductosRepository;
import com.gadbacorp.api.service.inventario.ICategoriasService;

@RestController
@RequestMapping("/api/minimarket/categorias")
@CrossOrigin("*")
public class CategoriasController {

    @Autowired
    private ICategoriasService serviceCategorias;


    // 1) Inyecta el repositorio de productos
    @Autowired
    private ProductosRepository productoRepo;


    /** Convierte una entidad Categoria en un DTO sencillo */
    private CategoriasDTO toDTO(Categorias c) {
        return new CategoriasDTO(
            c.getIdcategoria(),
            c.getNombre(),
            c.getImagen()
        );
    }

    /** Nuevo método: devuelve lista de maps que incluyen productos */
    @GetMapping
    public ResponseEntity<List<Map<String,Object>>> listarTodasConProductos() {
        List<Categorias> categorias = serviceCategorias.buscarTodos();

        List<Map<String,Object>> respuesta = categorias.stream().map(c -> {
            Map<String,Object> mapCat = new LinkedHashMap<>();
            mapCat.put("idcategoria", c.getIdcategoria());
            mapCat.put("nombre",      c.getNombre());
            mapCat.put("imagen",      c.getImagen());

            // 2) Trae productos relacionados
            List<Productos> prods = productoRepo.findByCategoria_Idcategoria(c.getIdcategoria());
            // 3) Mapea sólo los campos que quieras
            List<Map<String,Object>> listaProds = prods.stream().map(p -> {
                Map<String,Object> m = new LinkedHashMap<>();
                m.put("idproducto", p.getIdproducto());
                m.put("nombre",     p.getNombre());
                m.put("descripcion",     p.getDescripcion());
                m.put("fechaVencimiento",     p.getFechaVencimiento());
                m.put("tipoImpuesto",     p.getTipoImpuesto());
                m.put("costoCompra",     p.getCostoCompra());
                m.put("costoventa",     p.getCostoVenta());
                m.put("costoMayor",     p.getCostoMayor());
                m.put("imagen",     p.getImagen());

                return m;
            }).collect(Collectors.toList());

            mapCat.put("productos", listaProds);
            return mapCat;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(respuesta);
    }

    /** Similar para obtener una sola categoría */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String,Object>> obtenerPorIdConProductos(@PathVariable Integer id) {
        Optional<Categorias> opt = serviceCategorias.buscarId(id);
        if (opt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Categorias c = opt.get();
        Map<String,Object> mapCat = new LinkedHashMap<>();
        mapCat.put("idcategoria", c.getIdcategoria());
        mapCat.put("nombre",      c.getNombre());
        mapCat.put("imagen",      c.getImagen());

        List<Productos> prods = productoRepo.findByCategoria_Idcategoria(c.getIdcategoria());
        List<Map<String,Object>> listaProds = prods.stream().map(p -> {
            Map<String,Object> m = new LinkedHashMap<>();
            m.put("idproducto", p.getIdproducto());
            m.put("nombre",     p.getNombre());
            m.put("descripcion",     p.getDescripcion());
            m.put("fechaVencimiento",     p.getFechaVencimiento());
            m.put("tipoImpuesto",     p.getTipoImpuesto());
            m.put("costoCompra",     p.getCostoCompra());
            m.put("costoventa",     p.getCostoVenta());
            m.put("costoMayor",     p.getCostoMayor());
            m.put("imagen",     p.getImagen());
            return m;
        }).collect(Collectors.toList());

        mapCat.put("productos", listaProds);
        return ResponseEntity.ok(mapCat);
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
