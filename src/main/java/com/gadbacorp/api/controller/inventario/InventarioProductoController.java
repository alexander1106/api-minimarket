package com.gadbacorp.api.controller.inventario;

import java.util.List;
import java.util.Optional;
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

import com.gadbacorp.api.entity.inventario.Inventario;
import com.gadbacorp.api.entity.inventario.InventarioProducto;
import com.gadbacorp.api.entity.inventario.InventarioProductosDTO;
import com.gadbacorp.api.entity.inventario.Productos;
import com.gadbacorp.api.repository.inventario.InventarioProductoRepository;
import com.gadbacorp.api.repository.inventario.InventarioRepository;
import com.gadbacorp.api.repository.inventario.ProductosRepository;

@RestController
@RequestMapping("/api/minimarket")
@CrossOrigin(origins = "http://localhost:4200")
public class InventarioProductoController {

    @Autowired
    private InventarioProductoRepository repoInventarioProducto;

    @Autowired
    private ProductosRepository repoProductos;

    @Autowired
    private InventarioRepository repoInventario;

    @GetMapping("/inventarioproducto")
    public ResponseEntity<List<InventarioProductosDTO>> listarTodos() {
        List<InventarioProductosDTO> lista = repoInventarioProducto.findAll().stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/inventarioproducto/{id}")
    public ResponseEntity<InventarioProductosDTO> obtenerPorId(@PathVariable Integer id) {
        return repoInventarioProducto.findById(id)
            .map(this::toDTO)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/inventarioproducto")
    public ResponseEntity<InventarioProductosDTO> crear(@RequestBody InventarioProductosDTO dto) {
        Productos producto = repoProductos.findById(dto.getIdproducto())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Producto no encontrado id=" + dto.getIdproducto()));

        Inventario inventario = repoInventario.findById(dto.getIdinventario())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Inventario no encontrado id=" + dto.getIdinventario()));

        Optional<InventarioProducto> optExistente = repoInventarioProducto
            .findByProductoIdproductoAndInventarioIdinventario(dto.getIdproducto(), dto.getIdinventario());

        InventarioProducto entidad;
        if (optExistente.isPresent()) {
            entidad = optExistente.get();
            int stockViejo = entidad.getStockactual() == null ? 0 : entidad.getStockactual();
            entidad.setStockactual(stockViejo + dto.getStockactual());
        } else {
            entidad = new InventarioProducto();
            entidad.setProducto(producto);
            entidad.setInventario(inventario);
            entidad.setStockactual(dto.getStockactual());
        }

        InventarioProducto guardado = repoInventarioProducto.save(entidad);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(guardado));
    }

    @PutMapping("/inventarioproducto")
    public ResponseEntity<InventarioProductosDTO> actualizar(@RequestBody InventarioProductosDTO dto) {
        if (dto.getIdinventarioproducto() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El campo 'idinventarioproducto' es requerido");
        }

        InventarioProducto existente = repoInventarioProducto.findById(dto.getIdinventarioproducto())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "InventarioProducto no encontrado id=" + dto.getIdinventarioproducto()));

        if (!existente.getProducto().getIdproducto().equals(dto.getIdproducto())) {
            Productos producto = repoProductos.findById(dto.getIdproducto())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Producto no encontrado id=" + dto.getIdproducto()));
            existente.setProducto(producto);
        }

        if (!existente.getInventario().getIdinventario().equals(dto.getIdinventario())) {
            Inventario inventario = repoInventario.findById(dto.getIdinventario())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Inventario no encontrado id=" + dto.getIdinventario()));
            existente.setInventario(inventario);
        }

        existente.setStockactual(dto.getStockactual());

        InventarioProducto actualizado = repoInventarioProducto.save(existente);
        return ResponseEntity.ok(toDTO(actualizado));
    }
    @DeleteMapping("/inventarioproducto/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Integer id) {
        repoInventarioProducto.deleteById(id);
        return ResponseEntity.ok("InventarioProducto eliminado correctamente");
    }

    private InventarioProductosDTO toDTO(InventarioProducto ip) {
        InventarioProductosDTO dto = new InventarioProductosDTO();
        dto.setIdinventarioproducto(ip.getIdinventarioproducto());
        dto.setStockactual(ip.getStockactual());
        dto.setFechaingreso(ip.getFechaingreso());
        dto.setIdproducto(ip.getProducto().getIdproducto());
        dto.setIdinventario(ip.getInventario().getIdinventario());
        return dto;
    }
}
