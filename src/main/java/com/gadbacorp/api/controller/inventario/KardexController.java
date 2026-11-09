package com.gadbacorp.api.controller.inventario;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import com.gadbacorp.api.entity.inventario.MovimientoStock;
import com.gadbacorp.api.service.inventario.IProductosService;
import com.gadbacorp.api.service.jpa.inventario.KardexService;

import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Inventario - Kardex")
@RestController
@RequestMapping("/api/minimarket/kardex")
public class KardexController {

    private final KardexService kardex;
    private final IProductosService productos;

    public KardexController(KardexService kardex, IProductosService productos) {
        this.kardex = kardex;
        this.productos = productos;
    }

    /**
     * Historial de movimientos de stock de un producto, el mas reciente primero.
     * Filtros opcionales: sucursal y rango de fechas (AAAA-MM-DD, ambos extremos incluidos).
     */
    @GetMapping("/productos/{idProducto}")
    public ResponseEntity<List<Map<String, Object>>> porProducto(
            @PathVariable Integer idProducto,
            @RequestParam(required = false) Integer idSucursal,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        // buscarId ya aplica el aislamiento por empresa: un producto ajeno es un 404
        productos.buscarId(idProducto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado id=" + idProducto));

        LocalDateTime ini = desde == null ? null : desde.atStartOfDay();
        LocalDateTime fin = hasta == null ? null : hasta.plusDays(1).atStartOfDay().minusNanos(1);
        List<Map<String, Object>> filas = kardex.consultar(idProducto, idSucursal, ini, fin).stream()
                .map(KardexController::fila)
                .collect(Collectors.toList());
        return ResponseEntity.ok(filas);
    }

    private static Map<String, Object> fila(MovimientoStock m) {
        Map<String, Object> f = new java.util.LinkedHashMap<>();
        f.put("idMovimiento", m.getIdMovimiento());
        f.put("fecha", m.getFecha());
        f.put("tipo", m.getTipo());
        f.put("idSucursal", m.getIdSucursal());
        f.put("cantidadBase", m.getCantidadBase());
        f.put("stockResultante", m.getStockResultante());
        f.put("referencia", m.getReferencia());
        f.put("usuario", m.getUsuario());
        return f;
    }
}
