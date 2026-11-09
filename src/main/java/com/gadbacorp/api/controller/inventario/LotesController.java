package com.gadbacorp.api.controller.inventario;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.gadbacorp.api.service.inventario.IProductosService;
import com.gadbacorp.api.service.jpa.inventario.LoteService;

import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Inventario - Lotes y vencimientos")
@RestController
@RequestMapping("/api/minimarket/lotes")
public class LotesController {

    private final LoteService lotes;
    private final IProductosService productos;

    public LotesController(LoteService lotes, IProductosService productos) {
        this.lotes = lotes;
        this.productos = productos;
    }

    /** Lotes con saldo que vencen dentro de {@code dias} (por defecto 30). Los ya vencidos vienen con vencido=true. */
    @GetMapping("/por-vencer")
    public ResponseEntity<List<Map<String, Object>>> porVencer(@RequestParam(defaultValue = "30") int dias,
                                                               @RequestParam(required = false) Integer idSucursal) {
        if (dias < 0 || dias > 3650) {
            throw new IllegalArgumentException("Los dias deben estar entre 0 y 3650");
        }
        return ResponseEntity.ok(lotes.porVencer(dias, idSucursal));
    }

    /** Lotes de un producto (con y sin saldo), el que vence antes primero. */
    @GetMapping("/productos/{idProducto}")
    public ResponseEntity<List<Map<String, Object>>> deProducto(@PathVariable Integer idProducto,
                                                                @RequestParam(required = false) Integer idSucursal) {
        var producto = productos.buscarId(idProducto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado id=" + idProducto));
        return ResponseEntity.ok(lotes.deProducto(producto, idSucursal));
    }
}
