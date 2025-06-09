package com.gadbacorp.api.gadbacorp.service.jpa.inventario;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.gadbacorp.api.gadbacorp.entity.inventario.AlmacenProducto;
import com.gadbacorp.api.gadbacorp.entity.inventario.Almacenes;
import com.gadbacorp.api.gadbacorp.entity.inventario.Inventario;
import com.gadbacorp.api.gadbacorp.entity.inventario.Productos;
import com.gadbacorp.api.gadbacorp.entity.inventario.TrasladoAlmacenes;
import com.gadbacorp.api.gadbacorp.repository.inventario.AlmacenProductoRepository;
import com.gadbacorp.api.gadbacorp.repository.inventario.InventarioRepository;
import com.gadbacorp.api.gadbacorp.repository.inventario.TrasladoAlmacenesRepository;
import com.gadbacorp.api.gadbacorp.service.inventario.ITrasladoAlmacenesService;

@Service
public class TrasladoAlmacenesService implements ITrasladoAlmacenesService {

    @Autowired
    private TrasladoAlmacenesRepository trasladoRepo;

    @Autowired
    private AlmacenProductoRepository almacenProductoRepo;

    @Autowired
    private InventarioRepository inventarioRepo;

    @Override
    public List<TrasladoAlmacenes> buscarTodos() {
        return trasladoRepo.findAll();
    }

    @Override
    public Optional<TrasladoAlmacenes> buscarId(Integer id) {
        return trasladoRepo.findById(id);
    }

    @Override
    @Transactional
    public TrasladoAlmacenes guardar(TrasladoAlmacenes traslado) {
        return ejecutarTraslado(traslado);
    }

    @Override
    @Transactional
    public TrasladoAlmacenes modificar(TrasladoAlmacenes trasladoNuevo) {

        TrasladoAlmacenes original = trasladoRepo.findById(trasladoNuevo.getIdtraslado())
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Traslado no encontrado id=" + trasladoNuevo.getIdtraslado()
            ));

        revertirTraslado(original);

        trasladoNuevo.setIdtraslado(original.getIdtraslado());
        trasladoNuevo.setFechaTraslado(
            trasladoNuevo.getFechaTraslado() != null
                ? trasladoNuevo.getFechaTraslado()
                : original.getFechaTraslado()
        );

        return ejecutarTraslado(trasladoNuevo);
    }


    private TrasladoAlmacenes ejecutarTraslado(TrasladoAlmacenes traslado) {
        Productos producto = traslado.getProducto();
        Almacenes origen   = traslado.getOrigen();
        Almacenes destino  = traslado.getDestino();
        int cantidad       = traslado.getCantidad();

        if (origen.getIdalmacen().equals(destino.getIdalmacen())) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "El almacén origen y destino no pueden ser iguales."
            );
        }

        // restar stock en el origen
        AlmacenProducto apOrigen = almacenProductoRepo
            .findByProductoAndAlmacen(producto, origen)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "No hay stock del producto en el almacén de origen."
            ));
        apOrigen.setStock(apOrigen.getStock() - cantidad);
        almacenProductoRepo.save(apOrigen);
        actualizarInventario(producto, origen, apOrigen.getStock());

        
        AlmacenProducto apDestino = almacenProductoRepo
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
        almacenProductoRepo.save(apDestino);
        actualizarInventario(producto, destino, apDestino.getStock());

        // asignar fecha de traslado si no viene en el DTO
        if (traslado.getFechaTraslado() == null) {
            traslado.setFechaTraslado(LocalDateTime.now());
        }

        // guarda o actualiza el traslado
        return trasladoRepo.save(traslado);
    }

    private void revertirTraslado(TrasladoAlmacenes traslado) {
        Productos producto = traslado.getProducto();
        Almacenes origen   = traslado.getOrigen();
        Almacenes destino  = traslado.getDestino();
        int cantidad       = traslado.getCantidad();

        // devolver stock al origen
        AlmacenProducto apOrigen = almacenProductoRepo
            .findByProductoAndAlmacen(producto, origen)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "No hay stock previo en el origen para revertir."
            ));
        apOrigen.setStock(apOrigen.getStock() + cantidad);
        almacenProductoRepo.save(apOrigen);
        actualizarInventario(producto, origen, apOrigen.getStock());

        // quitar stock del destino
        AlmacenProducto apDestino = almacenProductoRepo
            .findByProductoAndAlmacen(producto, destino)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "No hay stock previo en el destino para revertir."
            ));
        apDestino.setStock(apDestino.getStock() - cantidad);
        almacenProductoRepo.save(apDestino);
        actualizarInventario(producto, destino, apDestino.getStock());
    }

    private void actualizarInventario(Productos producto, Almacenes almacen, int nuevoStock) {
        Optional<Inventario> optInv = inventarioRepo.findByProductoAndAlmacen(producto, almacen);
        Inventario inventario = optInv
            .orElseGet(() -> new Inventario(producto, almacen, 0));
        inventario.setStock(nuevoStock);
        inventarioRepo.save(inventario);
    }

    @Override
    @Transactional
    public void eliminar(Integer id) {
        
        if (!trasladoRepo.existsById(id)) {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Traslado no encontrado id=" + id
            );
        }
        trasladoRepo.deleteById(id);
    }
}
