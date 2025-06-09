package com.gadbacorp.api.gadbacorp.service.jpa.inventario;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gadbacorp.api.gadbacorp.entity.inventario.AjusteInventario;
import com.gadbacorp.api.gadbacorp.entity.inventario.AjusteInventarioDTO;
import com.gadbacorp.api.gadbacorp.entity.inventario.AlmacenProducto;
import com.gadbacorp.api.gadbacorp.entity.inventario.Inventario;
import com.gadbacorp.api.gadbacorp.repository.inventario.AjusteInventarioRepository;
import com.gadbacorp.api.gadbacorp.repository.inventario.AlmacenProductoRepository;
import com.gadbacorp.api.gadbacorp.repository.inventario.InventarioRepository;
import com.gadbacorp.api.gadbacorp.service.inventario.IAjusteInventarioService;

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

        Inventario inv = existing.getInventario();
        AlmacenProducto ap = repoAlmProd.findByProductoAndAlmacen(inv.getProducto(), inv.getAlmacen())
            .orElseThrow(() -> new IllegalArgumentException(
                "Registro almacen_producto no encontrado"));

        // Revertir el ajuste anterior
        int stockSinAjuste = inv.getStock() - existing.getCantidad();

        // Aplicar el nuevo ajuste
        int nuevoStock = stockSinAjuste + dto.getCantidad();
        if (nuevoStock < 0) {
            throw new IllegalArgumentException(
                "No hay stock suficiente: stock actual=" + inv.getStock() + ", ajuste corregido resultaría en negativo");
        }

        inv.setStock(nuevoStock);
        repoInventario.save(inv);

        ap.setStock(nuevoStock);
        repoAlmProd.save(ap);

        existing.setCantidad(dto.getCantidad());
        existing.setDescripcion(dto.getDescripcion());
        existing.setFechaAjuste(dto.getFechaAjuste() != null ? dto.getFechaAjuste() : existing.getFechaAjuste());

        return repoAjuste.save(existing);
    }
}


