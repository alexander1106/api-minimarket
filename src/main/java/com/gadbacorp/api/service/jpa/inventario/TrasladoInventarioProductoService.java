package com.gadbacorp.api.service.jpa.inventario;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.gadbacorp.api.entity.inventario.InventarioProducto;
import com.gadbacorp.api.entity.inventario.TrasladoInventarioProducto;
import com.gadbacorp.api.repository.inventario.InventarioProductoRepository;
import com.gadbacorp.api.repository.inventario.TrasladoInventarioProductoRepository;
import com.gadbacorp.api.service.inventario.ITrasladoInventarioProductoService;

@Service
public class TrasladoInventarioProductoService implements ITrasladoInventarioProductoService {

    @Autowired
    private TrasladoInventarioProductoRepository trasladoRepo;

    @Autowired
    private InventarioProductoRepository invProdRepo;

    @Override
    public List<TrasladoInventarioProducto> buscarTodos() {
        return trasladoRepo.findAll();
    }

    @Override
    public Optional<TrasladoInventarioProducto> buscarId(Integer id) {
        return trasladoRepo.findById(id);
    }

    @Override
    @Transactional
    public TrasladoInventarioProducto guardar(TrasladoInventarioProducto traslado) {
        return ejecutarTraslado(traslado);
    }

    private TrasladoInventarioProducto ejecutarTraslado(TrasladoInventarioProducto traslado) {
        // 1) Extraer los IDs que vienen en el objeto traslado (sin confiar en que la entidad esté “adjunta”)
        Integer idOrigen   = traslado.getOrigen()  .getIdinventarioproducto();
        Integer idDestino  = traslado.getDestino() .getIdinventarioproducto();

        // 2) Cargar las dos filas de InventarioProducto desde BD
        InventarioProducto origen = invProdRepo.findById(idOrigen)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "InventarioProducto origen no encontrado id=" + idOrigen
            ));

        InventarioProducto destino = invProdRepo.findById(idDestino)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "InventarioProducto destino no encontrado id=" + idDestino
            ));

        // 3) Validar que origen != destino
        if (origen.getIdinventarioproducto().equals(destino.getIdinventarioproducto())) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "El origen y destino no pueden ser la misma fila de inventario (id=" + idOrigen + ")."
            );
        }

        // 4) Validar que correspondan al mismo producto
        Integer prodOrigen  = origen.getProducto().getIdproducto();
        Integer prodDestino = destino.getProducto().getIdproducto();
        if (!prodOrigen.equals(prodDestino)) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "El origen (producto=" + prodOrigen + ") y el destino (producto=" + prodDestino + ") " +
                "deben pertenecer al mismo producto."
            );
        }

        // 5) Verificar que haya stock suficiente en “origen”
        Integer cantidad = traslado.getCantidad();
        if (cantidad == null || cantidad <= 0) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "La 'cantidad' a trasladar debe ser un entero positivo."
            );
        }
        if (origen.getStockactual() < cantidad) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "No hay stock suficiente en origen: stock actual=" + origen.getStockactual() +
                ", cantidad a trasladar=" + cantidad
            );
        }

        // 6) Restar stock en el origen y persistir ese cambio
        origen.setStockactual(origen.getStockactual() - cantidad);
        invProdRepo.save(origen);

        // 7) Sumar stock en el destino y persistir ese cambio
        destino.setStockactual(destino.getStockactual() + cantidad);
        invProdRepo.save(destino);

        if (traslado.getFechaTraslado() == null) {
            traslado.setFechaTraslado(LocalDateTime.now());
        }

        return trasladoRepo.save(traslado);
    }


    @Override
    @Transactional
    public TrasladoInventarioProducto modificar(TrasladoInventarioProducto trasladoNuevo) {
        throw new ResponseStatusException(
            HttpStatus.FORBIDDEN,
            "No está permitido modificar un traslado de inventario."
        );
    }

    @Override
    @Transactional
    public void eliminar(Integer id) {
        throw new ResponseStatusException(
            HttpStatus.FORBIDDEN,
            "No está permitido eliminar un traslado de inventario."
        );
    }

}
