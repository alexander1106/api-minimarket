package com.gadbacorp.api.service.jpa.inventario;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.gadbacorp.api.entity.inventario.AjusteInventario;
import com.gadbacorp.api.entity.inventario.AjusteInventarioDTO;
import com.gadbacorp.api.entity.inventario.InventarioProducto;
import com.gadbacorp.api.repository.inventario.AjusteInventarioRepository;
import com.gadbacorp.api.repository.inventario.InventarioProductoRepository;
import com.gadbacorp.api.service.inventario.IAjusteInventarioService;

import jakarta.transaction.Transactional;

@Service
public class AjusteInventarioService implements IAjusteInventarioService {

    @Autowired
    private AjusteInventarioRepository repoAjuste;

    @Autowired
    private InventarioProductoRepository repoInventarioProducto;

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
        if (!repoAjuste.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                "AjusteInventario no encontrado id=" + id);
        }
        repoAjuste.deleteById(id);
    }

    @Override
    @Transactional
    public AjusteInventario ajustarStock(AjusteInventarioDTO dto) {
        // 1. Buscar InventarioProducto
        InventarioProducto invProd = repoInventarioProducto.findById(dto.getIdinventarioproducto())
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "InventarioProducto no encontrado id=" + dto.getIdinventarioproducto()));

        // 2. Calcular nuevo stock y validar
        int cantidad = dto.getCantidad();
        int nuevoStock = invProd.getStockactual() + cantidad;
        if (nuevoStock < 0) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "No hay stock suficiente: stock actual=" + invProd.getStockactual() + ", ajuste=" + cantidad);
        }

        // 3. Actualizar stock en InventarioProducto
        invProd.setStockactual(nuevoStock);
        repoInventarioProducto.save(invProd);

        // 4. Crear y guardar el ajuste
        AjusteInventario aj = new AjusteInventario();
        aj.setCantidad(cantidad);
        aj.setDescripcion(dto.getDescripcion());
        aj.setFechaAjuste(dto.getFechaAjuste() != null ? dto.getFechaAjuste() : LocalDateTime.now());
        aj.setInventarioProducto(invProd);

        return repoAjuste.save(aj);
    }

    @Override
    @Transactional
    public AjusteInventario modificarAjuste(AjusteInventarioDTO dto) {
        // 1. Buscar Ajuste existente
        AjusteInventario existing = repoAjuste.findById(dto.getIdajusteinventario())
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "AjusteInventario no encontrado id=" + dto.getIdajusteinventario()));

        // 2. Obtener InventarioProducto asociado
        InventarioProducto invProd = existing.getInventarioProducto();

        // 3. Revertir el ajuste anterior
        int stockSinAjuste = invProd.getStockactual() - existing.getCantidad();

        // 4. Calcular nuevo stock con el ajuste modificado
        int nuevoStock = stockSinAjuste + dto.getCantidad();
        if (nuevoStock < 0) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "No hay stock suficiente: stock actual=" + invProd.getStockactual()
                    + ", ajuste corregido resultaría en negativo");
        }

        // 5. Actualizar stock en InventarioProducto
        invProd.setStockactual(nuevoStock);
        repoInventarioProducto.save(invProd);

        // 6. Actualizar campos del ajuste existente
        existing.setCantidad(dto.getCantidad());
        existing.setDescripcion(dto.getDescripcion());
        existing.setFechaAjuste(dto.getFechaAjuste() != null ? dto.getFechaAjuste() : existing.getFechaAjuste());

        return repoAjuste.save(existing);
    }
}
