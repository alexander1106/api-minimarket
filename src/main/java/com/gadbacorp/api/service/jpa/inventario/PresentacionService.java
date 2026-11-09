package com.gadbacorp.api.service.jpa.inventario;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gadbacorp.api.entity.inventario.PresentacionDTO;
import com.gadbacorp.api.entity.inventario.ProductoPresentacion;
import com.gadbacorp.api.entity.inventario.Productos;
import com.gadbacorp.api.excepciones.RecursoDuplicadoException;
import com.gadbacorp.api.excepciones.RegistroInvalidoException;
import com.gadbacorp.api.repository.inventario.ProductoPresentacionRepository;
import com.gadbacorp.api.repository.inventario.ProductosRepository;

/** Alta, edicion y baja de las presentaciones de un producto (saco, caja, paquete...). */
@Service
public class PresentacionService {

    private final ProductoPresentacionRepository presentaciones;
    private final ProductosRepository productos;

    public PresentacionService(ProductoPresentacionRepository presentaciones, ProductosRepository productos) {
        this.presentaciones = presentaciones;
        this.productos = productos;
    }

    @Transactional(readOnly = true)
    public List<PresentacionDTO> listar(Integer idProducto) {
        Productos producto = producto(idProducto);
        return presentaciones.findByProductoIdproductoOrderByFactorAsc(idProducto).stream()
                .map(p -> aDto(p, producto)).toList();
    }

    @Transactional
    public PresentacionDTO crear(Integer idProducto, PresentacionDTO dto) {
        Productos producto = producto(idProducto);
        validar(dto, producto);
        String nombre = dto.getNombre().trim();
        if (presentaciones.existsByProductoIdproductoAndNombreIgnoreCase(idProducto, nombre)) {
            throw new RecursoDuplicadoException("El producto ya tiene una presentación llamada " + nombre + ".");
        }
        ProductoPresentacion p = new ProductoPresentacion();
        p.setProducto(producto);
        aplicar(p, dto);
        return aDto(presentaciones.save(p), producto);
    }

    @Transactional
    public PresentacionDTO actualizar(PresentacionDTO dto) {
        if (dto.getIdPresentacion() == null) {
            throw new RegistroInvalidoException(Map.of("idPresentacion", "El id de la presentación es obligatorio."));
        }
        ProductoPresentacion p = presentaciones.findById(dto.getIdPresentacion()).orElseThrow(
                () -> new NoSuchElementException("Presentación no encontrada con ID: " + dto.getIdPresentacion()));
        Productos producto = p.getProducto();
        validar(dto, producto);
        String nombre = dto.getNombre().trim();
        if (presentaciones.existsByProductoIdproductoAndNombreIgnoreCaseAndIdPresentacionNot(
                producto.getIdproducto(), nombre, p.getIdPresentacion())) {
            throw new RecursoDuplicadoException("El producto ya tiene una presentación llamada " + nombre + ".");
        }
        aplicar(p, dto);
        return aDto(presentaciones.save(p), producto);
    }

    @Transactional
    public void eliminar(Integer idPresentacion) {
        if (!presentaciones.existsById(idPresentacion)) {
            throw new NoSuchElementException("Presentación no encontrada con ID: " + idPresentacion);
        }
        presentaciones.deleteById(idPresentacion);
    }

    private Productos producto(Integer idProducto) {
        return productos.findById(idProducto)
                .orElseThrow(() -> new NoSuchElementException("Producto no encontrado con ID: " + idProducto));
    }

    static void validar(PresentacionDTO d, Productos producto) {
        Map<String, String> errores = new LinkedHashMap<>();
        if (d.getNombre() == null || d.getNombre().isBlank()) {
            errores.put("nombre", "El nombre de la presentación es obligatorio.");
        } else if (d.getNombre().trim().length() > 80) {
            errores.put("nombre", "El nombre no puede pasar de 80 caracteres.");
        }
        if (d.getFactor() == null || d.getFactor().signum() <= 0) {
            errores.put("factor", "El factor (unidades base que contiene) debe ser mayor a 0.");
        } else if (d.getFactor().stripTrailingZeros().scale() > ConversionUnidadesService.DECIMALES) {
            errores.put("factor", "El factor admite como máximo 3 decimales.");
        } else if (!ConversionUnidadesService.admiteDecimales(producto) && d.getFactor().stripTrailingZeros().scale() > 0) {
            errores.put("factor", "La unidad base de este producto es entera: el factor no puede tener decimales.");
        }
        if (d.getPrecioVenta() != null && d.getPrecioVenta().signum() < 0) {
            errores.put("precioVenta", "El precio de venta no puede ser negativo.");
        }
        if (d.getPrecioCompra() != null && d.getPrecioCompra().signum() < 0) {
            errores.put("precioCompra", "El precio de compra no puede ser negativo.");
        }
        if (d.getCodigoBarras() != null && d.getCodigoBarras().trim().length() > 40) {
            errores.put("codigoBarras", "El código de barras no puede pasar de 40 caracteres.");
        }
        if (!errores.isEmpty()) {
            throw new RegistroInvalidoException(errores);
        }
    }

    private static void aplicar(ProductoPresentacion p, PresentacionDTO d) {
        p.setNombre(d.getNombre().trim());
        p.setFactor(d.getFactor());
        p.setPrecioVenta(d.getPrecioVenta());
        p.setPrecioCompra(d.getPrecioCompra());
        p.setCodigoBarras(d.getCodigoBarras() == null || d.getCodigoBarras().isBlank() ? null : d.getCodigoBarras().trim());
    }

    static PresentacionDTO aDto(ProductoPresentacion p, Productos producto) {
        PresentacionDTO d = new PresentacionDTO();
        d.setIdPresentacion(p.getIdPresentacion());
        d.setIdProducto(producto.getIdproducto());
        d.setNombre(p.getNombre());
        d.setFactor(p.getFactor());
        d.setPrecioVenta(p.getPrecioVenta());
        d.setPrecioCompra(p.getPrecioCompra());
        d.setCodigoBarras(p.getCodigoBarras());
        String unidad = producto.getUnidadMedida() != null && producto.getUnidadMedida().getCodigo() != null
                ? producto.getUnidadMedida().getCodigo() : "UND";
        d.setUnidadBase(unidad);
        BigDecimal factor = p.getFactor() == null ? BigDecimal.ZERO : p.getFactor().stripTrailingZeros();
        d.setEquivalencia("1 " + p.getNombre() + " = " + factor.toPlainString() + " " + unidad);
        return d;
    }
}
