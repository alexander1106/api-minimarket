package com.gadbacorp.api.service.jpa.inventario;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gadbacorp.api.entity.inventario.LoteProducto;
import com.gadbacorp.api.entity.inventario.Productos;
import com.gadbacorp.api.repository.inventario.LoteProductoRepository;
import com.gadbacorp.api.tenant.TenantService;

/** Lotes y vencimientos: entran con la compra y se consumen primero el que vence antes (FEFO). */
@Service
public class LoteService {

    private final LoteProductoRepository lotes;

    public LoteService(LoteProductoRepository lotes) {
        this.lotes = lotes;
    }

    /** Crea el lote de una linea de compra. No hace nada si la linea no trae lote ni vencimiento. */
    @Transactional
    public void registrar(Productos producto, Integer idSucursal, Integer idDetalleCompra, String codigo,
                          LocalDate vencimiento, BigDecimal cantidadBase, String referencia) {
        if ((codigo == null || codigo.isBlank()) && vencimiento == null) {
            return;
        }
        LoteProducto lote = new LoteProducto();
        lote.setProducto(producto);
        lote.setIdSucursal(idSucursal);
        lote.setIdDetalleCompra(idDetalleCompra);
        lote.setCodigo(codigo == null || codigo.isBlank() ? null : codigo.trim());
        lote.setFechaVencimiento(vencimiento);
        lote.setCantidadInicial(cantidadBase);
        lote.setCantidadDisponible(cantidadBase);
        lote.setFechaIngreso(LocalDateTime.now());
        lote.setReferencia(referencia);
        lotes.save(lote);
    }

    /** La linea de compra cambio de cantidad, lote o vencimiento: el lote la sigue (o se crea si ahora trae datos de lote). */
    @Transactional
    public void sincronizarLinea(Productos producto, Integer idSucursal, Integer idDetalleCompra, String codigo,
                                 LocalDate vencimiento, BigDecimal cantidadBaseNueva, BigDecimal diferenciaBase, String referencia) {
        var existente = lotes.findByIdDetalleCompra(idDetalleCompra);
        if (existente.isEmpty()) {
            registrar(producto, idSucursal, idDetalleCompra, codigo, vencimiento, cantidadBaseNueva, referencia);
            return;
        }
        LoteProducto l = existente.get();
        l.setCodigo(codigo == null || codigo.isBlank() ? null : codigo.trim());
        l.setFechaVencimiento(vencimiento);
        l.setCantidadInicial(l.getCantidadInicial().add(diferenciaBase));
        l.setCantidadDisponible(l.getCantidadDisponible().add(diferenciaBase).max(BigDecimal.ZERO));
        lotes.save(l);
    }

    /** Se quito la linea de compra: el lote sale (solo se puede devolver lo que aun no se vendio). */
    @Transactional
    public void retirarDeLinea(Integer idDetalleCompra, BigDecimal cantidadBase) {
        lotes.findByIdDetalleCompra(idDetalleCompra).ifPresent(l -> {
            l.setCantidadDisponible(l.getCantidadDisponible().subtract(cantidadBase).max(BigDecimal.ZERO));
            lotes.save(l);
        });
    }

    /** Descuenta de los lotes, el que vence antes primero. Lo que no esta en lotes (stock sin lote) no se toca. */
    @Transactional
    public void consumirFefo(Integer idSucursal, Integer idProducto, BigDecimal cantidadBase) {
        BigDecimal pendiente = cantidadBase;
        for (LoteProducto l : lotes.disponiblesFefo(idProducto, idSucursal)) {
            if (pendiente.signum() <= 0) {
                break;
            }
            BigDecimal tomar = l.getCantidadDisponible().min(pendiente);
            l.setCantidadDisponible(l.getCantidadDisponible().subtract(tomar));
            lotes.save(l);
            pendiente = pendiente.subtract(tomar);
        }
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> deProducto(Productos producto, Integer idSucursal) {
        return lotes.deProducto(producto.getIdproducto(), idSucursal).stream().map(LoteService::fila).collect(Collectors.toList());
    }

    /** Lotes con saldo que vencen dentro de {@code dias} (los ya vencidos incluidos), los mas urgentes primero. */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> porVencer(int dias, Integer idSucursal) {
        return lotes.porVencer(LocalDate.now().plusDays(dias), idSucursal).stream()
                .filter(l -> TenantService.puedeVer(TenantService.empresaDe(l.getProducto().getEmpresa())))
                .map(LoteService::fila)
                .collect(Collectors.toList());
    }

    private static Map<String, Object> fila(LoteProducto l) {
        Map<String, Object> f = new java.util.LinkedHashMap<>();
        LocalDate hoy = LocalDate.now();
        f.put("idLote", l.getIdLote());
        f.put("idProducto", l.getProducto().getIdproducto());
        f.put("producto", l.getProducto().getNombre());
        f.put("idSucursal", l.getIdSucursal());
        f.put("codigo", l.getCodigo());
        f.put("fechaVencimiento", l.getFechaVencimiento());
        f.put("diasParaVencer", l.getFechaVencimiento() == null ? null
                : java.time.temporal.ChronoUnit.DAYS.between(hoy, l.getFechaVencimiento()));
        f.put("vencido", l.getFechaVencimiento() != null && l.getFechaVencimiento().isBefore(hoy));
        f.put("cantidadInicial", l.getCantidadInicial());
        f.put("cantidadDisponible", l.getCantidadDisponible());
        f.put("referencia", l.getReferencia());
        return f;
    }
}
