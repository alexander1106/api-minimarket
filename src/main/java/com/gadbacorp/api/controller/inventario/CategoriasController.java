package com.gadbacorp.api.controller.inventario;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.gadbacorp.api.repository.inventario.ProductosRepository;
import com.gadbacorp.api.service.inventario.ICategoriasService;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/minimarket")
public class CategoriasController {

    @Autowired
    private ICategoriasService service;

    @Autowired
    private ProductosRepository productoRepo;

    private CategoriasDTO toDTO(Categorias c) {
        return new CategoriasDTO(c.getIdcategoria(), c.getNombre(), c.getImagen());
    }

    @GetMapping("/categorias")
    public List<Map<String, Object>> listar() {
        return service.buscarTodos().stream().map(c -> {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("idcategoria", c.getIdcategoria());
            map.put("nombre", c.getNombre());
            map.put("imagen", c.getImagen());

            List<Map<String, Object>> productos = productoRepo.findByCategoria_Idcategoria(c.getIdcategoria())
                .stream().map(p -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("idproducto", p.getIdproducto());
                    m.put("nombre", p.getNombre());
                    m.put("descripcion", p.getDescripcion());
                    return m;
                }).collect(Collectors.toList());

            map.put("productos", productos);
            return map;
        }).collect(Collectors.toList());
    }

    @GetMapping("/categorias/{id}")
    public Map<String, Object> obtenerPorId(@PathVariable Integer id) {
        Categorias c = service.buscarId(id).orElse(new Categorias());
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("idcategoria", c.getIdcategoria());
        map.put("nombre", c.getNombre());
        map.put("imagen", c.getImagen());

        List<Map<String, Object>> productos = productoRepo.findByCategoria_Idcategoria(c.getIdcategoria())
            .stream().map(p -> {
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("idproducto", p.getIdproducto());
                m.put("nombre", p.getNombre());
                m.put("descripcion", p.getDescripcion());
                return m;
            }).collect(Collectors.toList());

        map.put("productos", productos);
        return map;
    }

    @PostMapping("/categorias")
    public CategoriasDTO guardar(@RequestBody CategoriasDTO dto) {
        Categorias c = new Categorias();
        c.setNombre(dto.getNombre());
        c.setImagen(dto.getImagen());
        return toDTO(service.guardar(c));
    }

    @PutMapping("/categorias")
    public CategoriasDTO modificar(@RequestBody CategoriasDTO dto) {
        Categorias c = service.buscarId(dto.getIdcategoria()).orElse(new Categorias());
        c.setNombre(dto.getNombre());
        c.setImagen(dto.getImagen());
        return toDTO(service.modificar(c));
    }

    @DeleteMapping("/categorias/{id}")
    public String eliminar(@PathVariable Integer id) {
        service.eliminar(id);
        return "Categoría eliminada";
    }
}