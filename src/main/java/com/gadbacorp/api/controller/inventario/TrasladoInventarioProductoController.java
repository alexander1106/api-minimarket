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

import com.gadbacorp.api.entity.inventario.InventarioProducto;
import com.gadbacorp.api.entity.inventario.TrasladoInventarioProducto;
import com.gadbacorp.api.entity.inventario.TrasladoInventarioProductoDTO;
import com.gadbacorp.api.service.inventario.ITrasladoInventarioProductoService;

@RestController
@RequestMapping("/api/minimarket/traslados")
public class TrasladoInventarioProductoController {

    @Autowired
    private ITrasladoInventarioProductoService trasladoService;

    @GetMapping
    public ResponseEntity<List<TrasladoInventarioProductoDTO>> listar() {
        List<TrasladoInventarioProductoDTO> lista = trasladoService.buscarTodos()
            .stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrasladoInventarioProductoDTO> obtener(@PathVariable Integer id) {
        Optional<TrasladoInventarioProductoDTO> op = trasladoService.buscarId(id).map(this::toDTO);
        return op.map(ResponseEntity::ok)
                 .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping
    public ResponseEntity<TrasladoInventarioProductoDTO> crear(@RequestBody TrasladoInventarioProductoDTO dto) {
        TrasladoInventarioProductoDTO guardado = toDTO(
            trasladoService.guardar(toEntity(dto))
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }

    // PUT y DELETE devuelven 403, según la lógica del servicio
    @PutMapping
    public ResponseEntity<?> actualizar(@RequestBody TrasladoInventarioProductoDTO dto) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                             .body("No está permitido modificar un traslado de inventario.");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Integer id) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                             .body("No está permitido eliminar un traslado de inventario.");
    }

    // —— Métodos de mapeo DTO <-> Entity (solo usados aquí) ——

    private TrasladoInventarioProductoDTO toDTO(TrasladoInventarioProducto t) {
        TrasladoInventarioProductoDTO dto = new TrasladoInventarioProductoDTO();
        dto.setIdtraslado(t.getIdtraslado());
        dto.setOrigenId(t.getOrigen().getIdinventarioproducto());
        dto.setDestinoId(t.getDestino().getIdinventarioproducto());
        dto.setCantidad(t.getCantidad());
        dto.setFechaTraslado(t.getFechaTraslado());
        return dto;
    }

    private TrasladoInventarioProducto toEntity(TrasladoInventarioProductoDTO dto) {
        TrasladoInventarioProducto entity = new TrasladoInventarioProducto();
        entity.setIdtraslado(dto.getIdtraslado());
        // OJO: Solo setea el ID en Origen y Destino (no traes todo el objeto aquí)
        InventarioProducto origen = new InventarioProducto();
        origen.setIdinventarioproducto(dto.getOrigenId());
        entity.setOrigen(origen);

        InventarioProducto destino = new InventarioProducto();
        destino.setIdinventarioproducto(dto.getDestinoId());
        entity.setDestino(destino);

        entity.setCantidad(dto.getCantidad());
        entity.setFechaTraslado(dto.getFechaTraslado());
        return entity;
    }
}
