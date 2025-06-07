package com.gadbacorp.api.service.jpa.inventario;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gadbacorp.api.entity.inventario.Categorias;
import com.gadbacorp.api.entity.inventario.CategoriasDTO;
import com.gadbacorp.api.repository.inventario.CategoriasRepository;
import com.gadbacorp.api.service.inventario.ICategoriasService;

@Service
public class CategoriasService implements ICategoriasService {

    @Autowired
    private CategoriasRepository repoCategorias;

    @Override
    public List<Categorias> buscarTodos() {
        return repoCategorias.findAll();
    }

    @Override
    public Categorias guardar(Categorias categoria) {
        Optional<Categorias> existente = repoCategorias.findByNombreIgnoreCase(categoria.getNombre());
        if (existente.isPresent()) {
            throw new IllegalArgumentException("Ya existe una categoría con ese nombre.");
        }
        return repoCategorias.save(categoria);
    }

    @Override
    public Categorias modificar(Categorias categoria) {
        Optional<Categorias> existente = repoCategorias.findByNombreIgnoreCase(categoria.getNombre());
        if (existente.isPresent() && !existente.get().getIdcategoria().equals(categoria.getIdcategoria())) {
            throw new IllegalArgumentException("Ya existe una categoría con ese nombre.");
        }
        return repoCategorias.save(categoria);
    }

    @Override
    public Optional<Categorias> buscarId(Integer id) {
        return repoCategorias.findById(id);
    }

    @Override
    public void eliminar(Integer id) {
        repoCategorias.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoriasDTO> listarConProductos() {
        return repoCategorias.findAll()
            .stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    /**
     * Convierte entidad → DTO, mapeando sólo los IDs de los productos
     */
    private CategoriasDTO toDTO(Categorias c) {
        // Usamos el constructor que inicializa id, nombre e imagen
        CategoriasDTO dto = new CategoriasDTO(
            c.getIdcategoria(),
            c.getNombre(),
            c.getImagen()
        );

        // Mapeamos productos → sólo sus IDs
        List<Integer> idsProductos = c.getProductos()
                                      .stream()
                                      .map(p -> p.getIdproducto())
                                      .collect(Collectors.toList());
        dto.setProductos(idsProductos);

        return dto;
    }
}
