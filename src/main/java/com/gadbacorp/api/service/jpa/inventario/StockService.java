package com.gadbacorp.api.service.jpa.inventario;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gadbacorp.api.entity.inventario.Almacenes;
import com.gadbacorp.api.entity.inventario.Inventario;
import com.gadbacorp.api.entity.inventario.InventarioProducto;
import com.gadbacorp.api.entity.inventario.MovimientoStock;
import com.gadbacorp.api.entity.notificaciones.Notificacion;
import com.gadbacorp.api.service.jpa.notificaciones.NotificacionService;
import com.gadbacorp.api.entity.inventario.Productos;
import com.gadbacorp.api.excepciones.OperacionNoPermitidaException;
import com.gadbacorp.api.excepciones.StockInsuficienteException;
import com.gadbacorp.api.repository.inventario.AlmacenesRepository;
import com.gadbacorp.api.repository.inventario.InventarioProductoRepository;
import com.gadbacorp.api.repository.inventario.InventarioRepository;

/**
 * Unico punto que descuenta stock por venta. Trabaja siempre en unidad base y con decimales (kg, litros).
 * Reparte el descuento entre los almacenes e inventarios de la sucursal y solo modifica algo si alcanza el stock total.
 */
@Service
public class StockService {

    private final AlmacenesRepository almacenesRepository;
    private final InventarioRepository inventarioRepository;
    private final InventarioProductoRepository inventarioProductoRepository;
    private final KardexService kardex;
    private final LoteService lotes;
    private final NotificacionService avisos;

    public StockService(AlmacenesRepository almacenesRepository, InventarioRepository inventarioRepository,
                        InventarioProductoRepository inventarioProductoRepository, KardexService kardex, LoteService lotes,
                        NotificacionService avisos) {
        this.kardex = kardex;
        this.lotes = lotes;
        this.avisos = avisos;
        this.almacenesRepository = almacenesRepository;
        this.inventarioRepository = inventarioRepository;
        this.inventarioProductoRepository = inventarioProductoRepository;
    }

    /**
     * @param cantidadBase cantidad a descontar, en unidad base del producto
     * @throws StockInsuficienteException si el stock total de la sucursal no cubre la cantidad (no se descuenta nada)
     */
    @Transactional
    public void descontar(Integer idSucursal, Integer idProducto, BigDecimal cantidadBase) {
        descontar(idSucursal, idProducto, cantidadBase, MovimientoStock.AJUSTE, null);
    }

    /** Igual que {@link #descontar(Integer, Integer, BigDecimal)} y anota el movimiento en el kardex con su motivo. */
    @Transactional
    public void descontar(Integer idSucursal, Integer idProducto, BigDecimal cantidadBase, String tipo, String referencia) {
        List<InventarioProducto> filas = stockDeLaSucursal(idSucursal, idProducto);
        BigDecimal disponible = filas.stream().map(InventarioProducto::stockOCero).reduce(BigDecimal.ZERO, BigDecimal::add);
        if (disponible.compareTo(cantidadBase) < 0) {
            throw new StockInsuficienteException("Stock insuficiente para producto ID " + idProducto
                    + ". Disponible: " + disponible.stripTrailingZeros().toPlainString()
                    + ", solicitado: " + cantidadBase.stripTrailingZeros().toPlainString());
        }

        BigDecimal pendiente = cantidadBase;
        for (InventarioProducto fila : filas) {
            if (pendiente.signum() == 0) {
                break;
            }
            BigDecimal tomar = fila.stockOCero().min(pendiente);
            if (tomar.signum() > 0) {
                fila.restarStock(tomar);
                inventarioProductoRepository.save(fila);
                pendiente = pendiente.subtract(tomar);
            }
        }
        kardex.registrar(filas.get(0).getProducto(), idSucursal, cantidadBase.negate(), tipo, referencia);
        if (!MovimientoStock.ANULACION_COMPRA.equals(tipo)) {
            lotes.consumirFefo(idSucursal, idProducto, cantidadBase);
        }
        avisarSiQuedaBajo(filas.get(0).getProducto(), idSucursal);
    }

    /** Avisa a quienes ven Inventario cuando el stock de la sucursal llega al minimo del producto o por debajo. */
    private void avisarSiQuedaBajo(Productos producto, Integer idSucursal) {
        if (producto == null || producto.getStockMinimo() == null || producto.getEmpresa() == null) {
            return;
        }
        BigDecimal stock = stockTotal(idSucursal, producto.getIdproducto());
        if (stock.compareTo(producto.getStockMinimo()) <= 0) {
            avisos.notificarEmpresa(producto.getEmpresa().getIdempresa(), "Inventario", Notificacion.STOCK_BAJO,
                    stock.signum() == 0 ? Notificacion.CRITICA : Notificacion.ADVERTENCIA,
                    stock.signum() == 0 ? "Producto agotado" : "Stock bajo",
                    producto.getNombre() + ": quedan " + stock.stripTrailingZeros().toPlainString()
                            + " (mínimo " + producto.getStockMinimo().stripTrailingZeros().toPlainString() + ")",
                    "/admin/productos", "STOCK:" + producto.getIdproducto() + ":" + idSucursal);
        }
    }

