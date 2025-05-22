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
import com.gadbacorp.api.entity.inventario.Inventario;
import com.gadbacorp.api.entity.inventario.InventarioDTO;
import com.gadbacorp.api.service.inventario.IInventarioService;

@RestController
@RequestMapping("/api/minimarket/inventario")
public class InventarioController {

    @Autowired
    private IInventarioService serviceInventario;

    private InventarioDTO toDTO(Inventario inv) {
        InventarioDTO dto = new InventarioDTO();
        dto.setIdinventario(inv.getIdinventario());
        dto.setIdproducto(inv.getProducto().getIdproducto());
        dto.setIdalmacen(inv.getAlmacen().getIdalmacen());
        dto.setStock(inv.getStock());
        dto.setAjustes(inv.getAjustes().stream().map(aj -> {
            AjusteInventarioDTO ajdto = new AjusteInventarioDTO();
            ajdto.setIdajusteinventario(aj.getIdajusteinventario());
            ajdto.setIdinventario(inv.getIdinventario());
            ajdto.setCantidad(aj.getCantidad());
            ajdto.setDescripcion(aj.getDescripcion());
            ajdto.setFechaAjuste(aj.getFechaAjuste());
            return ajdto;
        }).collect(Collectors.toList()));
        return dto;
    }

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
        return opt.map(this::toDTO).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<InventarioDTO> consultaStock(@RequestBody InventarioDTO dto) {
        try {
            Inventario inventario = serviceInventario.sincronizarStock(dto.getIdproducto(), dto.getIdalmacen());
            return ResponseEntity.ok(toDTO(inventario));
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al consultar stock", e);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<InventarioDTO> actualizar(@PathVariable Integer id, @RequestBody InventarioDTO dto) {
        try {
            Inventario actualizado = serviceInventario.actualizarStock(id, dto.getStock(), dto.getIdproducto(), dto.getIdalmacen());
            return ResponseEntity.ok(toDTO(actualizado));
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al actualizar inventario", e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Integer id) {
        serviceInventario.eliminar(id);
        return ResponseEntity.ok("Movimiento de inventario eliminado");
    }
}