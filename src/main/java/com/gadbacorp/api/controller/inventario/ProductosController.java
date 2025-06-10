package com.gadbacorp.api.controller.inventario;

import java.util.List;
import java.util.Map;
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

import com.gadbacorp.api.entity.inventario.InventarioProducto;
import com.gadbacorp.api.entity.inventario.InventarioProductosDTO;
import com.gadbacorp.api.entity.inventario.Productos;
import com.gadbacorp.api.entity.inventario.ProductosDTO;
import com.gadbacorp.api.repository.inventario.CategoriasRepository;
import com.gadbacorp.api.repository.inventario.InventarioRepository;
import com.gadbacorp.api.repository.inventario.ProductosRepository;
import com.gadbacorp.api.repository.inventario.TipoProductoRepository;
import com.gadbacorp.api.repository.inventario.UnidadDeMedidaRepository;

@RestController
@RequestMapping("/api/minimarket")
public class ProductosController {

    @Autowired private ProductosRepository productosRepo;
    @Autowired private CategoriasRepository categoriasRepo;
    @Autowired private UnidadDeMedidaRepository unidadRepo;
    @Autowired private TipoProductoRepository tipoRepo;
    @Autowired private InventarioRepository inventarioRepo;

    @GetMapping("/productos")
    public List<ProductosDTO> listar() {
        return productosRepo.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @GetMapping("/productos/{id}")
    public ProductosDTO obtener(@PathVariable Integer id) {
        return productosRepo.findById(id).map(this::toDTO)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado id=" + id));
    }

    @PostMapping("/productos")
    public ResponseEntity<ProductosDTO> crear(@RequestBody ProductosDTO dto) {
        Productos nuevo = toEntity(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(productosRepo.save(nuevo)));
    }

    @PutMapping("/productos")
    public ProductosDTO actualizar(@RequestBody ProductosDTO dto) {
        Productos producto = productosRepo.findById(dto.getIdproducto())
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Producto no encontrado id=" + dto.getIdproducto()));

        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setFechaVencimiento(dto.getFechaVencimiento());
        producto.setTipoImpuesto(dto.getTipoImpuesto());
        producto.setCostoCompra(dto.getCostoCompra());
        producto.setCostoVenta(dto.getCostoVenta());
        producto.setCostoMayor(dto.getCostoMayor());

        if (dto.getImagen() != null && !dto.getImagen().isBlank()) {
            producto.setImagen(dto.getImagen());
        }

        producto.setCategoria(categoriasRepo.findById(dto.getIdcategoria())
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Categoría no encontrada id=" + dto.getIdcategoria())));
        producto.setUnidadMedida(unidadRepo.findById(dto.getIdunidadmedida())
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Unidad no encontrada id=" + dto.getIdunidadmedida())));
        producto.setTipoProducto(tipoRepo.findById(dto.getIdtipoproducto())
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Tipo no encontrado id=" + dto.getIdtipoproducto())));

        if (dto.getInventarioProducto() != null && !dto.getInventarioProducto().isEmpty()) {
            Map<Integer, InventarioProducto> actuales = producto.getInventarioProductos().stream()
                .collect(Collectors.toMap(ip -> ip.getInventario().getIdinventario(), ip -> ip));

            producto.getInventarioProductos().clear();

            for (InventarioProductosDTO ipDto : dto.getInventarioProducto()) {
                InventarioProducto ip = actuales.remove(ipDto.getIdinventario());
                if (ip == null) {
                    ip = new InventarioProducto();
                    ip.setProducto(producto);
                    ip.setInventario(inventarioRepo.findById(ipDto.getIdinventario())
                        .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.BAD_REQUEST, "Inventario no encontrado id=" + ipDto.getIdinventario())));
                }
                ip.setStockactual(ipDto.getStockactual());
                ip.setFechaingreso(ipDto.getFechaingreso());
                producto.getInventarioProductos().add(ip);
            }
        }

        return toDTO(productosRepo.save(producto));
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
            p.getInventarioProductos().stream().map(ip -> new InventarioProductosDTO(
                ip.getIdinventarioproducto(),
                ip.getStockactual(),
                ip.getFechaingreso(),
                ip.getProducto().getIdproducto(),
                ip.getInventario().getIdinventario()
            )).collect(Collectors.toList())
        );
        return dto;
    }

    private Productos toEntity(ProductosDTO dto) {
        Productos p = new Productos();
        if (dto.getIdproducto() != null) p.setIdproducto(dto.getIdproducto());

        p.setNombre(dto.getNombre());
        p.setDescripcion(dto.getDescripcion());
        p.setFechaVencimiento(dto.getFechaVencimiento());
        p.setTipoImpuesto(dto.getTipoImpuesto());
        p.setCostoCompra(dto.getCostoCompra());
        p.setCostoVenta(dto.getCostoVenta());
        p.setCostoMayor(dto.getCostoMayor());
        p.setImagen(dto.getImagen());

        p.setCategoria(categoriasRepo.findById(dto.getIdcategoria())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Categoría no encontrada id=" + dto.getIdcategoria())));
        p.setUnidadMedida(unidadRepo.findById(dto.getIdunidadmedida())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unidad no encontrada id=" + dto.getIdunidadmedida())));
        p.setTipoProducto(tipoRepo.findById(dto.getIdtipoproducto())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tipo no encontrado id=" + dto.getIdtipoproducto())));

        if (dto.getInventarioProducto() != null && !dto.getInventarioProducto().isEmpty()) {
            for (InventarioProductosDTO ipDto : dto.getInventarioProducto()) {
                InventarioProducto ip = new InventarioProducto();
                ip.setProducto(p);
                ip.setStockactual(ipDto.getStockactual());
                ip.setFechaingreso(ipDto.getFechaingreso());
                ip.setInventario(inventarioRepo.findById(ipDto.getIdinventario())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Inventario no encontrado id=" + ipDto.getIdinventario())));
                p.getInventarioProductos().add(ip);
            }
        }

        return p;
    }
    @DeleteMapping("/productos/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Integer id) {
        productosRepo.deleteById(id);
        return ResponseEntity.ok("Producto eliminado correctamente");
    }

}
