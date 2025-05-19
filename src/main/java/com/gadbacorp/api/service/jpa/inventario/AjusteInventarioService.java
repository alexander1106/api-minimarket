package com.gadbacorp.api.service.jpa.inventario;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gadbacorp.api.entity.inventario.AjusteInventario;
import com.gadbacorp.api.entity.inventario.AjusteInventarioDTO;
import com.gadbacorp.api.entity.inventario.AlmacenProducto;
import com.gadbacorp.api.entity.inventario.Inventario;
import com.gadbacorp.api.repository.inventario.AjusteInventarioRepository;
import com.gadbacorp.api.repository.inventario.AlmacenProductoRepository;
import com.gadbacorp.api.repository.inventario.InventarioRepository;
import com.gadbacorp.api.service.inventario.IAjusteInventarioService;

import jakarta.transaction.Transactional;

@Service
public class AjusteInventarioService implements IAjusteInventarioService {

    @Autowired
    private AjusteInventarioRepository repoAjuste;

    @Autowired
    private InventarioRepository repoInventario;

    @Autowired
    private AlmacenProductoRepository repoAlmProd;

    @Override
    public List<AjusteInventario> buscarTodos() {
        return repoAjuste.findAll();
    }

    @Override
    public Optional<AjusteInventario> buscarId(Integer id) {
        return repoAjuste.findById(id);
    }

    @Override
    @Transactional
    public AjusteInventario guardar(AjusteInventario ajuste) {
        return repoAjuste.save(ajuste);
    }

    @Override
    @Transactional
    public AjusteInventario modificar(AjusteInventario ajuste) {
        return repoAjuste.save(ajuste);
    }

    @Override
    public void eliminar(Integer id) {
        repoAjuste.deleteById(id);
    }

    @Transactional
    public AjusteInventario ajustarStock(AjusteInventarioDTO dto) {
        Inventario inv = repoInventario.findById(dto.getIdinventario())
            .orElseThrow(() -> new IllegalArgumentException(
                "Inventario no encontrado id=" + dto.getIdinventario()));

        int cantidad = dto.getCantidad();
        int nuevoStock = inv.getStock() + cantidad;
        if (nuevoStock < 0) {
            throw new IllegalArgumentException(
                "No hay stock suficiente: stock actual=" + inv.getStock() + ", ajuste=" + cantidad);
        }

        inv.setStock(nuevoStock);
        repoInventario.save(inv);

        AlmacenProducto ap = repoAlmProd.findByProductoAndAlmacen(inv.getProducto(), inv.getAlmacen())
            .orElseThrow(() -> new IllegalArgumentException(
                "Registro almacen_producto no encontrado"));
        ap.setStock(nuevoStock);
        repoAlmProd.save(ap);

        AjusteInventario aj = new AjusteInventario();
        aj.setCantidad(cantidad);
        aj.setDescripcion(dto.getDescripcion());
        aj.setFechaAjuste(dto.getFechaAjuste() != null ? dto.getFechaAjuste() : LocalDateTime.now());
        aj.setInventario(inv);
        return repoAjuste.save(aj);
    }

    @Transactional
    public AjusteInventario modificarAjuste(AjusteInventarioDTO dto) {
        AjusteInventario existing = repoAjuste.findById(dto.getIdajusteinventario())
            .orElseThrow(() -> new IllegalArgumentException(
                "Ajuste no encontrado id=" + dto.getIdajusteinventario()));

        int originalQty = existing.getCantidad();
        int newQty = dto.getCantidad();
        int delta = newQty - originalQty;

        Inventario inv = existing.getInventario();
        int stockResult = inv.getStock() + delta;
        if (stockResult < 0) {
            throw new IllegalArgumentException(
                "No hay stock suficiente: stock actual=" + inv.getStock() + ", delta=" + delta);
        }

        inv.setStock(stockResult);
        repoInventario.save(inv);

        AlmacenProducto ap = repoAlmProd.findByProductoAndAlmacen(inv.getProducto(), inv.getAlmacen())
            .orElseThrow(() -> new IllegalArgumentException(
                "Registro almacen_producto no encontrado"));
        ap.setStock(stockResult);
        repoAlmProd.save(ap);

        existing.setCantidad(newQty);
        existing.setDescripcion(dto.getDescripcion());
        existing.setFechaAjuste(dto.getFechaAjuste() != null ? dto.getFechaAjuste() : existing.getFechaAjuste());
        return repoAjuste.save(existing);
    }
}

