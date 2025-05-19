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
import com.gadbacorp.api.entity.inventario.Productos;
import com.gadbacorp.api.entity.inventario.TrasladoAlmacenes;
import com.gadbacorp.api.repository.inventario.AlmacenProductoRepository;
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

    @Override
    public List<TrasladoAlmacenes> buscarTodos() {
        return repoTrasladoAlmacenes.findAll();
    }

    @Override
    public Optional<TrasladoAlmacenes> buscarId(Integer id) {
        return repoTrasladoAlmacenes.findById(id);
    }

    /**
     * Realiza el proceso de traslado y guarda el registro.
     */
    @Transactional
    protected TrasladoAlmacenes executarTraslado(TrasladoAlmacenes trasladoAlmacenes) {
        Productos producto = trasladoAlmacenes.getProducto();
        Almacenes origen = trasladoAlmacenes.getOrigen();
        Almacenes destino = trasladoAlmacenes.getDestino();
        int cantidad = trasladoAlmacenes.getCantidad();

        AlmacenProducto apOrigen = repoAlmacenProducto
            .findByProductoAndAlmacen(producto, origen)
            .orElseThrow(() -> new IllegalArgumentException(
                "No existe stock para producto " + producto.getIdproducto() +
                " en almacén origen " + origen.getIdalmacen()));
        if (apOrigen.getStock() < cantidad) {
            throw new IllegalArgumentException(
                "Stock insuficiente: disponible=" + apOrigen.getStock() + ", solicitado=" + cantidad);
        }

        apOrigen.setStock(apOrigen.getStock() - cantidad);
        repoAlmacenProducto.save(apOrigen);

        AlmacenProducto apDestino = repoAlmacenProducto
            .findByProductoAndAlmacen(producto, destino)
            .orElseGet(() -> {
                AlmacenProducto almacenproducto = new AlmacenProducto();
                almacenproducto.setProducto(producto);
                almacenproducto.setAlmacen(destino);
                almacenproducto.setStock(0);
                almacenproducto.setFechaIngreso(LocalDate.now());
                return almacenproducto;
            });
        apDestino.setStock(apDestino.getStock() + cantidad);
        repoAlmacenProducto.save(apDestino);

        if (trasladoAlmacenes.getFechaTraslado() == null) {
            trasladoAlmacenes.setFechaTraslado(LocalDateTime.now());
        }
        return repoTrasladoAlmacenes.save(trasladoAlmacenes);
    }

    @Override
    public TrasladoAlmacenes guardar(TrasladoAlmacenes trasladoAlmacenes) {
        return executarTraslado(trasladoAlmacenes);
    }

    @Override
    public TrasladoAlmacenes modificar(TrasladoAlmacenes trasladoAlmacenes) {
        // Para edición rápida sin cambiar stock, simplemente salva
        return repoTrasladoAlmacenes.save(trasladoAlmacenes);
    }

    @Override
    public void eliminar(Integer id) {
        repoTrasladoAlmacenes.deleteById(id);
    }
}
