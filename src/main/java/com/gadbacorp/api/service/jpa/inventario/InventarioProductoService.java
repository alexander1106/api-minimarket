package com.gadbacorp.api.service.jpa.inventario;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.gadbacorp.api.entity.inventario.Inventario;
import com.gadbacorp.api.entity.inventario.InventarioProducto;
import com.gadbacorp.api.entity.inventario.InventarioProductosDTO;
import com.gadbacorp.api.entity.inventario.Productos;
import com.gadbacorp.api.repository.inventario.InventarioProductoRepository;
import com.gadbacorp.api.repository.inventario.InventarioRepository;
import com.gadbacorp.api.repository.inventario.ProductosRepository;
import com.gadbacorp.api.service.inventario.IInventarioProductoService;

@Service
public class InventarioProductoService implements IInventarioProductoService {

    @Autowired
    private InventarioProductoRepository repoInventarioProducto;

    @Autowired
    private ProductosRepository repoProductos;

    @Autowired
    private InventarioRepository repoInventario;

    @Override
    public List<InventarioProductosDTO> listarTodosDTO() {
        return repoInventarioProducto.findAll().stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public Optional<InventarioProducto> buscarPorId(Integer idinventarioproducto) {
        return repoInventarioProducto.findById(idinventarioproducto);
    }

    @Override
    public Optional<InventarioProductosDTO> buscarPorIdDTO(Integer idinventarioproducto) {
        return repoInventarioProducto.findById(idinventarioproducto)
            .map(this::toDTO);
    }

    @Override
public InventarioProductosDTO crearDTO(InventarioProductosDTO dto) {
    // 1. Validar existencia de producto e inventario
    Productos producto = repoProductos.findById(dto.getIdproducto())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
            "Producto no encontrado id=" + dto.getIdproducto()));
    Inventario inventario = repoInventario.findById(dto.getIdinventario())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
            "Inventario no encontrado id=" + dto.getIdinventario()));

    // 2. Buscar si ya hay una fila con (producto, inventario)
    Optional<InventarioProducto> optExistente = repoInventarioProducto
        .findByProductoIdproductoAndInventarioIdinventario(dto.getIdproducto(), dto.getIdinventario());

    InventarioProducto entidad;
    if (optExistente.isPresent()) {
        // Ya existe → actualizamos (sumamos) el stock
        entidad = optExistente.get();
        int stockViejo = entidad.getStockactual() == null ? 0 : entidad.getStockactual();
        entidad.setStockactual(stockViejo + dto.getStockactual());

        // opcional: podrías querer actualizar la fechaIngreso manualmente,
        // pero si la dejas con @CreationTimestamp, se mantiene la original.
    } else {
        // No existe → crear nueva fila
        entidad = new InventarioProducto();
        entidad.setProducto(producto);
        entidad.setInventario(inventario);
        entidad.setStockactual(dto.getStockactual());
        // la fechaIngreso se pone automática por @CreationTimestamp
    }

    // 3. Guardar (ya sea la fila nueva o la existente modificada)
    InventarioProducto guardado = repoInventarioProducto.save(entidad);
    return toDTO(guardado);
}


    @Override
    public InventarioProductosDTO actualizarDTO(Integer idinventarioproducto, InventarioProductosDTO dto) {
        InventarioProducto existente = repoInventarioProducto.findById(idinventarioproducto)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "InventarioProducto no encontrado id=" + idinventarioproducto
            ));

        // Si cambió el producto, validar y asignar
        if (!existente.getProducto().getIdproducto().equals(dto.getIdproducto())) {
            Productos prod = repoProductos.findById(dto.getIdproducto())
                .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Producto no encontrado id=" + dto.getIdproducto()
                ));
            existente.setProducto(prod);
        }
        // Si cambió el inventario, validar y asignar
        if (!existente.getInventario().getIdinventario().equals(dto.getIdinventario())) {
            Inventario inv = repoInventario.findById(dto.getIdinventario())
                .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Inventario no encontrado id=" + dto.getIdinventario()
                ));
            existente.setInventario(inv);
        }
        // Actualizar stockactual
        existente.setStockactual(dto.getStockactual());
        // No modificamos fechaingreso; mantiene el timestamp original

        InventarioProducto modificado = repoInventarioProducto.save(existente);
        return toDTO(modificado);
    }

    @Override
    public void eliminar(Integer idinventarioproducto) {
        if (!repoInventarioProducto.existsById(idinventarioproducto)) {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "InventarioProducto no encontrado id=" + idinventarioproducto
            );
        }
        repoInventarioProducto.deleteById(idinventarioproducto);
        // @SQLDelete en la entidad convierte esto en soft-delete (estado=0)
    }

    private InventarioProductosDTO toDTO(InventarioProducto ip) {
        InventarioProductosDTO dto = new InventarioProductosDTO();
        dto.setIdinventarioproducto(ip.getIdinventarioproducto());
        dto.setStockactual(ip.getStockactual());
        dto.setFechaingreso(ip.getFechaingreso());
        dto.setIdproducto(ip.getProducto().getIdproducto());
        dto.setIdinventario(ip.getInventario().getIdinventario());
        return dto;
    }
}
