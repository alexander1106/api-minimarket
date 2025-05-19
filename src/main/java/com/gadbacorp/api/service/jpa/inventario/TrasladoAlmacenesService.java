package com.gadbacorp.api.service.jpa.inventario;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gadbacorp.api.entity.inventario.AlmacenProducto;
import com.gadbacorp.api.entity.inventario.Almacenes;
import com.gadbacorp.api.entity.inventario.Inventario;
import com.gadbacorp.api.entity.inventario.Productos;
import com.gadbacorp.api.entity.inventario.TrasladoAlmacenes;
import com.gadbacorp.api.repository.inventario.AlmacenProductoRepository;
import com.gadbacorp.api.repository.inventario.InventarioRepository;
import com.gadbacorp.api.repository.inventario.TrasladoAlmacenesRepository;
import com.gadbacorp.api.service.inventario.ITrasladoAlmacenesService;

/**
 * Servicio para gestionar traslados de productos entre almacenes.
 */
@Service
public class TrasladoAlmacenesService implements ITrasladoAlmacenesService {

    @Autowired
    private TrasladoAlmacenesRepository repoTrasladoAlmacenes;

    @Autowired
    private AlmacenProductoRepository repoAlmacenProducto;

    @Autowired
    private InventarioRepository repoInventario;

    @Override
    public List<TrasladoAlmacenes> buscarTodos() {
        return repoTrasladoAlmacenes.findAll();
    }

    @Override
    public Optional<TrasladoAlmacenes> buscarId(Integer id) {
        return repoTrasladoAlmacenes.findById(id);
    }

    @Override
    @Transactional
    public TrasladoAlmacenes guardar(TrasladoAlmacenes trasladoAlmacenes) {
        return ejecutarTraslado(trasladoAlmacenes);
    }

    private TrasladoAlmacenes ejecutarTraslado(TrasladoAlmacenes traslado) {
        Productos producto = traslado.getProducto();
        Almacenes origen = traslado.getOrigen();
        Almacenes destino = traslado.getDestino();
        int cantidad = traslado.getCantidad();

        if (origen.getIdalmacen().equals(destino.getIdalmacen())) {
            throw new IllegalArgumentException("El almacén origen y destino no pueden ser iguales.");
        }

        AlmacenProducto apOrigen = repoAlmacenProducto
            .findByProductoAndAlmacen(producto, origen)
            .orElseThrow(() -> new IllegalArgumentException("No hay stock del producto en el almacén de origen."));

        if (apOrigen.getStock() < cantidad) {
            throw new IllegalArgumentException("Stock insuficiente en almacén origen. Disponible: " + apOrigen.getStock());
        }

        apOrigen.setStock(apOrigen.getStock() - cantidad);
        repoAlmacenProducto.save(apOrigen);
        actualizarInventario(producto, origen, apOrigen.getStock());

        AlmacenProducto apDestino = repoAlmacenProducto
            .findByProductoAndAlmacen(producto, destino)
            .orElseGet(() -> {
                AlmacenProducto nuevo = new AlmacenProducto();
                nuevo.setProducto(producto);
                nuevo.setAlmacen(destino);
                nuevo.setStock(0);
                nuevo.setFechaIngreso(LocalDate.now());
                return nuevo;
            });
        apDestino.setStock(apDestino.getStock() + cantidad);
        repoAlmacenProducto.save(apDestino);
        actualizarInventario(producto, destino, apDestino.getStock());

        if (traslado.getFechaTraslado() == null) {
            traslado.setFechaTraslado(LocalDateTime.now());
        }

        return repoTrasladoAlmacenes.save(traslado);
    }

    private void actualizarInventario(Productos producto, Almacenes almacen, int nuevoStock) {
        Optional<Inventario> optInv = repoInventario.findByProductoAndAlmacen(producto, almacen);
        Inventario inventario = optInv.orElseGet(() -> new Inventario(producto, almacen, 0));
        inventario.setStock(nuevoStock);
        repoInventario.save(inventario);
    }

    @Override
    public TrasladoAlmacenes modificar(TrasladoAlmacenes traslado) {
        return repoTrasladoAlmacenes.save(traslado);
    }

    @Override
    public void eliminar(Integer id) {
        repoTrasladoAlmacenes.deleteById(id);
    }
}


