package com.gadbacorp.api.controller.inventario;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.gadbacorp.api.entity.inventario.AlmacenProducto;
import com.gadbacorp.api.entity.inventario.AlmacenProductosDTO;
import com.gadbacorp.api.entity.inventario.Almacenes;
import com.gadbacorp.api.entity.inventario.Categorias;
import com.gadbacorp.api.entity.inventario.Productos;
import com.gadbacorp.api.entity.inventario.ProductosDTO;
import com.gadbacorp.api.entity.inventario.TipoProducto;
import com.gadbacorp.api.entity.inventario.UnidadDeMedida;
import com.gadbacorp.api.repository.inventario.AlmacenProductoRepository;
import com.gadbacorp.api.repository.inventario.AlmacenesRepository;
import com.gadbacorp.api.repository.inventario.CategoriasRepository;
import com.gadbacorp.api.repository.inventario.TipoProductoRepository;
import com.gadbacorp.api.repository.inventario.UnidadDeMedidaRepository;
import com.gadbacorp.api.service.inventario.IProductosService;

@RestController
@RequestMapping("/api/minimarket/productos")
public class ProductosController {
    @Autowired
    private IProductosService serviceProductos;

    @Autowired
    private CategoriasRepository repoCategorias;

    @Autowired
    private UnidadDeMedidaRepository repoUnidadDeMedida;

    @Autowired
    private TipoProductoRepository repoTipoProducto;

    @Autowired
    private AlmacenesRepository repoAlmacenes;

    @Autowired
    private AlmacenProductoRepository repoAlmacenProducto;

    // Map DTO to Entity
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

        // Validate and set relations
        Categorias categoria = repoCategorias.findById(dto.getIdcategoria())
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Categoría no encontrada con id=" + dto.getIdcategoria()));
        UnidadDeMedida unidad = repoUnidadDeMedida.findById(dto.getIdunidadmedida())
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Unidad no encontrada con id=" + dto.getIdunidadmedida()));
        TipoProducto tipo = repoTipoProducto.findById(dto.getIdtipoproducto())
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Tipo no encontrado con id=" + dto.getIdtipoproducto()));

        producto.setCategoria(categoria);
        producto.setUnidadMedida(unidad);
        producto.setTipoProducto(tipo);

        return producto;
    }

    // Map Entity to DTO
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
        dto.setIdcategoria(p.getCategoria().getIdcategoria());
        dto.setIdunidadmedida(p.getUnidadMedida().getIdunidadmedida());
        dto.setIdtipoproducto(p.getTipoProducto().getIdtipoproducto());

        dto.setAlmacenes(
            p.getAlmacenProductos().stream()
             .map(ap -> new AlmacenProductosDTO(
                 ap.getAlmacen().getIdalmacen(),
                 ap.getStock(),
                 ap.getFechaIngreso()))
             .collect(Collectors.toList())
        );
        return dto;
    }

    @GetMapping
    public ResponseEntity<List<ProductosDTO>> listar() {
        List<ProductosDTO> lista = serviceProductos.buscarTodos().stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductosDTO> obtener(@PathVariable Integer id) {
        Optional<Productos> opt = serviceProductos.buscarId(id);
        return opt
            .map(this::toDTO)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ProductosDTO> crear(@RequestBody ProductosDTO dto) {
        Productos prod = toEntity(dto);
        // Map almacenProductos
        if (dto.getAlmacenes() != null) {
            dto.getAlmacenes().forEach(apDto -> {
                AlmacenProducto ap = new AlmacenProducto();
                Almacenes alm = repoAlmacenes.findById(apDto.getIdalmacen())
                  .orElseThrow(() -> new ResponseStatusException(
                      HttpStatus.BAD_REQUEST, "Almacén no encontrado id=" + apDto.getIdalmacen()));
                ap.setAlmacen(alm);
                ap.setStock(apDto.getStock());
                ap.setFechaIngreso(apDto.getFechaIngreso());
                ap.setProducto(prod);
                prod.getAlmacenProductos().add(ap);
            });
        }
        Productos creado = serviceProductos.guardar(prod);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(creado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductosDTO> actualizar(
        @PathVariable Integer id,
        @RequestBody ProductosDTO dto) {

        Productos existing = serviceProductos.buscarId(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Producto no encontrado id=" + id));
        existing.setNombre(dto.getNombre());
        existing.setDescripcion(dto.getDescripcion());
        existing.setFechaVencimiento(dto.getFechaVencimiento());
        existing.setTipoImpuesto(dto.getTipoImpuesto());
        existing.setCostoCompra(dto.getCostoCompra());
        existing.setCostoVenta(dto.getCostoVenta());
        existing.setCostoMayor(dto.getCostoMayor());

        existing.setCategoria(repoCategorias.findById(dto.getIdcategoria())
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Categoría no encontrada id=" + dto.getIdcategoria())));
        existing.setUnidadMedida(repoUnidadDeMedida.findById(dto.getIdunidadmedida())
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Unidad no encontrada id=" + dto.getIdunidadmedida())));
        existing.setTipoProducto(repoTipoProducto.findById(dto.getIdtipoproducto())
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Tipo no encontrado id=" + dto.getIdtipoproducto())));

        // Merge almacenProductos
        Map<Integer, AlmacenProducto> oldMap = existing.getAlmacenProductos().stream()
            .collect(Collectors.toMap(a -> a.getAlmacen().getIdalmacen(), a -> a));
        List<AlmacenProducto> merged = new ArrayList<>();
        if (dto.getAlmacenes() != null) {
            dto.getAlmacenes().forEach(apDto -> {
                AlmacenProducto ap = oldMap.remove(apDto.getIdalmacen());
                if (ap != null) {
                    ap.setStock(apDto.getStock());
                    ap.setFechaIngreso(apDto.getFechaIngreso());
                } else {
                    ap = new AlmacenProducto();
                    Almacenes alm = repoAlmacenes.findById(apDto.getIdalmacen())
                      .orElseThrow(() -> new ResponseStatusException(
                          HttpStatus.BAD_REQUEST, "Almacén no encontrado id=" + apDto.getIdalmacen()));
                    ap.setAlmacen(alm);
                    ap.setStock(apDto.getStock());
                    ap.setFechaIngreso(apDto.getFechaIngreso());
                    ap.setProducto(existing);
                }
                merged.add(ap);
            });
        }
        existing.getAlmacenProductos().clear();
        existing.getAlmacenProductos().addAll(merged);

        Productos updated = serviceProductos.modificar(existing);
        return ResponseEntity.ok(toDTO(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Integer id) {
        serviceProductos.eliminar(id);
        return ResponseEntity.ok("Producto eliminada");
    }
}
