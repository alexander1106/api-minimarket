package com.gadbacorp.api.tenant;

import java.lang.reflect.Method;
import java.util.Map;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.gadbacorp.api.entity.administrable.Empresas;
import com.gadbacorp.api.entity.administrable.Sucursales;
import com.gadbacorp.api.entity.caja.AperturaCaja;
import com.gadbacorp.api.entity.caja.Caja;
import com.gadbacorp.api.entity.compras.Compras;
import com.gadbacorp.api.entity.compras.DetallesCompras;
import com.gadbacorp.api.entity.compras.Proveedores;
import com.gadbacorp.api.entity.delivery.Delivery;
import com.gadbacorp.api.entity.empleados.Usuarios;
import com.gadbacorp.api.entity.inventario.Almacenes;
import com.gadbacorp.api.entity.inventario.Inventario;
import com.gadbacorp.api.entity.inventario.InventarioProducto;
import com.gadbacorp.api.entity.inventario.Productos;
import com.gadbacorp.api.entity.ventas.Clientes;
import com.gadbacorp.api.entity.ventas.Cotizaciones;
import com.gadbacorp.api.entity.ventas.DetallesCotizaciones;
import com.gadbacorp.api.entity.ventas.DetallesVentas;
import com.gadbacorp.api.entity.ventas.Devoluciones;
import com.gadbacorp.api.entity.ventas.MetodosPago;
import com.gadbacorp.api.entity.ventas.Pagos;
import com.gadbacorp.api.entity.ventas.Ventas;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 * Comprobaciones de empresa que necesitan la base de datos: de quien es el registro que pide la URL
 * ({@code /clientes/5}, {@code /almacenes/sucursal/2}) y a que sucursal o empresa apunta el cuerpo que se envia.
 */
@Component
public class TenantGuard {

    /** Segmento de la URL que antecede a un id, y el tipo de dato al que se refiere. */
    private static final Map<String, Class<?>> RECURSOS = Map.ofEntries(
            Map.entry("empresas", Empresas.class),
            Map.entry("sucursal", Sucursales.class),
            Map.entry("sucursales", Sucursales.class),
            Map.entry("usuarios", Usuarios.class),
            Map.entry("productos", Productos.class),
            Map.entry("proveedores", Proveedores.class),
            Map.entry("almacenes", Almacenes.class),
            Map.entry("inventario", Inventario.class),
            Map.entry("inventarioproducto", InventarioProducto.class),
            Map.entry("clientes", Clientes.class),
            Map.entry("metodos-pago", MetodosPago.class),
            Map.entry("ventas", Ventas.class),
            Map.entry("detalles-ventas", DetallesVentas.class),
            Map.entry("cotizaciones", Cotizaciones.class),
            Map.entry("detalles-cotizaciones", DetallesCotizaciones.class),
            Map.entry("devoluciones", Devoluciones.class),
            Map.entry("pagos", Pagos.class),
            Map.entry("compras", Compras.class),
            Map.entry("detalles-compras", DetallesCompras.class),
            Map.entry("cajas", Caja.class),
            Map.entry("aperturas-cajas", AperturaCaja.class),
            Map.entry("delivery", Delivery.class));

    @PersistenceContext
    private EntityManager em;

    private final TenantRules reglas;

    public TenantGuard(TenantRules reglas) {
        this.reglas = reglas;
    }

    /** Rechaza (404) la URL si apunta a un registro de otra empresa. Los ids que no existen se dejan pasar al controlador. */
    @Transactional(readOnly = true)
    public void validarRuta(String rutaSinPrefijo) {
        if (!TenantService.restringido()) {
            return;
        }
        String[] partes = rutaSinPrefijo.split("/");
        for (int i = 0; i + 1 < partes.length; i++) {
            Class<?> tipo = RECURSOS.get(partes[i]);
            Integer id = entero(partes[i + 1]);
            if (tipo != null && id != null) {
                Object registro = em.find(tipo, id);
                if (registro != null) {
                    TenantService.exigir(reglas.empresaDe(registro));
                }
            }
        }
    }

    /**
     * Valida un cuerpo recibido: no puede apuntar a una sucursal o empresa ajena, ni pisar un registro ajeno
     * (un PUT con el id de otra empresa). Lanza 403 si no se cumple.
     */
    @Transactional(readOnly = true)
    public void validarCuerpo(Object cuerpo) {
        if (cuerpo == null || !TenantService.restringido()) {
            return;
        }
        Integer actual = TenantService.empresaActual();

        Integer idSucursal = idSucursalDe(cuerpo);
        if (idSucursal != null) {
            Sucursales s = em.find(Sucursales.class, idSucursal);
            if (s != null && !actual.equals(TenantService.empresaDe(s))) {
                throw new AccessDeniedException("La sucursal pertenece a otra empresa");
            }
        }
        Integer idAlmacen = numero(cuerpo, "getIdalmacen");
        if (idAlmacen != null && !actual.equals(reglas.empresaDeAlmacenId(idAlmacen)) && reglas.empresaDeAlmacenId(idAlmacen) != null) {
            throw new AccessDeniedException("El almacen pertenece a otra empresa");
        }
        Integer idInventario = numero(cuerpo, "getIdinventario");
        if (idInventario != null && reglas.empresaDeInventarioId(idInventario) != null
                && !actual.equals(reglas.empresaDeInventarioId(idInventario))) {
            throw new AccessDeniedException("El inventario pertenece a otra empresa");
        }
        Integer idEmpresa = numero(cuerpo, "getIdEmpresa", "getIdempresa");
        if (idEmpresa != null && !actual.equals(idEmpresa)) {
            throw new AccessDeniedException("La empresa no es la suya");
        }
        if (em.getMetamodel().getManagedTypes().stream().anyMatch(t -> t.getJavaType() == cuerpo.getClass())) {
            Object id = em.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(cuerpo);
            if (id != null) {
                Object existente = em.find(cuerpo.getClass(), id);
                if (existente != null && reglas.protegido(existente)
                        && !actual.equals(reglas.empresaDe(existente))) {
                    throw new AccessDeniedException("El registro pertenece a otra empresa");
                }
            }
        }
    }

    private Integer idSucursalDe(Object cuerpo) {
        Integer directo = numero(cuerpo, "getIdSucursal", "getId_sucursal");
        if (directo != null) {
            return directo;
        }
        for (String getter : new String[] {"getSucursal", "getSucursales"}) {
            try {
                Method m = cuerpo.getClass().getMethod(getter);
                Object sucursal = m.invoke(cuerpo);
                if (sucursal != null) {
                    return numero(sucursal, "getIdSucursal");
                }
            } catch (ReflectiveOperationException e) {
                // el tipo no tiene esa relacion
            }
        }
        return null;
    }

    private static Integer numero(Object objeto, String... getters) {
        for (String getter : getters) {
            try {
                Object valor = objeto.getClass().getMethod(getter).invoke(objeto);
                if (valor instanceof Integer n) {
                    return n;
                }
            } catch (ReflectiveOperationException e) {
                // el tipo no tiene ese campo
            }
        }
        return null;
    }

    private static Integer entero(String texto) {
        try {
            return Integer.valueOf(texto);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
