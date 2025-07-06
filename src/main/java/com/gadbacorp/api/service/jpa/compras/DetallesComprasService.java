package com.gadbacorp.api.service.jpa.compras;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gadbacorp.api.entity.compras.Compras;
import com.gadbacorp.api.entity.compras.DetallesCompras;
import com.gadbacorp.api.entity.compras.DetallesComprasDTO;
import com.gadbacorp.api.entity.inventario.Productos;
import com.gadbacorp.api.repository.compras.DetallesComprasRepository;
import com.gadbacorp.api.service.compras.IComprasService;
import com.gadbacorp.api.service.compras.IDetallesComprasService;
import com.gadbacorp.api.service.inventario.IProductosService;

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

    @Override
    @Transactional
    public DetallesCompras guardarDetalle(DetallesCompras detalle, Integer idCompra, Integer idProducto) {
        // Buscar compra y producto
        Optional<Compras> compra = comprasService.buscarCompra(idCompra);
        Optional<Productos> producto = productosService.buscarId(idProducto);

        if (compra.isEmpty() || compra.get().getEstado() == 0) {
            throw new RuntimeException("Compra no encontrada o inactiva");
        }

        if (producto.isEmpty() || producto.get().getEstado() == 0) {
            throw new RuntimeException("Producto no encontrado o inactivo");
        }

        // Establecer relaciones
        detalle.setCompra(compra.get());
        detalle.setProducto(producto.get());
        
        // Calcular subtotal si no está establecido
        if (detalle.getSubTotal() == null) {
            BigDecimal subTotal = detalle.getPrecioUnitario().multiply(new BigDecimal(detalle.getCantidad()));
            detalle.setSubTotal(subTotal);
        }

        // Establecer estado por defecto si no viene
        if (detalle.getEstado() == null) {
            detalle.setEstado(1);
        }

        return detallesComprasRepository.save(detalle);
    }

    @Override
    @Transactional
    public DetallesCompras actualizarDetalle(DetallesCompras detalle) {
        Optional<DetallesCompras> detalleExistente = detallesComprasRepository.findById(detalle.getIdDetalleCompra());

        if (detalleExistente.isEmpty() || detalleExistente.get().getEstado() == 0) {
            throw new RuntimeException("Detalle no encontrado o eliminado");
        }

        DetallesCompras detalleActual = detalleExistente.get();
        detalleActual.setCantidad(detalle.getCantidad());
        detalleActual.setPrecioUnitario(detalle.getPrecioUnitario());
        
        // Recalcular subtotal
        BigDecimal subTotal = detalle.getPrecioUnitario().multiply(new BigDecimal(detalle.getCantidad()));
        detalleActual.setSubTotal(subTotal);
        
        detalleActual.setEstado(detalle.getEstado());

        return detallesComprasRepository.save(detalleActual);
    }

    @Override
    @Transactional
    public void eliminarDetalle(Integer idDetalle) {
        Optional<DetallesCompras> detalle = detallesComprasRepository.findById(idDetalle);
        if (detalle.isPresent() && detalle.get().getEstado() == 1) {
            DetallesCompras detalleActual = detalle.get();
            detalleActual.setEstado(0);
            detallesComprasRepository.save(detalleActual);
        } else {
            throw new RuntimeException("Detalle no encontrado o ya eliminado");
        }
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