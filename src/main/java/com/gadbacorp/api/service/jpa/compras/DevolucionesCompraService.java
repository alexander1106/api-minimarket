package com.gadbacorp.api.service.jpa.compras;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gadbacorp.api.entity.compras.Compras;
import com.gadbacorp.api.entity.compras.DevolucionesCompra;
import com.gadbacorp.api.entity.compras.DevolucionesCompraDTO;
import com.gadbacorp.api.entity.inventario.Productos;
import com.gadbacorp.api.entity.ventas.MetodosPago;
import com.gadbacorp.api.repository.compras.ComprasRepository;
import com.gadbacorp.api.repository.compras.DevolucionesCompraRepository;
import com.gadbacorp.api.repository.inventario.ProductosRepository;
import com.gadbacorp.api.repository.ventas.MetodosPagoRepository;
import com.gadbacorp.api.service.compras.IDevolucionesCompraService;

@Service
public class DevolucionesCompraService implements IDevolucionesCompraService {

    @Autowired
    private DevolucionesCompraRepository devolucionesCompraRepository;

    @Autowired
    private ComprasRepository comprasRepository;

    @Autowired
    private ProductosRepository productosRepository;

    @Autowired
    private MetodosPagoRepository metodosPagoRepository;

    @Override
    public List<DevolucionesCompra> findAll() {
        return devolucionesCompraRepository.findAll();
    }

    @Override
    public Optional<DevolucionesCompra> findById(Integer id) {
        return devolucionesCompraRepository.findById(id);
    }

    @Override
    public DevolucionesCompra save(DevolucionesCompraDTO devolucionDTO) {
        DevolucionesCompra devolucion = new DevolucionesCompra();
        return guardarOActualizar(devolucion, devolucionDTO);
    }

    @Override
    public DevolucionesCompra update(Integer id, DevolucionesCompraDTO devolucionDTO) {
        Optional<DevolucionesCompra> devolucionExistente = devolucionesCompraRepository.findById(id);
        if (devolucionExistente.isPresent()) {
            DevolucionesCompra devolucion = devolucionExistente.get();
            return guardarOActualizar(devolucion, devolucionDTO);
        }
        return null;
    }

    private DevolucionesCompra guardarOActualizar(DevolucionesCompra devolucion, DevolucionesCompraDTO dto) {
        Optional<Compras> compra = comprasRepository.findById(dto.getIdCompra());
        Optional<Productos> producto = productosRepository.findById(dto.getIdProducto());
        Optional<MetodosPago> metodoPago = metodosPagoRepository.findById(dto.getIdMetodoPago());

        if (!compra.isPresent() || !producto.isPresent() || !metodoPago.isPresent()) {
            return null;
        }

        devolucion.setCompra(compra.get());
        devolucion.setProducto(producto.get());
        devolucion.setMetodoPago(metodoPago.get());
        devolucion.setCantidadDevuelta(dto.getCantidadDevuelta());
        devolucion.setMotivo(dto.getMotivo());
        devolucion.setFechaDevolucion(dto.getFechaDevolucion());
        devolucion.setEstado(1); // Estado activo por defecto

        return devolucionesCompraRepository.save(devolucion);
    }

    @Override
    public void delete(Integer id) {
        devolucionesCompraRepository.deleteById(id);
    }
}