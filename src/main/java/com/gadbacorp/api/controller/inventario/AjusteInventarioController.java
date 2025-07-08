package com.gadbacorp.api.controller.inventario;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.gadbacorp.api.entity.inventario.AjusteInventario;
import com.gadbacorp.api.entity.inventario.AjusteInventarioDTO;
import com.gadbacorp.api.entity.inventario.InventarioProducto;
import com.gadbacorp.api.repository.inventario.AjusteInventarioRepository;
import com.gadbacorp.api.repository.inventario.InventarioProductoRepository;

@RestController
@RequestMapping("/api/minimarket")
@CrossOrigin(origins = "http://localhost:4200")
public class AjusteInventarioController {

    @Autowired
    private AjusteInventarioRepository ajusteRepo;

    @Autowired
    private InventarioProductoRepository invProdRepo;

    @GetMapping("/ajustes")
    public ResponseEntity<List<AjusteInventarioDTO>> listar() {
        List<AjusteInventarioDTO> lista = ajusteRepo.findAll().stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/ajustes/{id}")
    public ResponseEntity<AjusteInventarioDTO> obtener(@PathVariable Integer id) {
        return ajusteRepo.findById(id)
            .map(this::toDTO)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    @GetMapping("/ajustes/sucursal/{idSucursal}")
    public ResponseEntity<List<AjusteInventarioDTO>> listarPorSucursal(
            @PathVariable Integer idSucursal) {

        List<AjusteInventarioDTO> lista = ajusteRepo
            .findByInventarioProducto_Inventario_Almacen_Sucursal_IdSucursal(idSucursal)
            .stream()
            .map(this::toDTO)
            .collect(Collectors.toList());

        return ResponseEntity.ok(lista);
    }

    @PostMapping("/ajustes")
    public ResponseEntity<AjusteInventarioDTO> crear(@RequestBody AjusteInventarioDTO dto) {
        InventarioProducto invProd = invProdRepo.findById(dto.getIdinventarioproducto())
            .orElseThrow(() -> new IllegalArgumentException("InventarioProducto no encontrado id=" + dto.getIdinventarioproducto()));

        int cantidad = dto.getCantidad();
        int nuevoStock = invProd.getStockactual() + cantidad;

        if (nuevoStock < 0) {
            throw new IllegalArgumentException("Stock insuficiente: actual=" + invProd.getStockactual() + ", ajuste=" + cantidad);
        }

        invProd.setStockactual(nuevoStock);
        invProdRepo.save(invProd);

        AjusteInventario ajuste = new AjusteInventario();
        ajuste.setInventarioProducto(invProd);
        ajuste.setCantidad(cantidad);
        ajuste.setDescripcion(dto.getDescripcion());
        ajuste.setFechaAjuste(dto.getFechaAjuste() != null ? dto.getFechaAjuste() : LocalDateTime.now());

        AjusteInventario guardado = ajusteRepo.save(ajuste);
        return ResponseEntity.status(201).body(toDTO(guardado));
    }

    @PutMapping("/ajustes")
    public ResponseEntity<AjusteInventarioDTO> actualizar(@RequestBody AjusteInventarioDTO dto) {
        AjusteInventario original = ajusteRepo.findById(dto.getIdajusteinventario())
            .orElseThrow(() -> new IllegalArgumentException("Ajuste no encontrado con id=" + dto.getIdajusteinventario()));

        InventarioProducto invProd = original.getInventarioProducto();

        int stockSinAjuste = invProd.getStockactual() - original.getCantidad();
        int nuevoStock = stockSinAjuste + dto.getCantidad();

        if (nuevoStock < 0) {
            throw new IllegalArgumentException("El nuevo ajuste dejaría el stock en negativo");
        }

        invProd.setStockactual(nuevoStock);
        invProdRepo.save(invProd);

        original.setCantidad(dto.getCantidad());
        original.setDescripcion(dto.getDescripcion());
        original.setFechaAjuste(dto.getFechaAjuste() != null ? dto.getFechaAjuste() : original.getFechaAjuste());

        AjusteInventario actualizado = ajusteRepo.save(original);
        return ResponseEntity.ok(toDTO(actualizado));
    }

    private AjusteInventarioDTO toDTO(AjusteInventario aj) {
        AjusteInventarioDTO dto = new AjusteInventarioDTO();
        dto.setIdajusteinventario(aj.getIdajusteinventario());
        dto.setIdinventarioproducto(aj.getInventarioProducto().getIdinventarioproducto());
        dto.setCantidad(aj.getCantidad());
        dto.setDescripcion(aj.getDescripcion());
        dto.setFechaAjuste(aj.getFechaAjuste());
        return dto;
    }

    @DeleteMapping("/ajustes/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Integer id) {
        ajusteRepo.deleteById(id);
        return ResponseEntity.ok("Ajuste de inventario eliminado correctamente");
    }
}
