package com.gadbacorp.api.service.jpa.compras;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gadbacorp.api.entity.compras.CompraCompletaDTO;
import com.gadbacorp.api.entity.compras.Compras;
import com.gadbacorp.api.entity.compras.DetallesCompras;
import com.gadbacorp.api.entity.compras.DetallesComprasDTO;
import com.gadbacorp.api.entity.compras.Proveedores;
import com.gadbacorp.api.entity.inventario.Productos;
import com.gadbacorp.api.entity.ventas.MetodosPago;
import com.gadbacorp.api.repository.compras.ComprasRepository;
import com.gadbacorp.api.repository.compras.DetallesComprasRepository;
import com.gadbacorp.api.repository.inventario.ProductosRepository;
import com.gadbacorp.api.service.compras.IComprasService;
import com.gadbacorp.api.service.jpa.ventas.MetodosPagoService;

@Service
public class ComprasService implements IComprasService {

    @Autowired
    private ComprasRepository comprasRepository;
    
    @Autowired
    private DetallesComprasRepository detallesRepo;
    
    @Autowired
    private ProductosRepository productosRepo;
    
    @Autowired
    private ProveedoresService proveedoresService;
    
    @Autowired
    private MetodosPagoService metodosPagoService;

    @Override
    public Compras guardarCompra(Compras compra) {
        return comprasRepository.save(compra);
    }

    @Override
    public Compras editarCompra(Compras compra) {
        return comprasRepository.save(compra);
    }

    @Override
    public void eliminarCompra(Integer idCompra) {
        comprasRepository.deleteById(idCompra);
    }

    @Override
    public Optional<Compras> buscarCompra(Integer idCompra) {
        return comprasRepository.findById(idCompra);
    }

    @Override
    public List<Compras> listarCompras() {
        return comprasRepository.findAll();
    }

    @Override
    public List<Compras> obtenerComprasPorProveedor(Integer idProveedor) {
        return comprasRepository.findByProveedorIdProveedor(idProveedor);
    }

    @Override
    public DetallesComprasDTO obtenerPreciosProducto(Integer idProducto) {
        Productos producto = productosRepo.findById(idProducto)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        
        DetallesComprasDTO detalleDTO = new DetallesComprasDTO();
        detalleDTO.setIdProducto(idProducto);
        detalleDTO.setNombreProducto(producto.getNombre());
        detalleDTO.setPrecioCompra(producto.getCostoCompra());
        detalleDTO.setPrecioVenta(producto.getCostoVenta());
        detalleDTO.setPrecioUnitario(producto.getCostoCompra());
        
        return detalleDTO;
    }

    @Override
    public CompraCompletaDTO crearCompraCompleta(CompraCompletaDTO compraDTO) {
        // Validar proveedor
        Proveedores proveedor = proveedoresService.buscarId(compraDTO.getIdProveedor())
            .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));
        
        // Validar método de pago
        MetodosPago metodoPago = metodosPagoService.obtenerMetodoPago(compraDTO.getIdMetodoPago())
            .orElseThrow(() -> new RuntimeException("Método de pago no encontrado"));
        
        // Crear la compra
        Compras compra = new Compras();
        compra.setProveedor(proveedor);
        compra.setMetodoPago(metodoPago);
        compra.setDescripcion(compraDTO.getDescripcion());
        compra.setFechaCompra(LocalDateTime.now());
        compra.setEstado(1);
        
        // Calcular total
        BigDecimal total = compraDTO.getDetalles().stream()
            .map(d -> d.getPrecioCompra().multiply(BigDecimal.valueOf(d.getCantidad())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        compra.setTotal(total);
        
        // Guardar compra
        Compras compraGuardada = comprasRepository.save(compra);
        
        // Guardar detalles
        for (DetallesComprasDTO detalleDTO : compraDTO.getDetalles()) {
            Productos producto = productosRepo.findById(detalleDTO.getIdProducto())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
            
            DetallesCompras detalle = new DetallesCompras();
            detalle.setCompra(compraGuardada);
            detalle.setProducto(producto);
            detalle.setCantidad(detalleDTO.getCantidad());
            detalle.setPrecioUnitario(detalleDTO.getPrecioUnitario());
            detalle.setPrecioCompra(detalleDTO.getPrecioCompra());
            detalle.setPrecioVenta(detalleDTO.getPrecioVenta());
            detalle.setSubTotal(detalleDTO.getPrecioCompra().multiply(BigDecimal.valueOf(detalleDTO.getCantidad())));
            detalle.setEstado(1);
            
            detallesRepo.save(detalle);
        }
        
        compraDTO.setIdCompra(compraGuardada.getIdCompra());
        compraDTO.setTotal(total);
        return compraDTO;
    }


}