package com.gadbacorp.api.controller.inventario;

import java.util.List;
import java.util.Optional;
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

import com.gadbacorp.api.entity.inventario.Categorias;
import com.gadbacorp.api.entity.inventario.Productos;
import com.gadbacorp.api.entity.inventario.Promociones;
import com.gadbacorp.api.entity.inventario.PromocionesDTO;
import com.gadbacorp.api.repository.inventario.CategoriasRepository;
import com.gadbacorp.api.repository.inventario.ProductosRepository;
import com.gadbacorp.api.service.inventario.IPromocionesService;

@RestController
@RequestMapping("/api/minimarket/promociones")
public class PromocionesController {
    @Autowired
    private IPromocionesService servicePromociones;

    @Autowired
    private ProductosRepository repoProductos;

    @Autowired
    private CategoriasRepository repoCategorias;

    // Mapea DTO a Entidad
    private Promociones toEntity(PromocionesDTO dto) {
        Promociones promociones = new Promociones();
        if (dto.getIdpromocion() != null) {
            promociones.setIdpromocion(dto.getIdpromocion());
        }
        promociones.setNombre(dto.getNombre());
        promociones.setFechaInicio(dto.getFechaInicio());
        promociones.setFechaFin(dto.getFechaFin());
        promociones.setTipoDescuento(dto.getTipoDescuento());
        promociones.setValor(dto.getValor());

        // Asociar productos
        promociones.getProductos().clear();
        if (dto.getProductos() != null) {
            dto.getProductos().forEach(idProducto -> {
                Productos p = repoProductos.findById(idProducto)
                    .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Producto no encontrado id=" + idProducto));
                promociones.getProductos().add(p);
            });
        }

        // Asociar categorías
        promociones.getCategorias().clear();
        if (dto.getCategorias() != null) {
            dto.getCategorias().forEach(idCat -> {
                Categorias c = repoCategorias.findById(idCat)
                    .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Categoría no encontrada id=" + idCat));
                promociones.getCategorias().add(c);
            });
        }

        return promociones;
    }

    // Mapea Entidad a DTO
    private PromocionesDTO toDTO(Promociones promociones) {
        PromocionesDTO dto = new PromocionesDTO();
        dto.setIdpromocion(promociones.getIdpromocion());
        dto.setNombre(promociones.getNombre());
        dto.setFechaInicio(promociones.getFechaInicio());
        dto.setFechaFin(promociones.getFechaFin());
        dto.setTipoDescuento(promociones.getTipoDescuento());
        dto.setValor(promociones.getValor());
        dto.setProductos(
            promociones.getProductos().stream()
                .map(Productos::getIdproducto)
                .collect(Collectors.toList())
        );
        dto.setCategorias(
            promociones.getCategorias().stream()
                .map(Categorias::getIdcategoria)
                .collect(Collectors.toList())
        );
        return dto;
    }

    /**
     * Lista todas las promociones.
     */
    @GetMapping
    public ResponseEntity<List<PromocionesDTO>> listar() {
        List<PromocionesDTO> list = servicePromociones.buscarTodos().stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    /**
     * Obtiene una promoción por ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PromocionesDTO> obtener(@PathVariable Integer id) {
        Optional<Promociones> opt = servicePromociones.buscarId(id);
        return opt
            .map(this::toDTO)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Crea una nueva promoción.
     */
    @PostMapping
    public ResponseEntity<PromocionesDTO> crear(@RequestBody PromocionesDTO dto) {
        Promociones entity = toEntity(dto);
        Promociones guardada = servicePromociones.guardar(entity);
        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(toDTO(guardada));
    }

    /**
     * Actualiza una promoción existente.
     */
    @PutMapping("/{id}")
    public ResponseEntity<PromocionesDTO> actualizar(
            @PathVariable Integer id,
            @RequestBody PromocionesDTO dto) {
        // Validar existencia
        servicePromociones.buscarId(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Promoción no encontrada id=" + id));
        dto.setIdpromocion(id);
        Promociones entity = toEntity(dto);
        Promociones updated = servicePromociones.guardar(entity);
        return ResponseEntity.ok(toDTO(updated));
    }

    /**
     * Elimina (soft delete) una promoción.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Integer id) {
        servicePromociones.eliminar(id);
        return ResponseEntity.ok("Promoción eliminada");
    }
}
