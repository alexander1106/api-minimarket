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

import com.gadbacorp.api.entity.inventario.Almacenes;
import com.gadbacorp.api.entity.inventario.Productos;
import com.gadbacorp.api.entity.inventario.TrasladoAlmacenes;
import com.gadbacorp.api.entity.inventario.TrasladoAlmacenesDTO;
import com.gadbacorp.api.repository.inventario.AlmacenesRepository;
import com.gadbacorp.api.repository.inventario.ProductosRepository;
import com.gadbacorp.api.service.inventario.ITrasladoAlmacenesService;

/**
 * Controlador para operaciones de traslado de productos entre almacenes.
 */
@RestController
@RequestMapping("/api/minimarket/traslados")
public class TrasladoAlmacenesController {

    @Autowired
    private ITrasladoAlmacenesService service;

    @Autowired
    private ProductosRepository repoProductos;

    @Autowired
    private AlmacenesRepository repoAlmacenes;

    private TrasladoAlmacenes toEntity(TrasladoAlmacenesDTO dto) {
        TrasladoAlmacenes t = new TrasladoAlmacenes();
        if (dto.getIdtraslado() != null) t.setIdtraslado(dto.getIdtraslado());

        Productos producto = repoProductos.findById(dto.getIdproducto())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Producto no encontrado"));
        Almacenes origen = repoAlmacenes.findById(dto.getOrigenId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Almacén origen no encontrado"));
        Almacenes destino = repoAlmacenes.findById(dto.getDestinoId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Almacén destino no encontrado"));

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

    @GetMapping
    public ResponseEntity<List<TrasladoAlmacenesDTO>> listar() {
        List<TrasladoAlmacenesDTO> lista = service.buscarTodos().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrasladoAlmacenesDTO> obtener(@PathVariable Integer id) {
        Optional<TrasladoAlmacenes> opt = service.buscarId(id);
        return opt.map(this::toDTO).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<TrasladoAlmacenesDTO> crear(@RequestBody TrasladoAlmacenesDTO dto) {
        TrasladoAlmacenes creado = service.guardar(toEntity(dto));
        return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(creado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TrasladoAlmacenesDTO> actualizar(@PathVariable Integer id, @RequestBody TrasladoAlmacenesDTO dto) {
        dto.setIdtraslado(id);
        TrasladoAlmacenes actualizado = service.modificar(toEntity(dto));
        return ResponseEntity.ok(toDTO(actualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Integer id) {
        service.eliminar(id);
        return ResponseEntity.ok("Traslado eliminado");
    }
}
