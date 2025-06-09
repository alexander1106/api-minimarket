package com.gadbacorp.api.controller.inventario;

import java.util.List;
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

import com.gadbacorp.api.entity.inventario.InventarioProducto;
import com.gadbacorp.api.entity.inventario.TrasladoInventarioProducto;
import com.gadbacorp.api.entity.inventario.TrasladoInventarioProductoDTO;
import com.gadbacorp.api.service.inventario.IInventarioProductoService;
import com.gadbacorp.api.service.inventario.ITrasladoInventarioProductoService;

@RestController
@RequestMapping("/api/minimarket/traslados")
public class TrasladoInventarioProductoController {

    @Autowired
    private ITrasladoInventarioProductoService trasladoService;

    @Autowired
    private IInventarioProductoService invProdService;

    @GetMapping
    public ResponseEntity<List<TrasladoInventarioProductoDTO>> listar() {
        List<TrasladoInventarioProductoDTO> lista = trasladoService.buscarTodos().stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrasladoInventarioProductoDTO> obtener(@PathVariable Integer id) {
        return trasladoService.buscarId(id)
            .map(this::toDTO)
            .map(ResponseEntity::ok)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Traslado no encontrado id=" + id
            ));
    }

    @PostMapping
    public ResponseEntity<TrasladoInventarioProductoDTO> crear(
            @RequestBody TrasladoInventarioProductoDTO dto) {

        TrasladoInventarioProducto entidad = toEntity(dto);
        TrasladoInventarioProducto guardado = trasladoService.guardar(entidad);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(toDTO(guardado));
    }

    @PutMapping
    public ResponseEntity<TrasladoInventarioProductoDTO> actualizar(
            @RequestBody TrasladoInventarioProductoDTO dto) {

        if (dto.getIdtraslado() == null) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Debe incluir el campo 'idtraslado' en el JSON para actualizar"
            );
        }
        TrasladoInventarioProducto entidad = toEntity(dto);
        TrasladoInventarioProducto modificado = trasladoService.modificar(entidad);
        return ResponseEntity.ok(toDTO(modificado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Integer id) {
        trasladoService.eliminar(id);
        return ResponseEntity.ok("Traslado eliminado correctamente");
    }

    // — Helpers de mapeo — 

    private TrasladoInventarioProducto toEntity(TrasladoInventarioProductoDTO dto) {
        TrasladoInventarioProducto t = new TrasladoInventarioProducto();
        if (dto.getIdtraslado() != null) {
            t.setIdtraslado(dto.getIdtraslado());
        }

        InventarioProducto origen = invProdService.buscarPorId(dto.getOrigenId())
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "InventarioProducto origen no encontrado id=" + dto.getOrigenId()
            ));

        InventarioProducto destino = invProdService.buscarPorId(dto.getDestinoId())
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "InventarioProducto destino no encontrado id=" + dto.getDestinoId()
            ));

        t.setOrigen(origen);
        t.setDestino(destino);
        t.setCantidad(dto.getCantidad());
        t.setFechaTraslado(dto.getFechaTraslado());
        return t;
    }

    private TrasladoInventarioProductoDTO toDTO(TrasladoInventarioProducto t) {
        TrasladoInventarioProductoDTO dto = new TrasladoInventarioProductoDTO();
        dto.setIdtraslado(t.getIdtraslado());
        dto.setOrigenId(t.getOrigen().getIdinventarioproducto());
        dto.setDestinoId(t.getDestino().getIdinventarioproducto());
        dto.setCantidad(t.getCantidad());
        dto.setFechaTraslado(t.getFechaTraslado());
        return dto;
    }
}