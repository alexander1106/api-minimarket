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

import com.gadbacorp.api.entity.inventario.Almacenes;
import com.gadbacorp.api.entity.inventario.Productos;
import com.gadbacorp.api.entity.inventario.TrasladoAlmacenes;
import com.gadbacorp.api.entity.inventario.TrasladoAlmacenesDTO;
import com.gadbacorp.api.service.inventario.IAlmacenesService;
import com.gadbacorp.api.service.inventario.IProductosService;
import com.gadbacorp.api.service.inventario.ITrasladoAlmacenesService;

@RestController
@RequestMapping("/api/minimarket/traslados")
public class TrasladoAlmacenesController {

    @Autowired
    private ITrasladoAlmacenesService trasladoService;

    @Autowired
    private IProductosService productosService;

    @Autowired
    private IAlmacenesService almacenesService;

    @GetMapping
    public ResponseEntity<List<TrasladoAlmacenesDTO>> listar() {
        List<TrasladoAlmacenesDTO> lista = trasladoService.buscarTodos().stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }


    @GetMapping("/{id}")
    public ResponseEntity<TrasladoAlmacenesDTO> obtener(@PathVariable Integer id) {
        return trasladoService.buscarId(id)
            .map(this::toDTO)
            .map(ResponseEntity::ok)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Traslado no encontrado id=" + id
            ));
    }

    @PostMapping
    public ResponseEntity<TrasladoAlmacenesDTO> crear(@RequestBody TrasladoAlmacenesDTO dto) {
        TrasladoAlmacenes entidad = toEntity(dto);
        TrasladoAlmacenes guardado = trasladoService.guardar(entidad);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(toDTO(guardado));
    }

    @PutMapping
    public ResponseEntity<TrasladoAlmacenesDTO> actualizar(@RequestBody TrasladoAlmacenesDTO dto) {
        // 1) Validar que venga el ID en el body
        if (dto.getIdtraslado() == null) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Debe incluir el campo idtraslado en el JSON para actualizar"
            );
        }
        // 2) Convertir DTO → Entidad
        TrasladoAlmacenes entidad = toEntity(dto);
        // 3) Ejecutar la modificación
        TrasladoAlmacenes modificado = trasladoService.modificar(entidad);
        // 4) Devolver el DTO actualizado
        return ResponseEntity.ok(toDTO(modificado));
    }


    // DELETE /api/minimarket/traslados/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Integer id) {
        trasladoService.eliminar(id);
        return ResponseEntity.ok("Traslado eliminado correctamente");
    }

    // — Helpers de mapeo —

    private TrasladoAlmacenes toEntity(TrasladoAlmacenesDTO dto) {
        TrasladoAlmacenes t = new TrasladoAlmacenes();
        if (dto.getIdtraslado() != null) {
            t.setIdtraslado(dto.getIdtraslado());
        }

        Productos producto = productosService.buscarId(dto.getIdproducto())
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Producto no encontrado id=" + dto.getIdproducto()
            ));

        Almacenes origen = almacenesService.buscarId(dto.getOrigenId())
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Almacén origen no encontrado id=" + dto.getOrigenId()
            ));

        Almacenes destino = almacenesService.buscarId(dto.getDestinoId())
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Almacén destino no encontrado id=" + dto.getDestinoId()
            ));

        t.setProducto(producto);
        t.setOrigen(origen);
        t.setDestino(destino);
        t.setCantidad(dto.getCantidad());
        t.setFechaTraslado(dto.getFechaTraslado());
        return t;
    }

    private TrasladoAlmacenesDTO toDTO(TrasladoAlmacenes t) {
        TrasladoAlmacenesDTO dto = new TrasladoAlmacenesDTO();
        dto.setIdtraslado(t.getIdtraslado());
        dto.setIdproducto(t.getProducto().getIdproducto());
        dto.setOrigenId(t.getOrigen().getIdalmacen());
        dto.setDestinoId(t.getDestino().getIdalmacen());
        dto.setCantidad(t.getCantidad());
        dto.setFechaTraslado(t.getFechaTraslado());
        return dto;
    }
}
