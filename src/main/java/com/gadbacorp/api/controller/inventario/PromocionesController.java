package com.gadbacorp.api.controller.inventario;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gadbacorp.api.entity.inventario.Categorias;
import com.gadbacorp.api.entity.inventario.Productos;
import com.gadbacorp.api.entity.inventario.Promociones;
import com.gadbacorp.api.entity.inventario.PromocionesDTO;
import com.gadbacorp.api.repository.inventario.CategoriasRepository;
import com.gadbacorp.api.repository.inventario.ProductosRepository;
import com.gadbacorp.api.repository.inventario.PromocionesRepository;

@RestController
@RequestMapping("/api/minimarket")
public class PromocionesController {

    @Autowired
    private PromocionesRepository repoPromociones;

    @Autowired
    private ProductosRepository repoProductos;

    @Autowired
    private CategoriasRepository repoCategorias;

    @GetMapping("/promociones")
    public ResponseEntity<List<PromocionesDTO>> listar() {
        List<PromocionesDTO> lista = repoPromociones.findAll().stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/promociones/{id}")
    public ResponseEntity<PromocionesDTO> obtener(@PathVariable Integer id) {
        return repoPromociones.findById(id)
            .map(this::toDTO)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/promociones")
    public ResponseEntity<PromocionesDTO> crear(@RequestBody PromocionesDTO dto) {
        validarContenido(dto);
        Promociones promo = toEntity(dto);
        Promociones guardado = repoPromociones.save(promo);
        return ResponseEntity.status(201).body(toDTO(guardado));
    }

    @PutMapping("/promociones")
    public ResponseEntity<PromocionesDTO> actualizar(@RequestBody PromocionesDTO dto) {
        if (dto.getIdpromocion() == null) {
            return ResponseEntity.badRequest().build();
        }
        Promociones promo = repoPromociones.findById(dto.getIdpromocion())
            .orElseThrow();

        promo.setNombre(dto.getNombre());
        promo.setFechaInicio(dto.getFechaInicio());
        promo.setFechaFin(dto.getFechaFin());
        promo.setTipoDescuento(dto.getTipoDescuento());
        promo.setValor(dto.getValor());
        promo.getProductos().clear();
        if (dto.getProductos() != null) {
            for (Integer idProd : dto.getProductos()) {
                Productos prod = repoProductos.findById(idProd)
                    .orElseThrow();
                promo.getProductos().add(prod);
            }
        }
        promo.getCategorias().clear();
        if (dto.getCategorias() != null) {
            for (Integer idCat : dto.getCategorias()) {
                Categorias cat = repoCategorias.findById(idCat)
                    .orElseThrow();
                promo.getCategorias().add(cat);
            }
        }

        Promociones actualizado = repoPromociones.save(promo);
        return ResponseEntity.ok(toDTO(actualizado));
    }

    
    private void validarContenido(PromocionesDTO dto) {
        if ((dto.getProductos() == null || dto.getProductos().isEmpty()) &&
            (dto.getCategorias() == null || dto.getCategorias().isEmpty())) {
            throw new IllegalArgumentException("Debe especificar al menos un producto o categoría");
        }
    }
    private PromocionesDTO toDTO(Promociones p) {
        PromocionesDTO dto = new PromocionesDTO();
        dto.setIdpromocion(p.getIdpromocion());
        dto.setNombre(p.getNombre());
        dto.setFechaInicio(p.getFechaInicio());
        dto.setFechaFin(p.getFechaFin());
        dto.setTipoDescuento(p.getTipoDescuento());
        dto.setValor(p.getValor());
        dto.setProductos(p.getProductos().stream()
            .map(Productos::getIdproducto)
            .collect(Collectors.toList()));
        dto.setCategorias(p.getCategorias().stream()
            .map(Categorias::getIdcategoria)
            .collect(Collectors.toList()));
        return dto;
    }

    private Promociones toEntity(PromocionesDTO dto) {
        Promociones p = new Promociones();
        if (dto.getIdpromocion() != null) {
            p.setIdpromocion(dto.getIdpromocion());
        }
        p.setNombre(dto.getNombre());
        p.setFechaInicio(dto.getFechaInicio());
        p.setFechaFin(dto.getFechaFin());
        p.setTipoDescuento(dto.getTipoDescuento());
        p.setValor(dto.getValor());

        if (dto.getProductos() != null) {
            for (Integer id : dto.getProductos()) {
                Productos prod = repoProductos.findById(id).orElseThrow();
                p.getProductos().add(prod);
            }
        }

        if (dto.getCategorias() != null) {
            for (Integer id : dto.getCategorias()) {
                Categorias cat = repoCategorias.findById(id).orElseThrow();
                p.getCategorias().add(cat);
            }
        }

        return p;
    }

    @DeleteMapping("/promociones/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Integer id) {
        repoPromociones.deleteById(id);
        return ResponseEntity.ok("Promoción eliminada correctamente");
    }
}
