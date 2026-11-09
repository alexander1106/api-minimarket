package com.gadbacorp.api.service.jpa.inventario;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Service;

import com.gadbacorp.api.entity.inventario.ProductoPresentacion;
import com.gadbacorp.api.entity.inventario.Productos;
import com.gadbacorp.api.entity.inventario.UnidadDeMedida;
import com.gadbacorp.api.repository.inventario.ProductoPresentacionRepository;

/**
 * Convierte lo que se vende o compra (por ejemplo "2 sacos" o "2.5 kg") a la unidad base del producto,
 * que es en la que se guarda el stock. Sin presentacion, la cantidad ya esta en unidad base.
 */
@Service
public class ConversionUnidadesService {

    public static final int DECIMALES = 3;

    private final ProductoPresentacionRepository presentaciones;

    public ConversionUnidadesService(ProductoPresentacionRepository presentaciones) {
        this.presentaciones = presentaciones;
    }

    /**
     * @param idPresentacion presentacion en la que se expresa la cantidad; null = unidad base
     * @return la cantidad en unidad base (con 3 decimales)
     * @throws IllegalArgumentException cantidad no positiva, con mas de 3 decimales, presentacion de otro producto
     *         o fraccion en un producto que se vende por unidades enteras
     */
    public BigDecimal aBase(Productos producto, Integer idPresentacion, BigDecimal cantidad) {
        return convertir(producto, idPresentacion, cantidad).cantidadBase();
    }

    /** Resultado de convertir: la cantidad en unidad base y la presentacion usada (null = unidad base). */
    public record Conversion(BigDecimal cantidadBase, ProductoPresentacion presentacion) {
    }

    public Conversion convertir(Productos producto, Integer idPresentacion, BigDecimal cantidad) {
        // todo producto que entra a una venta, cotizacion o compra debe ser del catalogo de la empresa del usuario
        com.gadbacorp.api.tenant.TenantService.exigir(com.gadbacorp.api.tenant.TenantService.empresaDe(producto.getEmpresa()));
        if (cantidad == null || cantidad.signum() <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0.");
        }
        if (cantidad.stripTrailingZeros().scale() > DECIMALES) {
            throw new IllegalArgumentException("La cantidad admite como máximo " + DECIMALES + " decimales.");
        }

        BigDecimal factor = BigDecimal.ONE;
        ProductoPresentacion presentacion = null;
        if (idPresentacion != null) {
            presentacion = presentaciones
                    .findByIdPresentacionAndProductoIdproducto(idPresentacion, producto.getIdproducto())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "La presentación " + idPresentacion + " no existe para el producto " + nombre(producto) + "."));
            factor = presentacion.getFactor();
        }

        BigDecimal base = cantidad.multiply(factor).setScale(DECIMALES, RoundingMode.HALF_UP);
        if (!admiteDecimales(producto) && base.stripTrailingZeros().scale() > 0) {
            throw new IllegalArgumentException("El producto " + nombre(producto)
                    + " se maneja por unidades enteras: la cantidad resultante (" + base.stripTrailingZeros().toPlainString()
                    + ") no puede tener decimales.");
        }
        return new Conversion(base, presentacion);
    }

    /**
     * Para movimientos que ya vienen en unidad base (ajustes, traslados, ingreso de stock): comprueba que la cantidad
     * tenga como maximo 3 decimales y que solo tenga fraccion si la unidad del producto lo admite.
     */
    public static void validarFraccion(Productos producto, BigDecimal cantidad) {
        if (cantidad == null) {
            throw new IllegalArgumentException("La cantidad es obligatoria.");
        }
        int decimales = cantidad.stripTrailingZeros().scale();
        if (decimales > DECIMALES) {
            throw new IllegalArgumentException("La cantidad admite como máximo " + DECIMALES + " decimales.");
        }
        if (decimales > 0 && producto != null && !admiteDecimales(producto)) {
            throw new IllegalArgumentException("El producto " + nombre(producto)
                    + " se maneja por unidades enteras: la cantidad no puede tener decimales.");
        }
    }

    /** Un producto sin unidad de medida asignada se trata como unidad entera. */
    public static boolean admiteDecimales(Productos producto) {
        UnidadDeMedida u = producto.getUnidadMedida();
        return u != null && Boolean.TRUE.equals(u.getPermiteDecimales());
    }

    private static String nombre(Productos p) {
        return p.getNombre() != null ? p.getNombre() : "#" + p.getIdproducto();
    }
}
