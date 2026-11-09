package com.gadbacorp.api.service.jpa.inventario;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gadbacorp.api.entity.inventario.InventarioProducto;
import com.gadbacorp.api.entity.inventario.MovimientoStock;
import com.gadbacorp.api.entity.inventario.Productos;
import com.gadbacorp.api.repository.inventario.InventarioProductoRepository;
import com.gadbacorp.api.repository.inventario.MovimientoStockRepository;

/** Historial de movimientos de stock (kardex): quien, cuando, por que y en cuanto quedo el stock. */
@Service
public class KardexService {

    private final MovimientoStockRepository movimientos;
    private final InventarioProductoRepository stocks;

    public KardexService(MovimientoStockRepository movimientos, InventarioProductoRepository stocks) {
        this.movimientos = movimientos;
        this.stocks = stocks;
    }

    /**
     * Anota un movimiento. Debe llamarse DESPUES de aplicar el cambio de stock para que el stock resultante sea el real.
     *
     * @param cantidadBase con signo: positiva si entra stock, negativa si sale
     */
    @Transactional
    public void registrar(Productos producto, Integer idSucursal, BigDecimal cantidadBase, String tipo, String referencia) {
        if (cantidadBase == null || cantidadBase.signum() == 0) {
            return;
        }
        MovimientoStock m = new MovimientoStock();
        m.setFecha(LocalDateTime.now());
        m.setTipo(tipo);
        m.setProducto(producto);
        m.setIdSucursal(idSucursal);
        m.setCantidadBase(cantidadBase);
        m.setStockResultante(stockDe(producto.getIdproducto(), idSucursal));
        m.setReferencia(referencia == null ? null : referencia.length() > 120 ? referencia.substring(0, 120) : referencia);
        m.setUsuario(usuarioActual());
        movimientos.save(m);
    }

    @Transactional(readOnly = true)
    public List<MovimientoStock> consultar(Integer idProducto, Integer idSucursal, LocalDateTime desde, LocalDateTime hasta) {
        return movimientos.consultar(idProducto, idSucursal, desde, hasta);
    }

    private BigDecimal stockDe(Integer idProducto, Integer idSucursal) {
        return stocks.findAllByProducto_Idproducto(idProducto).stream()
                .filter(ip -> idSucursal == null || idSucursalDe(ip) != null && idSucursal.equals(idSucursalDe(ip)))
                .map(InventarioProducto::stockOCero)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /** Sucursal donde esta guardado el stock de esta fila; null si el inventario no tiene almacen o sucursal. */
    public static Integer idSucursalDe(InventarioProducto ip) {
        return ip.getInventario() == null || ip.getInventario().getAlmacen() == null
                || ip.getInventario().getAlmacen().getSucursal() == null
                ? null : ip.getInventario().getAlmacen().getSucursal().getIdSucursal();
    }

    private static String usuarioActual() {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        return a == null || !a.isAuthenticated() ? "sistema" : a.getName();
    }
}