    /**
     * Suma stock (compras, devoluciones). Va al primer inventario de la sucursal donde ya esta el producto; si el producto
     * aun no esta en ninguno, se crea en el primer inventario de la sucursal.
     *
     * @param cantidadBase cantidad a sumar, en unidad base del producto
     * @throws OperacionNoPermitidaException si la sucursal no tiene ningun inventario donde guardar el stock
     */
    @Transactional
    public void ingresar(Integer idSucursal, Productos producto, BigDecimal cantidadBase) {
        ingresar(idSucursal, producto, cantidadBase, MovimientoStock.AJUSTE, null);
    }

    /** Igual que {@link #ingresar(Integer, Productos, BigDecimal)} y anota el movimiento en el kardex con su motivo. */
    @Transactional
    public void ingresar(Integer idSucursal, Productos producto, BigDecimal cantidadBase, String tipo, String referencia) {
        List<InventarioProducto> filas = stockDeLaSucursal(idSucursal, producto.getIdproducto());
        if (!filas.isEmpty()) {
            InventarioProducto fila = filas.get(0);
            fila.sumarStock(cantidadBase);
            inventarioProductoRepository.save(fila);
            kardex.registrar(producto, idSucursal, cantidadBase, tipo, referencia);
            return;
        }
        for (Almacenes almacen : almacenesRepository.findBySucursalIdSucursal(idSucursal)) {
            List<Inventario> inventarios = inventarioRepository.findByAlmacen_Idalmacen(almacen.getIdalmacen());
            if (!inventarios.isEmpty()) {
                InventarioProducto nueva = new InventarioProducto();
                nueva.setProducto(producto);
                nueva.setInventario(inventarios.get(0));
                nueva.setStockactual(cantidadBase);
                nueva.setFechaingreso(java.time.LocalDateTime.now());
                inventarioProductoRepository.save(nueva);
                kardex.registrar(producto, idSucursal, cantidadBase, tipo, referencia);
                return;
            }
        }
        throw new OperacionNoPermitidaException(
                "La sucursal no tiene un inventario donde guardar el stock. Cree un almacén y un inventario primero.");
    }

    /** Mueve stock por una diferencia en unidad base: positiva suma (ingresa), negativa descuenta. Cero no hace nada. */
    @Transactional
    public void mover(Integer idSucursal, Productos producto, BigDecimal diferenciaBase) {
        mover(idSucursal, producto, diferenciaBase, MovimientoStock.AJUSTE, null);
    }

    /** Igual que {@link #mover(Integer, Productos, BigDecimal)} con el motivo para el kardex. */
    @Transactional
    public void mover(Integer idSucursal, Productos producto, BigDecimal diferenciaBase, String tipo, String referencia) {
        if (diferenciaBase.signum() > 0) {
            ingresar(idSucursal, producto, diferenciaBase, tipo, referencia);
        } else if (diferenciaBase.signum() < 0) {
            descontar(idSucursal, producto.getIdproducto(), diferenciaBase.negate(), tipo, referencia);
        }
    }

    /** Sucursal donde se vendio: la del cliente de la venta. */
    public static Integer sucursalDe(com.gadbacorp.api.entity.ventas.Ventas venta) {
        if (venta == null || venta.getCliente() == null || venta.getCliente().getSucursal() == null) {
            throw new IllegalArgumentException("La venta no tiene una sucursal asociada (cliente sin sucursal)");
        }
        return venta.getCliente().getSucursal().getIdSucursal();
    }

    /** Stock total del producto en la sucursal (o en todas si {@code idSucursal} es null), en unidad base. */
    @Transactional(readOnly = true)
    public BigDecimal stockTotal(Integer idSucursal, Integer idProducto) {
        List<InventarioProducto> filas = idSucursal == null
                ? inventarioProductoRepository.findAllByProducto_Idproducto(idProducto)
                : stockDeLaSucursal(idSucursal, idProducto);
        return filas.stream()
                .map(InventarioProducto::stockOCero).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private List<InventarioProducto> stockDeLaSucursal(Integer idSucursal, Integer idProducto) {
        List<InventarioProducto> filas = new ArrayList<>();
        for (Almacenes almacen : almacenesRepository.findBySucursalIdSucursal(idSucursal)) {
            for (Inventario inventario : inventarioRepository.findByAlmacen_Idalmacen(almacen.getIdalmacen())) {
                inventarioProductoRepository
                        .findByProductoIdproductoAndInventarioIdinventario(idProducto, inventario.getIdinventario())
                        .ifPresent(filas::add);
            }
        }
        return filas;
    }
}
