package com.gadbacorp.api.service.jpa.inventario;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.gadbacorp.api.entity.inventario.InventarioProducto;
import com.gadbacorp.api.entity.inventario.TrasladoInventarioProducto;
import com.gadbacorp.api.entity.inventario.TrasladoInventarioProductoDTO;
import com.gadbacorp.api.repository.inventario.InventarioProductoRepository;
import com.gadbacorp.api.repository.inventario.TrasladoInventarioProductoRepository;

@Service
@Transactional
public class TrasladoInventarioProductoService {

    @Autowired
    private TrasladoInventarioProductoRepository trasladoRepo;

    @Autowired
    private InventarioProductoRepository invProdRepo;

    public List<TrasladoInventarioProductoDTO> listarTodos() {
        return trasladoRepo.findAll().stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    public TrasladoInventarioProductoDTO obtenerPorId(Integer id) {
        return trasladoRepo.findById(id)
            .map(this::toDTO)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Traslado no encontrado id=" + id
            ));
    }

    public TrasladoInventarioProductoDTO crearTraslado(TrasladoInventarioProductoDTO dto) {
        // 1) Origen ≠ Destino
        if (dto.getOrigenId().equals(dto.getDestinoId())) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Origen y destino no pueden ser iguales"
            );
        }

        // 2) Cargar entidades
        InventarioProducto origen = invProdRepo.findById(dto.getOrigenId())
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Origen no encontrado id=" + dto.getOrigenId()
            ));
        InventarioProducto destino = invProdRepo.findById(dto.getDestinoId())
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Destino no encontrado id=" + dto.getDestinoId()
            ));

        // 3) Deben ser mismo producto
        if (!origen.getProducto().getIdproducto()
                   .equals(destino.getProducto().getIdproducto())) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Origen y destino deben ser del mismo producto"
            );
        }

        // 4) Cantidad > 0
        int cantidad = dto.getCantidad();
        if (cantidad <= 0) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "La cantidad debe ser mayor que cero"
            );
        }

        // 5) Stock suficiente
        if (origen.getStockactual() < cantidad) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Stock insuficiente en origen: actual=" + origen.getStockactual()
            );
        }

        // 6) Aplicar traslado
        origen.setStockactual(origen.getStockactual() - cantidad);
        destino.setStockactual(destino.getStockactual() + cantidad);
        invProdRepo.saveAll(List.of(origen, destino));

        // 7) Guardar registro de traslado
        TrasladoInventarioProducto traslado = new TrasladoInventarioProducto();
        traslado.setOrigen(origen);
        traslado.setDestino(destino);
        traslado.setCantidad(cantidad);
        traslado.setDescripcion(dto.getDescripcion());
        traslado.setFechaTraslado(
            dto.getFechaTraslado() != null
                ? dto.getFechaTraslado()
                : LocalDateTime.now()
        );
        TrasladoInventarioProducto guardado = trasladoRepo.save(traslado);

        return toDTO(guardado);
    }

    public TrasladoInventarioProductoDTO actualizarTraslado(TrasladoInventarioProductoDTO dto) {
        throw new ResponseStatusException(
            HttpStatus.METHOD_NOT_ALLOWED,
            "No está permitido editar traslados"
        );
    }

    public void eliminarTraslado(Integer id) {
        throw new ResponseStatusException(
            HttpStatus.METHOD_NOT_ALLOWED,
            "No está permitido eliminar traslados"
        );
    }

    private TrasladoInventarioProductoDTO toDTO(TrasladoInventarioProducto t) {
        TrasladoInventarioProductoDTO dto = new TrasladoInventarioProductoDTO();
        dto.setIdtraslado(t.getIdtraslado());
        dto.setOrigenId(t.getOrigen().getIdinventarioproducto());
        dto.setDestinoId(t.getDestino().getIdinventarioproducto());
        dto.setCantidad(t.getCantidad());
        dto.setDescripcion(t.getDescripcion());
        dto.setFechaTraslado(t.getFechaTraslado());
        return dto;
    }
}
