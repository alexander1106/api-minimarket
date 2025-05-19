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

import com.gadbacorp.api.entity.inventario.AjusteInventarioDTO;
import com.gadbacorp.api.entity.inventario.AlmacenProducto;
import com.gadbacorp.api.entity.inventario.Almacenes;
import com.gadbacorp.api.entity.inventario.Inventario;
import com.gadbacorp.api.entity.inventario.InventarioDTO;
import com.gadbacorp.api.entity.inventario.Productos;
import com.gadbacorp.api.repository.inventario.AlmacenProductoRepository;
import com.gadbacorp.api.repository.inventario.AlmacenesRepository;
import com.gadbacorp.api.repository.inventario.InventarioRepository;
import com.gadbacorp.api.repository.inventario.ProductosRepository;
import com.gadbacorp.api.service.inventario.IInventarioService;

/**
 * Controlador para gestionar Inventario (stock de producto en almacén).
 */
@RestController
@RequestMapping("/api/minimarket/inventario")
public class InventarioController {

    @Autowired
    private IInventarioService serviceInventario;

    @Autowired
    private ProductosRepository repoProductos;

    @Autowired
    private AlmacenesRepository repoAlmacenes;

    @Autowired
    private InventarioRepository repoInventario;

    @Autowired
    private AlmacenProductoRepository repoAlmacenProducto;

    // ------------------------
    // Mappers: DTO <-> Entity
    // ------------------------

    private Inventario toEntity(InventarioDTO dto) {
        Inventario inv = new Inventario();
        if (dto.getIdinventario() != null) {
            inv.setIdinventario(dto.getIdinventario());
        }
        // validar existencia de producto y almacén
        Productos prod = repoProductos.findById(dto.getIdproducto())
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Producto no encontrado id=" + dto.getIdproducto()));
        Almacenes alm = repoAlmacenes.findById(dto.getIdalmacen())
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Almacén no encontrado id=" + dto.getIdalmacen()));
        inv.setProducto(prod);
        inv.setAlmacen(alm);
        inv.setStock(dto.getStock());
        return inv;
    }

    private InventarioDTO toDTO(Inventario inv) {
        InventarioDTO dto = new InventarioDTO();
        dto.setIdinventario(inv.getIdinventario());
        dto.setIdproducto(inv.getProducto().getIdproducto());
        dto.setIdalmacen(inv.getAlmacen().getIdalmacen());
        dto.setStock(inv.getStock());
        // Mapear historial de ajustes
        List<AjusteInventarioDTO> ajustes = inv.getAjustes().stream()
            .map(aj -> {
                AjusteInventarioDTO ajdto = new AjusteInventarioDTO();
                ajdto.setIdajusteinventario(aj.getIdajusteinventario());
                ajdto.setIdinventario(inv.getIdinventario());
                ajdto.setCantidad(aj.getCantidad());
                ajdto.setDescripcion(aj.getDescripcion());
                ajdto.setFechaAjuste(aj.getFechaAjuste());
                return ajdto;
            })
            .collect(Collectors.toList());
        dto.setAjustes(ajustes);
        return dto;
    }

    // ------------------------
    // Endpoints CRUD
    // ------------------------

    @GetMapping
    public ResponseEntity<List<InventarioDTO>> listarTodos() {
        List<InventarioDTO> list = serviceInventario.buscarTodos().stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventarioDTO> obtenerPorId(@PathVariable Integer id) {
        Optional<Inventario> opt = serviceInventario.buscarId(id);
        return opt
            .map(this::toDTO)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<InventarioDTO> consultaStock(
        @RequestBody InventarioDTO dto) {
    // 1) Busca el registro de AlmacenProducto
    Productos prod = repoProductos.findById(dto.getIdproducto())
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "Producto no encontrado id=" + dto.getIdproducto()));
    Almacenes alm = repoAlmacenes.findById(dto.getIdalmacen())
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "Almacén no encontrado id=" + dto.getIdalmacen()));

    AlmacenProducto ap = repoAlmacenProducto
        .findByProductoAndAlmacen(prod, alm)
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "No hay stock para producto " + dto.getIdproducto() +
            " en almacén " + dto.getIdalmacen()));

    // 2) Busca o crea Inventario y sincroniza stock
    Optional<Inventario> optInv = repoInventario.findByProductoAndAlmacen(prod, alm);
    Inventario inv = optInv
        .map(i -> { i.setStock(ap.getStock()); return i; })
        .orElseGet(() -> new Inventario(prod, alm, ap.getStock()));

    // 3) Guarda Inventario
    Inventario guardado = repoInventario.save(inv);

    // 4) Devuelve DTO con id y stock actualizado
    return ResponseEntity.ok(toDTO(guardado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<InventarioDTO> actualizar(
            @PathVariable Integer id,
            @RequestBody InventarioDTO dto) {
        Inventario existente = serviceInventario.buscarId(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Inventario no encontrado id=" + id));
        existente.setStock(dto.getStock());
        Inventario updated = serviceInventario.modificar(existente);
        return ResponseEntity.ok(toDTO(updated));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Integer id) {
        serviceInventario.eliminar(id);
        return ResponseEntity.ok("Movimiento de inventario eliminada");
    }
}