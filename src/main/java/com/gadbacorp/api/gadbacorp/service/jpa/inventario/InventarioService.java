package com.gadbacorp.api.gadbacorp.service.jpa.inventario;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.gadbacorp.api.gadbacorp.entity.inventario.AlmacenProducto;
import com.gadbacorp.api.gadbacorp.entity.inventario.Almacenes;
import com.gadbacorp.api.gadbacorp.entity.inventario.Inventario;
import com.gadbacorp.api.gadbacorp.entity.inventario.Productos;
import com.gadbacorp.api.gadbacorp.repository.inventario.AlmacenProductoRepository;
import com.gadbacorp.api.gadbacorp.repository.inventario.AlmacenesRepository;
import com.gadbacorp.api.gadbacorp.repository.inventario.InventarioRepository;
import com.gadbacorp.api.gadbacorp.repository.inventario.ProductosRepository;
import com.gadbacorp.api.gadbacorp.service.inventario.IInventarioService;

@Service
public class InventarioService implements IInventarioService{
    @Autowired
    private InventarioRepository repoInventario;

    @Autowired
    private ProductosRepository repoProductos;

    @Autowired
    private AlmacenesRepository repoAlmacenes;

    @Autowired
    private AlmacenProductoRepository repoAlmacenProducto;

    @Override
    public List<Inventario> buscarTodos() {
        return repoInventario.findAll();
    }

    @Override
    public Inventario guardar(Inventario inventario) {
        return repoInventario.save(inventario);
    }

    @Override
    public Inventario modificar(Inventario inventario) {
        return repoInventario.save(inventario);
    }

    @Override
    public Optional<Inventario> buscarId(Integer id) {
        return repoInventario.findById(id);
    }

    @Override
    public void eliminar(Integer id) {
        repoInventario.deleteById(id);
    }

    @Override
    public Inventario sincronizarStock(Integer idproducto, Integer idalmacen) {
        Productos producto = repoProductos.findById(idproducto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado id=" + idproducto));

        Almacenes almacen = repoAlmacenes.findById(idalmacen)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Almacén no encontrado id=" + idalmacen));

        AlmacenProducto ap = repoAlmacenProducto.findByProductoAndAlmacen(producto, almacen)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No hay stock para producto " + idproducto + " en almacén " + idalmacen));

        Optional<Inventario> optInv = repoInventario.findByProductoAndAlmacen(producto, almacen);

        Inventario inv = optInv.map(i -> {
            i.setStock(ap.getStock());
            return i;
        }).orElseGet(() -> new Inventario(producto, almacen, ap.getStock()));

        return repoInventario.save(inv);
    }


    public Inventario actualizarStock(Integer idinventario, Integer stock, Integer idproducto, Integer idalmacen) {
    Productos producto = repoProductos.findById(idproducto)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado id=" + idproducto));
    Almacenes almacen = repoAlmacenes.findById(idalmacen)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Almacén no encontrado id=" + idalmacen));
    AlmacenProducto ap = repoAlmacenProducto.findByProductoAndAlmacen(producto, almacen)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                "No hay stock para producto " + idproducto + " en almacén " + idalmacen));

    Inventario inventario = repoInventario.findById(idinventario)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Inventario no encontrado id=" + idinventario));

    inventario.setProducto(producto);
    inventario.setAlmacen(almacen);
    inventario.setStock(ap.getStock());
    return repoInventario.save(inventario);
    }
}