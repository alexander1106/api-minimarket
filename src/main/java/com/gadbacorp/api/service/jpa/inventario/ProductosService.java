package com.gadbacorp.api.service.jpa.inventario;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
import com.gadbacorp.api.entity.inventario.ProductosDTO;
import com.gadbacorp.api.repository.inventario.CategoriasRepository;
import com.gadbacorp.api.repository.inventario.InventarioRepository;
import com.gadbacorp.api.repository.inventario.ProductosRepository;
import com.gadbacorp.api.repository.inventario.TipoProductoRepository;
import com.gadbacorp.api.repository.inventario.UnidadDeMedidaRepository;
import com.gadbacorp.api.service.inventario.IProductosService;

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
    private InventarioRepository repoInventario;

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

        // Asignar relaciones a Categoría, UnidadDeMedida y TipoProducto
        producto.setCategoria(repoCategorias.findById(dto.getIdcategoria())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Categoría no encontrada id=" + dto.getIdcategoria())));
        producto.setUnidadMedida(repoUnidadDeMedida.findById(dto.getIdunidadmedida())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Unidad no encontrada id=" + dto.getIdunidadmedida())));
        producto.setTipoProducto(repoTipoProducto.findById(dto.getIdtipoproducto())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Tipo no encontrado id=" + dto.getIdtipoproducto())));

        // Sincronizar InventarioProducto si el DTO trae lista
        if (dto.getInventarioProducto() != null && !dto.getInventarioProducto().isEmpty()) {
            // 1. Mapear las líneas actuales por idInventario
            Map<Integer, InventarioProducto> actual = producto.getInventarioProductos().stream()
                .collect(Collectors.toMap(
                    ip -> ip.getInventario().getIdinventario(),  // clave = idInventario
                    ip -> ip                                    // valor = la propia entidad
                ));

            List<InventarioProducto> nuevos = new ArrayList<>();
            for (InventarioProductosDTO ipDto : dto.getInventarioProducto()) {
                // Obtener el ID de Inventario desde el DTO
                Integer idInv = ipDto.getIdinventario();

                // Verificar si ya existe una línea para este idInventario
                InventarioProducto ip = actual.remove(idInv);
                if (ip == null) {
                    // No existía → crear nueva línea
                    ip = new InventarioProducto();
                    ip.setProducto(producto);

                    Inventario inv = repoInventario.findById(idInv)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                             "Inventario no encontrado id=" + idInv));
                    ip.setInventario(inv);
                }

                // Actualizar campos de stock y fecha
                ip.setStockactual(ipDto.getStockactual());
                if (ipDto.getFechaingreso() != null) {
                    ip.setFechaingreso(ipDto.getFechaingreso());
                }

                nuevos.add(ip);
            }

            // 2. Reemplazar la lista actual de InventarioProducto
            producto.getInventarioProductos().clear();
            producto.getInventarioProductos().addAll(nuevos);
            // Con orphanRemoval = true, las líneas que quedaron en 'actual' se eliminarán
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

        if (dto.getInventarioProducto() != null && !dto.getInventarioProducto().isEmpty()) {
            for (InventarioProductosDTO ipDto : dto.getInventarioProducto()) {
                InventarioProducto ip = new InventarioProducto();
                ip.setProducto(producto);
                ip.setStockactual(ipDto.getStockactual());
                if (ipDto.getFechaingreso() != null) {
                    ip.setFechaingreso(ipDto.getFechaingreso());
                }
                Inventario inv = repoInventario.findById(ipDto.getIdinventario())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                         "Inventario no encontrado id=" + ipDto.getIdinventario()));
                ip.setInventario(inv);

                producto.getInventarioProductos().add(ip);
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

        dto.setInventarioProducto(
            p.getInventarioProductos()
             .stream()
             .map(ip -> new InventarioProductosDTO(
                 ip.getIdinventarioproducto(),
                 ip.getStockactual(),
                 ip.getFechaingreso(),
                 ip.getProducto().getIdproducto(),
                 ip.getInventario().getIdinventario()
             ))
             .collect(Collectors.toList())
        );

        return dto;
    }
}
