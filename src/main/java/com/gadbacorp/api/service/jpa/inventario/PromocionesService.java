package com.gadbacorp.api.service.jpa.inventario;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.gadbacorp.api.entity.inventario.Categorias;
import com.gadbacorp.api.entity.inventario.Productos;
import com.gadbacorp.api.entity.inventario.Promociones;
import com.gadbacorp.api.entity.inventario.PromocionesDTO;
import com.gadbacorp.api.repository.inventario.CategoriasRepository;
import com.gadbacorp.api.repository.inventario.ProductosRepository;
import com.gadbacorp.api.repository.inventario.PromocionesRepository;
import com.gadbacorp.api.service.inventario.IPromocionesService;

@Service
public class PromocionesService implements IPromocionesService {

    @Autowired
    private PromocionesRepository repoPromociones;

    @Autowired
    private ProductosRepository repoProductos;

    @Autowired
    private CategoriasRepository repoCategorias;

    @Override
    public List<PromocionesDTO> buscarTodosDTO() {
        return repoPromociones.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<PromocionesDTO> buscarIdDTO(Integer id) {
        return repoPromociones.findById(id).map(this::toDTO);
    }

    @Override
    public PromocionesDTO guardarDTO(PromocionesDTO dto) {
        Promociones promo = toEntity(dto);
        Promociones creado = repoPromociones.save(promo);
        return toDTO(creado);
    }

    @Override
    public PromocionesDTO actualizarDTO(Integer id, PromocionesDTO dto) {
        Promociones promo = repoPromociones.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Promoción no encontrada id=" + id));

        promo.setNombre(dto.getNombre());
        promo.setFechaInicio(dto.getFechaInicio());
        promo.setFechaFin(dto.getFechaFin());
        promo.setTipoDescuento(dto.getTipoDescuento());
        promo.setValor(dto.getValor());

        // Actualizar productos y categorías
        promo.getProductos().clear();
        promo.getCategorias().clear();

        if (dto.getProductos() != null) {
            for (Integer prodId : dto.getProductos()) {
                Productos prod = repoProductos.findById(prodId)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Producto no encontrado id=" + prodId));
                promo.getProductos().add(prod);
            }
        }

        if (dto.getCategorias() != null) {
            for (Integer catId : dto.getCategorias()) {
                Categorias cat = repoCategorias.findById(catId)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Categoría no encontrada id=" + catId));
                promo.getCategorias().add(cat);
            }
        }

        return toDTO(repoPromociones.save(promo));
    }

    @Override
    public void eliminar(Integer id) {
        repoPromociones.deleteById(id);
    }

    private PromocionesDTO toDTO(Promociones p) {
        PromocionesDTO dto = new PromocionesDTO();
        dto.setIdpromocion(p.getIdpromocion());
        dto.setNombre(p.getNombre());
        dto.setFechaInicio(p.getFechaInicio());
        dto.setFechaFin(p.getFechaFin());
        dto.setTipoDescuento(p.getTipoDescuento());
        dto.setValor(p.getValor());
        if (p.getProductos() != null) {
            dto.setProductos(p.getProductos().stream().map(Productos::getIdproducto).collect(Collectors.toList()));
        }
        if (p.getCategorias() != null) {
            dto.setCategorias(p.getCategorias().stream().map(Categorias::getIdcategoria).collect(Collectors.toList()));
        }
        return dto;
    }

    private Promociones toEntity(PromocionesDTO dto) {
        Promociones p = new Promociones();
        if (dto.getIdpromocion() != null) p.setIdpromocion(dto.getIdpromocion());
        p.setNombre(dto.getNombre());
        p.setFechaInicio(dto.getFechaInicio());
        p.setFechaFin(dto.getFechaFin());
        p.setTipoDescuento(dto.getTipoDescuento());
        p.setValor(dto.getValor());

        if (dto.getProductos() != null) {
            for (Integer idprod : dto.getProductos()) {
                Productos prod = repoProductos.findById(idprod)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Producto no encontrado id=" + idprod));
                p.getProductos().add(prod);
            }
        }

        if (dto.getCategorias() != null) {
            for (Integer idcat : dto.getCategorias()) {
                Categorias cat = repoCategorias.findById(idcat)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Categoría no encontrada id=" + idcat));
                p.getCategorias().add(cat);
            }
        }

        return p;
    }
}
