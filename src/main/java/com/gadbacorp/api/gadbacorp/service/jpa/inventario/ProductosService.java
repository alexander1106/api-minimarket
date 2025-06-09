package com.gadbacorp.api.gadbacorp.service.jpa.inventario;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.gadbacorp.api.gadbacorp.entity.inventario.AlmacenProducto;
import com.gadbacorp.api.gadbacorp.entity.inventario.AlmacenProductosDTO;
import com.gadbacorp.api.gadbacorp.entity.inventario.Productos;
import com.gadbacorp.api.gadbacorp.entity.inventario.ProductosDTO;
import com.gadbacorp.api.gadbacorp.repository.inventario.AlmacenesRepository;
import com.gadbacorp.api.gadbacorp.repository.inventario.CategoriasRepository;
import com.gadbacorp.api.gadbacorp.repository.inventario.ProductosRepository;
import com.gadbacorp.api.gadbacorp.repository.inventario.TipoProductoRepository;
import com.gadbacorp.api.gadbacorp.repository.inventario.UnidadDeMedidaRepository;
import com.gadbacorp.api.gadbacorp.service.inventario.IProductosService;

@Service
public class ProductosService implements IProductosService {

    @Autowired
    private ProductosRepository repoProductos;

    @Autowired
    private CategoriasRepository repoCategorias;

    @Autowired
    private UnidadDeMedidaRepository repoUnidadDeMedida;

    @Autowired
    private TipoProductoRepository repoTipoProducto;

    @Autowired
    private AlmacenesRepository repoAlmacenes;

    @Override
    public List<Productos> buscarTodos() {
        return repoProductos.findAll();
    }

    @Override
    public Optional<Productos> buscarId(Integer id) {
        return repoProductos.findById(id);
    }

    @Override
    public Productos guardar(Productos producto) {
        return repoProductos.save(producto);
    }

    @Override
    public Productos modificar(Productos producto) {
        return repoProductos.save(producto);
    }

    @Override
    public void eliminar(Integer id) {
        repoProductos.deleteById(id);
    }

    public List<ProductosDTO> buscarTodosDTO() {
        return repoProductos.findAll().stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    public Optional<ProductosDTO> buscarIdDTO(Integer id) {
        return repoProductos.findById(id).map(this::toDTO);
    }

    public ProductosDTO guardarDTO(ProductosDTO dto) {
        Optional<Productos> existente = repoProductos.findByNombreIgnoreCase(dto.getNombre());
        if (existente.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ya existe un producto con ese nombre.");
        }
        Productos producto = toEntity(dto);
        Productos creado = repoProductos.save(producto);
        return toDTO(creado);
    }

    public ProductosDTO actualizarDTO(Integer id, ProductosDTO dto) {
        Productos producto = repoProductos.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Producto no encontrado id=" + id));

        // Validación de nombre único
        Optional<Productos> existente = repoProductos.findByNombreIgnoreCase(dto.getNombre());
        if (existente.isPresent() && !existente.get().getIdproducto().equals(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Ya existe un producto con ese nombre.");
        }

        // Actualizar campos básicos
        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setFechaVencimiento(dto.getFechaVencimiento());
        producto.setTipoImpuesto(dto.getTipoImpuesto());
        producto.setCostoCompra(dto.getCostoCompra());
        producto.setCostoVenta(dto.getCostoVenta());
        producto.setCostoMayor(dto.getCostoMayor());
        
        // Solo actualizar imagen si se proporcionó
        if (dto.getImagen() != null && !dto.getImagen().isBlank()) {
            producto.setImagen(dto.getImagen());
        }

        // Asignar relaciones
        producto.setCategoria(repoCategorias.findById(dto.getIdcategoria())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Categoría no encontrada id=" + dto.getIdcategoria())));
        producto.setUnidadMedida(repoUnidadDeMedida.findById(dto.getIdunidadmedida())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Unidad no encontrada id=" + dto.getIdunidadmedida())));
        producto.setTipoProducto(repoTipoProducto.findById(dto.getIdtipoproducto())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Tipo no encontrado id=" + dto.getIdtipoproducto())));

        // Sincronizar stocks solo si la lista se envía con elementos
        if (dto.getAlmacenes() != null && !dto.getAlmacenes().isEmpty()) {
            Map<Integer, AlmacenProducto> actual = producto.getAlmacenProductos().stream()
                .collect(Collectors.toMap(ap -> ap.getAlmacen().getIdalmacen(), ap -> ap));

            List<AlmacenProducto> nuevos = new ArrayList<>();
            for (AlmacenProductosDTO apDto : dto.getAlmacenes()) {
                AlmacenProducto ap = actual.remove(apDto.getIdAlmacen());
                if (ap == null) {
                    ap = new AlmacenProducto();
                    ap.setProducto(producto);
                    ap.setAlmacen(repoAlmacenes.findById(apDto.getIdAlmacen())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                             "Almacén no encontrado id=" + apDto.getIdAlmacen())));
                }
                ap.setStock(apDto.getStock());
                ap.setFechaIngreso(apDto.getFechaIngreso());
                nuevos.add(ap);
            }
            producto.getAlmacenProductos().clear();
            producto.getAlmacenProductos().addAll(nuevos);
        }

        Productos guardado = repoProductos.save(producto);
        return toDTO(guardado);
    }

    private Productos toEntity(ProductosDTO dto) {
        Productos producto = new Productos();
        if (dto.getIdproducto() != null) {
            producto.setIdproducto(dto.getIdproducto());
        }
        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setFechaVencimiento(dto.getFechaVencimiento());
        producto.setTipoImpuesto(dto.getTipoImpuesto());
        producto.setCostoCompra(dto.getCostoCompra());
        producto.setCostoVenta(dto.getCostoVenta());
        producto.setCostoMayor(dto.getCostoMayor());
        producto.setImagen(dto.getImagen());

        producto.setCategoria(repoCategorias.findById(dto.getIdcategoria())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Categoría no encontrada id=" + dto.getIdcategoria())));
        producto.setUnidadMedida(repoUnidadDeMedida.findById(dto.getIdunidadmedida())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Unidad no encontrada id=" + dto.getIdunidadmedida())));
        producto.setTipoProducto(repoTipoProducto.findById(dto.getIdtipoproducto())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Tipo no encontrado id=" + dto.getIdtipoproducto())));

        if (dto.getAlmacenes() != null && !dto.getAlmacenes().isEmpty()) {
            for (AlmacenProductosDTO apDto : dto.getAlmacenes()) {
                AlmacenProducto ap = new AlmacenProducto();
                ap.setProducto(producto);
                ap.setStock(apDto.getStock());
                ap.setFechaIngreso(apDto.getFechaIngreso());
                ap.setAlmacen(repoAlmacenes.findById(apDto.getIdAlmacen())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                         "Almacén no encontrado id=" + apDto.getIdAlmacen())));
                producto.getAlmacenProductos().add(ap);
            }
        }
        return producto;
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

        dto.setAlmacenes(p.getAlmacenProductos().stream()
            .map(ap -> new AlmacenProductosDTO(
                ap.getIdalmacenproducto(),
                ap.getStock(),
                ap.getFechaIngreso(),
                ap.getEstado(),
                ap.getProducto().getIdproducto(),
                ap.getAlmacen().getIdalmacen()
            ))
            .collect(Collectors.toList()));
        return dto;
    }
}
