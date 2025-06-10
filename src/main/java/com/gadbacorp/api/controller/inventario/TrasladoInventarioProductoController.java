package com.gadbacorp.api.controller.inventario;

import java.time.LocalDateTime;
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

import com.gadbacorp.api.entity.inventario.InventarioProducto;
import com.gadbacorp.api.entity.inventario.TrasladoInventarioProducto;
import com.gadbacorp.api.entity.inventario.TrasladoInventarioProductoDTO;
import com.gadbacorp.api.repository.inventario.InventarioProductoRepository;
import com.gadbacorp.api.repository.inventario.TrasladoInventarioProductoRepository;

@RestController
@RequestMapping("/api/minimarket")
public class TrasladoInventarioProductoController {

    @Autowired
    private TrasladoInventarioProductoRepository trasladoRepo;

    @Autowired
    private InventarioProductoRepository invProdRepo;

    @GetMapping("/traslados")
    public ResponseEntity<List<TrasladoInventarioProductoDTO>> listar() {
        List<TrasladoInventarioProductoDTO> lista = trasladoRepo.findAll().stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/traslados/{id}")
    public ResponseEntity<TrasladoInventarioProductoDTO> obtener(@PathVariable Integer id) {
        return trasladoRepo.findById(id)
            .map(this::toDTO)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/traslados")
    public ResponseEntity<TrasladoInventarioProductoDTO> crear(@RequestBody TrasladoInventarioProductoDTO dto) {
        TrasladoInventarioProducto traslado = ejecutarTraslado(dto);
        return ResponseEntity.status(201).body(toDTO(traslado));
    }

    @PutMapping("/traslados")
    public ResponseEntity<TrasladoInventarioProductoDTO> actualizar(@RequestBody TrasladoInventarioProductoDTO dto) {
        TrasladoInventarioProducto original = trasladoRepo.findById(dto.getIdtraslado())
            .orElseThrow();

        revertirTraslado(original);

        TrasladoInventarioProducto actualizado = ejecutarTraslado(dto);
        actualizado.setIdtraslado(original.getIdtraslado());
        if (dto.getFechaTraslado() == null) {
            actualizado.setFechaTraslado(original.getFechaTraslado());
        }

        return ResponseEntity.ok(toDTO(actualizado));
    }

    private TrasladoInventarioProducto ejecutarTraslado(TrasladoInventarioProductoDTO dto) {
        InventarioProducto origen = invProdRepo.findById(dto.getOrigenId()).orElseThrow();
        InventarioProducto destino = invProdRepo.findById(dto.getDestinoId()).orElseThrow();

        if (origen.getIdinventarioproducto().equals(destino.getIdinventarioproducto())) {
            throw new IllegalArgumentException("Origen y destino no pueden ser iguales");
        }

        if (!origen.getProducto().getIdproducto().equals(destino.getProducto().getIdproducto())) {
            throw new IllegalArgumentException("Deben pertenecer al mismo producto");
        }

        int cantidad = dto.getCantidad();
        if (cantidad <= 0) throw new IllegalArgumentException("Cantidad debe ser mayor a cero");

        if (origen.getStockactual() < cantidad) {
            throw new IllegalArgumentException("Stock insuficiente en origen");
        }

        origen.setStockactual(origen.getStockactual() - cantidad);
        destino.setStockactual(destino.getStockactual() + cantidad);

        invProdRepo.save(origen);
        invProdRepo.save(destino);

        TrasladoInventarioProducto traslado = new TrasladoInventarioProducto();
        traslado.setOrigen(origen);
        traslado.setDestino(destino);
        traslado.setCantidad(cantidad);
        traslado.setDescripcion(dto.getDescripcion()); 
        traslado.setFechaTraslado(dto.getFechaTraslado() != null ? dto.getFechaTraslado() : LocalDateTime.now());

        return trasladoRepo.save(traslado);
    }


    private void revertirTraslado(TrasladoInventarioProducto traslado) {
        InventarioProducto origen = invProdRepo.findById(traslado.getOrigen().getIdinventarioproducto()).orElseThrow();
        InventarioProducto destino = invProdRepo.findById(traslado.getDestino().getIdinventarioproducto()).orElseThrow();

        int cantidad = traslado.getCantidad();

        if (destino.getStockactual() < cantidad) {
            throw new IllegalArgumentException("No se puede revertir: stock destino insuficiente");
        }

        origen.setStockactual(origen.getStockactual() + cantidad);
        destino.setStockactual(destino.getStockactual() - cantidad);

        invProdRepo.save(origen);
        invProdRepo.save(destino);
    }

    private TrasladoInventarioProductoDTO toDTO(TrasladoInventarioProducto t) {
        TrasladoInventarioProductoDTO dto = new TrasladoInventarioProductoDTO();
        dto.setIdtraslado(t.getIdtraslado());
        dto.setOrigenId(t.getOrigen().getIdinventarioproducto());
        dto.setDestinoId(t.getDestino().getIdinventarioproducto());
        dto.setCantidad(t.getCantidad());
        dto.setDescripcion(t.getDescripcion());
        dto.setFechaTraslado(t.getFechaTraslado());
        return dto;
    }
     @DeleteMapping("/traslados/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Integer id) {
        trasladoRepo.deleteById(id);
        return ResponseEntity.ok("Traslado eliminado correctamente");
    }
}
