// ProductosService.java
package com.gadbacorp.api.service.jpa.inventario;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.gadbacorp.api.entity.inventario.InventarioProducto;
import com.gadbacorp.api.entity.inventario.InventarioProductosDTO;
import com.gadbacorp.api.entity.inventario.Productos;
import com.gadbacorp.api.entity.inventario.ProductosDTO;
import com.gadbacorp.api.repository.inventario.CategoriasRepository;
import com.gadbacorp.api.repository.inventario.InventarioProductoRepository;
import com.gadbacorp.api.repository.inventario.InventarioRepository;
import com.gadbacorp.api.repository.inventario.ProductosRepository;
import com.gadbacorp.api.repository.inventario.TipoProductoRepository;
import com.gadbacorp.api.repository.inventario.UnidadDeMedidaRepository;
import com.gadbacorp.api.service.inventario.IProductosService;

@Service
@Transactional
public class ProductosService implements IProductosService {

    @Autowired private ProductosRepository repoProd;
    @Autowired private CategoriasRepository repoCat;
    @Autowired private UnidadDeMedidaRepository repoUni;
    @Autowired private TipoProductoRepository repoTipo;
    @Autowired private InventarioProductoRepository repoInvProd;
    @Autowired private InventarioRepository repoInv;

    // === Implementación de IProductosService ===

    @Override
    public List<Productos> buscarTodos() {
        return repoProd.findAll();
    }

    @Override
    public Optional<Productos> buscarId(Integer id) {
        return repoProd.findById(id);
    }

    @Override
    public Productos guardar(Productos producto) {
        return repoProd.save(producto);
    }

    @Override
    public Productos modificar(Productos producto) {
        return repoProd.save(producto);
    }

    @Override
    public void eliminar(Integer id) {
        if (repoInvProd.existsByProducto_Idproducto(id)) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "No se puede eliminar: el producto está presente en inventario");
        }
        repoProd.deleteById(id);
    }

    @Override
    public List<Productos> listarProductosPorCategoria(Integer id) {
        return repoProd.findByCategoria_Idcategoria(id);
    }

    // === Métodos auxiliares para el controlador con DTOs ===

    public List<ProductosDTO> listarProductosDTO() {
        return buscarTodos().stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    public ProductosDTO obtenerProductoDTO(Integer id) {
        Productos p = buscarId(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Producto no encontrado id=" + id));
        return toDTO(p);
    }

    public List<ProductosDTO> listarProductosPorCategoriaDTO(Integer categoriaId) {
        return listarProductosPorCategoria(categoriaId).stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    public ProductosDTO crearProducto(ProductosDTO dto) {
        validarDTO(dto, false);
        Productos entidad = toEntity(dto);
        return toDTO(repoProd.save(entidad));
    }

    public ProductosDTO actualizarProducto(ProductosDTO dto) {
        if (dto.getIdproducto() == null) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "El campo 'idproducto' es obligatorio");
        }
        // Verificar existencia
        buscarId(dto.getIdproducto())
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Producto no encontrado id=" + dto.getIdproducto()));
        validarDTO(dto, true);
        Productos entidad = toEntity(dto);
        return toDTO(repoProd.save(entidad));
    }

    // === Validaciones comunes ===

    private void validarDTO(ProductosDTO dto, boolean isUpdate) {
        if (!isUpdate && dto.getIdproducto() != null) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "No debe enviar idproducto al crear");
        }
        if (dto.getNombre() == null || dto.getNombre().isBlank()) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "El campo 'nombre' es obligatorio");
        }
        if (dto.getFechaVencimiento() != null
            && dto.getFechaVencimiento().isBefore(LocalDate.now())) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "La fecha de vencimiento no puede ser anterior a hoy");
        }
        repoCat.findById(dto.getIdcategoria()).orElseThrow(() ->
            new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Categoría no encontrada id=" + dto.getIdcategoria()));
        repoUni.findById(dto.getIdunidadmedida()).orElseThrow(() ->
            new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Unidad de medida no encontrada id=" + dto.getIdunidadmedida()));
        repoTipo.findById(dto.getIdtipoproducto()).orElseThrow(() ->
            new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Tipo de producto no encontrado id=" + dto.getIdtipoproducto()));
    }

    // === Conversión DTO ↔ Entidad ===

    private Productos toEntity(ProductosDTO dto) {
        Productos p = new Productos();
        if (dto.getIdproducto() != null) {
            p.setIdproducto(dto.getIdproducto());
        }
        p.setNombre(dto.getNombre());
        p.setDescripcion(dto.getDescripcion());
        p.setFechaVencimiento(dto.getFechaVencimiento());
        p.setTipoImpuesto(dto.getTipoImpuesto());
        p.setCostoCompra(dto.getCostoCompra());
        p.setCostoVenta(dto.getCostoVenta());
        p.setCostoMayor(dto.getCostoMayor());
        p.setImagen(dto.getImagen());
        p.setCategoria(repoCat.findById(dto.getIdcategoria()).get());
        p.setUnidadMedida(repoUni.findById(dto.getIdunidadmedida()).get());
        p.setTipoProducto(repoTipo.findById(dto.getIdtipoproducto()).get());

        p.getInventarioProductos().clear();
        if (dto.getInventarioProducto() != null) {
            dto.getInventarioProducto().forEach(ipDto -> {
                InventarioProducto ip = new InventarioProducto();
                ip.setProducto(p);
                ip.setInventario(repoInv.findById(ipDto.getIdinventario())
                    .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Inventario no encontrado id=" + ipDto.getIdinventario())));
                ip.setStockactual(ipDto.getStockactual());
                ip.setFechaingreso(ipDto.getFechaingreso());
                p.getInventarioProductos().add(ip);
            });
        }
        return p;
    }

    private ProductosDTO toDTO(Productos p) {
        ProductosDTO dto = new ProductosDTO();
        dto.setIdproducto(p.getIdproducto());
        dto.setNombre(p.getNombre());
        dto.setDescripcion(p.getDescripcion());
        dto.setFechaVencimiento(p.getFechaVencimiento());
        dto.setTipoImpuesto(p.getTipoImpuesto());
        dto.setCostoCompra(p.getCostoCompra());
        dto.setCostoVenta(p.getCostoVenta());
        dto.setCostoMayor(p.getCostoMayor());
        dto.setImagen(p.getImagen());
        dto.setIdcategoria(p.getCategoria().getIdcategoria());
        dto.setIdunidadmedida(p.getUnidadMedida().getIdunidadmedida());
        dto.setIdtipoproducto(p.getTipoProducto().getIdtipoproducto());

        dto.setInventarioProducto(
            p.getInventarioProductos()
             .stream()
             .map(ip -> new InventarioProductosDTO(
                 ip.getIdinventarioproducto(),
                 ip.getStockactual(),
                 ip.getFechaingreso(),
                 p.getIdproducto(),
                 ip.getInventario().getIdinventario()
             ))
             .collect(Collectors.toList())
        );
        return dto;
    }
    public void eliminarProducto(Integer id) {
    // Llama al método de la interfaz que ya elimina y valida inventario
    eliminar(id);
}
}
