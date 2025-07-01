// src/main/java/com/gadbacorp/api/service/jpa/inventario/InventarioService.java
package com.gadbacorp.api.service.jpa.inventario;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.gadbacorp.api.entity.inventario.Inventario;
import com.gadbacorp.api.repository.inventario.InventarioProductoRepository;
import com.gadbacorp.api.repository.inventario.InventarioRepository;
import com.gadbacorp.api.service.inventario.IInventarioService;

@Service
public class InventarioService implements IInventarioService {

    @Autowired
    private InventarioRepository repoInventario;

    @Autowired
    private InventarioProductoRepository repoInvProd;

    @Override
    public List<Inventario> buscarTodos() {
        return repoInventario.findAll();
    }

    @Override
    public Optional<Inventario> buscarId(Integer id) {
        return repoInventario.findById(id);
    }

    @Override
    public Inventario guardar(Inventario inventario) {
        boolean existe = repoInventario
            .existsByAlmacen_IdalmacenAndEstado(
                inventario.getAlmacen().getIdalmacen(), 
                1
            );
        if (existe) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Ya existe un inventario para el almacén seleccionado."
            );
        }
        return repoInventario.save(inventario);
    }

    @Override
    public Inventario modificar(Inventario inventario) {
        // 1) Compruebo asociación en inventario_producto
        boolean tieneProductos = repoInvProd.existsByInventarioIdinventario(
            inventario.getIdinventario()
        );
        if (tieneProductos) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "No se puede modificar: este inventario tiene productos asociados."
            );
        }
        // 2) Si no, guardo la modificación
        return repoInventario.save(inventario);
    }


    
    @Override
    public void eliminar(Integer id) {
        // validamos antes de borrar
        if (repoInvProd.existsByInventario_Idinventario(id)) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "No se puede eliminar: el inventario tiene productos asociados"
            );
        }
        repoInventario.deleteById(id);
    }
}
