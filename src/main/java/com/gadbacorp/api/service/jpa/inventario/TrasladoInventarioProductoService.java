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

    @Autowired
    private KardexService kardex;

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
        if (dto.getOrigenId().equals(dto.getDestinoId())) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Origen y destino no pueden ser iguales"
            );
        }

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

        if (!origen.getProducto().getIdproducto()
                   .equals(destino.getProducto().getIdproducto())) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Origen y destino deben ser del mismo producto"
            );
        }

        java.math.BigDecimal cantidad = dto.getCantidad();
        if (cantidad == null || cantidad.signum() <= 0) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "La cantidad debe ser mayor que cero"
            );
        }

        try {
            ConversionUnidadesService.validarFraccion(origen.getProducto(), cantidad);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }

        if (!origen.tieneStock(cantidad)) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Stock insuficiente en origen: actual=" + origen.getStockactual()
            );
        }

        origen.restarStock(cantidad);
        destino.sumarStock(cantidad);
        invProdRepo.saveAll(List.of(origen, destino));
        String ref = "Traslado " + (dto.getDescripcion() == null ? "" : dto.getDescripcion());
        kardex.registrar(origen.getProducto(), KardexService.idSucursalDe(origen), cantidad.negate(),
            com.gadbacorp.api.entity.inventario.MovimientoStock.TRASLADO_SALIDA, ref);
        kardex.registrar(destino.getProducto(), KardexService.idSucursalDe(destino), cantidad,
            com.gadbacorp.api.entity.inventario.MovimientoStock.TRASLADO_ENTRADA, ref);

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
