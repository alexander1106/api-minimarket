package com.gadbacorp.api.service.jpa.compras;

import org.springframework.beans.factory.annotation.Autowired;
import com.gadbacorp.api.service.jpa.inventario.ConversionUnidadesService;
import org.springframework.stereotype.Service;

import com.gadbacorp.api.entity.compras.Compras;
import com.gadbacorp.api.entity.compras.DetallesCompras;
import com.gadbacorp.api.entity.inventario.MovimientoStock;
import com.gadbacorp.api.entity.inventario.Productos;
import com.gadbacorp.api.repository.compras.DetallesComprasRepository;
import com.gadbacorp.api.service.compras.IComprasService;
import com.gadbacorp.api.service.compras.IDetallesComprasService;
import com.gadbacorp.api.service.inventario.IProductosService;
import com.gadbacorp.api.service.jpa.inventario.LoteService;
import com.gadbacorp.api.service.jpa.inventario.StockService;

import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class DetallesComprasService implements IDetallesComprasService {

    @Autowired
    private DetallesComprasRepository detallesComprasRepository;

    @Autowired
    private IComprasService comprasService;

    @Autowired
    private IProductosService productosService;

    @Autowired
    private ConversionUnidadesService conversionUnidadesService;

    @Autowired
    private StockService stockService;

    @Autowired
    private LoteService loteService;

    @Override
    @Transactional
    public DetallesCompras guardarDetalle(DetallesCompras detalle, Integer idCompra, Integer idProducto) {
        Optional<Compras> compra = comprasService.buscarCompra(idCompra);
        Optional<Productos> producto = productosService.buscarId(idProducto);

        if (compra.isEmpty() || compra.get().getEstado() == 0) {
            throw new RuntimeException("Compra no encontrada o inactiva");
        }

        if (producto.isEmpty() || producto.get().getEstado() == 0) {
            throw new RuntimeException("Producto no encontrado o inactivo");
        }

        if (detalle.getFechaVencimiento() != null && detalle.getFechaVencimiento().isBefore(java.time.LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de vencimiento no puede ser anterior a hoy");
        }

        detalle.setCompra(compra.get());
        detalle.setProducto(producto.get());

        // La cantidad puede venir en una presentacion (10 sacos): se guarda tambien en unidad base (500 kg)
        ConversionUnidadesService.Conversion conversion = conversionUnidadesService.convertir(
                producto.get(), detalle.getIdPresentacionSolicitada(), detalle.getCantidad());
        detalle.setPresentacion(conversion.presentacion());
        detalle.setCantidadBase(conversion.cantidadBase());
        
        if (detalle.getPrecioUnitario() == null) {
            throw new RuntimeException("El precio unitario es requerido");
        }
        
        // Si precioCompra no viene, usar precioUnitario como valor por defecto
        if (detalle.getPrecioCompra() == null) {
            detalle.setPrecioCompra(detalle.getPrecioUnitario());
        }
        
        if (detalle.getSubTotal() == null) {
            BigDecimal subTotal = detalle.getPrecioUnitario().multiply(detalle.getCantidad());
            detalle.setSubTotal(subTotal);
        }

        if (detalle.getEstado() == null) {
            detalle.setEstado(1);
        }

        DetallesCompras guardado = detallesComprasRepository.save(detalle);
        moverStock(compra.get(), producto.get(), conversion.cantidadBase());
        if (compra.get().getSucursal() != null) {
            loteService.registrar(producto.get(), compra.get().getSucursal().getIdSucursal(), guardado.getIdDetalleCompra(),
                    detalle.getLote(), detalle.getFechaVencimiento(), conversion.cantidadBase(),
                    "Compra #" + compra.get().getIdCompra());
        }
        recalcularTotal(compra.get());
        return guardado;
    }

    @Override
    @Transactional
    public DetallesCompras actualizarDetalle(DetallesCompras detalle) {
        Optional<DetallesCompras> detalleExistente = detallesComprasRepository.findById(detalle.getIdDetalleCompra());

        if (detalleExistente.isEmpty() || detalleExistente.get().getEstado() == 0) {
            throw new RuntimeException("Detalle no encontrado o eliminado");
        }

        DetallesCompras detalleActual = detalleExistente.get();
        BigDecimal baseAnterior = detalleActual.cantidadEnBase();
        detalleActual.setCantidad(detalle.getCantidad());
        ConversionUnidadesService.Conversion conversion = conversionUnidadesService.convertir(
                detalleActual.getProducto(), detalle.getIdPresentacionSolicitada(), detalle.getCantidad());
        detalleActual.setPresentacion(conversion.presentacion());
        detalleActual.setCantidadBase(conversion.cantidadBase());
        detalleActual.setLote(detalle.getLote());
        detalleActual.setFechaVencimiento(detalle.getFechaVencimiento());
        detalleActual.setPrecioUnitario(detalle.getPrecioUnitario());
        detalleActual.setPrecioCompra(detalle.getPrecioCompra());
        detalleActual.setPrecioVenta(detalle.getPrecioVenta());
        
        BigDecimal subTotal = detalle.getPrecioUnitario().multiply(detalle.getCantidad());
        detalleActual.setSubTotal(subTotal);
        
        detalleActual.setEstado(detalle.getEstado());

        DetallesCompras guardado = detallesComprasRepository.save(detalleActual);
        moverStock(detalleActual.getCompra(), detalleActual.getProducto(),
                conversion.cantidadBase().subtract(baseAnterior));
        if (detalleActual.getCompra().getSucursal() != null) {
            loteService.sincronizarLinea(detalleActual.getProducto(), detalleActual.getCompra().getSucursal().getIdSucursal(),
                    detalleActual.getIdDetalleCompra(), detalle.getLote(), detalle.getFechaVencimiento(),
                    conversion.cantidadBase(), conversion.cantidadBase().subtract(baseAnterior),
                    "Compra #" + detalleActual.getCompra().getIdCompra());
        }
        recalcularTotal(detalleActual.getCompra());
        return guardado;
    }

    @Override
    @Transactional
    public void eliminarDetalle(Integer idDetalle) {
        Optional<DetallesCompras> detalle = detallesComprasRepository.findById(idDetalle);
        if (detalle.isPresent() && detalle.get().getEstado() == 1) {
            DetallesCompras detalleActual = detalle.get();
            detalleActual.setEstado(0);
            detallesComprasRepository.save(detalleActual);
            loteService.retirarDeLinea(detalleActual.getIdDetalleCompra(), detalleActual.cantidadEnBase());
            moverStock(detalleActual.getCompra(), detalleActual.getProducto(), detalleActual.cantidadEnBase().negate());
            recalcularTotal(detalleActual.getCompra());
        } else {
            throw new RuntimeException("Detalle no encontrado o ya eliminado");
        }
    }

    /** Una compra suma stock en su sucursal; quitar o reducir una linea lo devuelve. Compras antiguas sin sucursal no mueven stock. */
    private void moverStock(Compras compra, Productos producto, BigDecimal cantidadBase) {
        if (compra.getSucursal() == null || cantidadBase.signum() == 0) {
            return;
        }
        if (cantidadBase.signum() > 0) {
            stockService.ingresar(compra.getSucursal().getIdSucursal(), producto, cantidadBase,
                    MovimientoStock.COMPRA, "Compra #" + compra.getIdCompra());
        } else {
            stockService.descontar(compra.getSucursal().getIdSucursal(), producto.getIdproducto(), cantidadBase.negate(),
                    MovimientoStock.ANULACION_COMPRA, "Compra #" + compra.getIdCompra());
        }
    }

    /** El total de la compra es la suma de sus lineas activas; se mantiene al agregar, editar o quitar lineas. */
    private void recalcularTotal(Compras compra) {
        BigDecimal total = detallesComprasRepository.findByCompraIdCompra(compra.getIdCompra()).stream()
                .filter(d -> d.getEstado() != null && d.getEstado() == 1 && d.getSubTotal() != null)
                .map(DetallesCompras::getSubTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        compra.setTotal(total);
        comprasService.editarCompra(compra);
    }

    @Override
    public Optional<DetallesCompras> buscarDetallePorId(Integer idDetalle) {
        return detallesComprasRepository.findById(idDetalle)
                .filter(detalle -> detalle.getEstado() == 1);
    }

    @Override
    public List<DetallesCompras> buscarPorIdCompra(Integer idCompra) {
        return detallesComprasRepository.findByCompraIdCompra(idCompra)
                .stream()
                .filter(detalle -> detalle.getEstado() == 1)
                .toList();
    }

    @Override
    public List<DetallesCompras> listarTodosDetalles() {
        return detallesComprasRepository.findAll()
                .stream()
                .filter(detalle -> detalle.getEstado() == 1)
                .toList();
    }
}