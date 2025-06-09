package com.gadbacorp.api.gadbacorp.controller.inventario;

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

import com.gadbacorp.api.gadbacorp.entity.inventario.AlmacenProducto;
import com.gadbacorp.api.gadbacorp.entity.inventario.AlmacenProductosDTO;
import com.gadbacorp.api.gadbacorp.entity.inventario.Almacenes;
import com.gadbacorp.api.gadbacorp.entity.inventario.Productos;
import com.gadbacorp.api.gadbacorp.service.inventario.IAlmacenProductoService;
import com.gadbacorp.api.gadbacorp.service.inventario.IAlmacenesService;
import com.gadbacorp.api.gadbacorp.service.inventario.IProductosService;

@RestController
@RequestMapping("/api/minimarket")
public class AlmacenProductoController {

    @Autowired
    private IAlmacenProductoService serviceAlmacenProducto;
    
    @Autowired
    private IProductosService productosService;
    
    @Autowired
    private IAlmacenesService almacenesService;

    @GetMapping("/almacenproducto")
    public List<AlmacenProductosDTO> buscarTodos() {
        return serviceAlmacenProducto.buscarTodos().stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    @GetMapping("/almacenproducto/{id}")
    public AlmacenProductosDTO buscarId(@PathVariable Integer id) {
        AlmacenProducto ent = serviceAlmacenProducto.buscarId(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, 
                "Registro no encontrado id=" + id
            ));
        return toDTO(ent);
    }

    @PostMapping("/almacenproducto")
public ResponseEntity<AlmacenProductosDTO> crear(@RequestBody AlmacenProductosDTO dto) {
    // 1) intento encontrar un registro existente
    Optional<AlmacenProducto> exist = serviceAlmacenProducto
        .buscarPorProductoYAlmacen(dto.getIdProducto(), dto.getIdAlmacen());
    if (exist.isPresent()) {
        // 2) si existe, sumo stock y actualizo
        AlmacenProducto ap = exist.get();
        ap.setStock(ap.getStock() + dto.getStock());
        ap.setFechaIngreso(dto.getFechaIngreso());
        serviceAlmacenProducto.modificar(ap);
        return ResponseEntity.ok(toDTO(ap));
    }
    // 3) si no existe, creo uno nuevo
    AlmacenProducto nuevo = toEntity(dto);
    serviceAlmacenProducto.guardar(nuevo);
    return ResponseEntity
             .status(HttpStatus.CREATED)
             .body(toDTO(nuevo));
}

    @PutMapping("/almacenproducto")
    public AlmacenProductosDTO actualizar(@RequestBody AlmacenProductosDTO dto) {
    // 1) Recuperar el registro existente
    AlmacenProducto existente = serviceAlmacenProducto
        .buscarId(dto.getIdalmacenproducto())
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "Registro no encontrado id=" + dto.getIdalmacenproducto()
        ));
    existente.setStock(existente.getStock() + dto.getStock());
    existente.setFechaIngreso(dto.getFechaIngreso());
    serviceAlmacenProducto.modificar(existente);
    return toDTO(existente);
}


    @DeleteMapping("/almacenproducto/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Integer id) {
        serviceAlmacenProducto.eliminar(id);
        return ResponseEntity.ok("AlmacenProducto eliminado correctamente");
    }

    // --- Helpers de mapeo ---

    private AlmacenProductosDTO toDTO(AlmacenProducto e) {
        AlmacenProductosDTO dto = new AlmacenProductosDTO();
        dto.setIdalmacenproducto(e.getIdalmacenproducto());
        dto.setStock(e.getStock());
        dto.setFechaIngreso(e.getFechaIngreso());
        dto.setEstado(e.getEstado());
        dto.setIdProducto(e.getProducto().getIdproducto());
        dto.setIdAlmacen(e.getAlmacen().getIdalmacen());
        return dto;
    }

    private AlmacenProducto toEntity(AlmacenProductosDTO dto) {
        AlmacenProducto e = new AlmacenProducto();
        if (dto.getIdalmacenproducto() != null) {
            e.setIdalmacenproducto(dto.getIdalmacenproducto());
        }
        e.setStock(dto.getStock());
        e.setFechaIngreso(dto.getFechaIngreso());
        e.setEstado(dto.getEstado());

        // Asignar Producto
        Productos p = productosService.buscarId(dto.getIdProducto())
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Producto no encontrado id=" + dto.getIdProducto()
            ));
        e.setProducto(p);

        // Asignar Almacen
        Almacenes a = almacenesService.buscarId(dto.getIdAlmacen())
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Almacén no encontrado id=" + dto.getIdAlmacen()
            ));
        e.setAlmacen(a);

        return e;
    }
}
