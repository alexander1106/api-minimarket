package com.gadbacorp.api.tenant;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.stereotype.Component;

import com.gadbacorp.api.entity.administrable.Empresas;
import com.gadbacorp.api.entity.administrable.EmpresaResumenDTO;
import com.gadbacorp.api.entity.administrable.SucursalDTO;
import com.gadbacorp.api.entity.administrable.Sucursales;
import com.gadbacorp.api.entity.empleados.UsuariosDTO;
import com.gadbacorp.api.entity.caja.AperturaCaja;
import com.gadbacorp.api.entity.caja.Caja;
import com.gadbacorp.api.entity.compras.Compras;
import com.gadbacorp.api.entity.compras.DetallesCompras;
import com.gadbacorp.api.entity.compras.ProveedorDTO;
import com.gadbacorp.api.entity.compras.Proveedores;
import com.gadbacorp.api.entity.delivery.Delivery;
import com.gadbacorp.api.entity.empleados.Usuarios;
import com.gadbacorp.api.entity.caja.CajaDTO;
import com.gadbacorp.api.entity.inventario.Almacenes;
import com.gadbacorp.api.entity.inventario.AlmacenesDTO;
import com.gadbacorp.api.entity.inventario.InventarioDTO;
import com.gadbacorp.api.entity.inventario.InventarioProductosDTO;
import com.gadbacorp.api.entity.inventario.TrasladoInventarioProductoDTO;
import com.gadbacorp.api.entity.inventario.Inventario;
import com.gadbacorp.api.entity.inventario.InventarioProducto;
import com.gadbacorp.api.entity.inventario.LoteProducto;
import com.gadbacorp.api.entity.inventario.MovimientoStock;
import com.gadbacorp.api.entity.inventario.Productos;
import com.gadbacorp.api.entity.inventario.ProductosDTO;
import com.gadbacorp.api.entity.ventas.Clientes;
import com.gadbacorp.api.entity.ventas.Cotizaciones;
import com.gadbacorp.api.entity.ventas.DetallesCotizaciones;
import com.gadbacorp.api.entity.ventas.DetallesVentas;
import com.gadbacorp.api.entity.ventas.Devoluciones;
import com.gadbacorp.api.entity.ventas.MetodosPago;
import com.gadbacorp.api.entity.ventas.Pagos;
import com.gadbacorp.api.entity.ventas.Ventas;

/**
 * De que empresa es cada tipo de dato: sigue la cadena de relaciones hasta la sucursal o la empresa.
 * Es el unico lugar del aislamiento por empresa; para proteger un tipo nuevo basta agregarlo aqui.
 */
@Component
public class TenantRules {

    private final Map<Class<?>, Function<Object, Integer>> reglas = new LinkedHashMap<>();

    @jakarta.persistence.PersistenceContext
    private jakarta.persistence.EntityManager em;

    public TenantRules() {
        regla(Empresas.class, TenantService::empresaDe);
        regla(Sucursales.class, TenantService::empresaDe);
        regla(Usuarios.class, u -> TenantService.empresaDe(u.getSucursal()));
        regla(SucursalDTO.class, SucursalDTO::getId_empresa);
        regla(UsuariosDTO.class, UsuariosDTO::getIdEmpresa);
        regla(EmpresaResumenDTO.class, EmpresaResumenDTO::idempresa);
        regla(Productos.class, p -> TenantService.empresaDe(p.getEmpresa()));
        regla(ProductosDTO.class, ProductosDTO::getIdempresa);
        regla(Proveedores.class, p -> TenantService.empresaDe(p.getEmpresa()));
        regla(ProveedorDTO.class, ProveedorDTO::getIdEmpresa);
        regla(Almacenes.class, a -> TenantService.empresaDe(a.getSucursal()));
        regla(Inventario.class, i -> i.getAlmacen() == null ? null : TenantService.empresaDe(i.getAlmacen().getSucursal()));
        regla(LoteProducto.class, l -> TenantService.empresaDe(l.getProducto() == null ? null : l.getProducto().getEmpresa()));
        regla(MovimientoStock.class, m -> TenantService.empresaDe(m.getProducto() == null ? null : m.getProducto().getEmpresa()));
        // DTOs que solo traen el id del padre: se busca de que empresa es ese padre
        regla(AlmacenesDTO.class, d -> empresaDeSucursalId(d.getIdSucursal()));
        regla(InventarioDTO.class, d -> empresaDeAlmacenId(d.getIdalmacen()));
        regla(InventarioProductosDTO.class, d -> empresaDeInventarioId(d.getIdinventario()));
        regla(TrasladoInventarioProductoDTO.class, d -> empresaDeInventarioProductoId(d.getOrigenId()));
        regla(CajaDTO.class, d -> empresaDeSucursalId(d.getIdSucursal()));
        regla(InventarioProducto.class, ip -> ip.getInventario() == null || ip.getInventario().getAlmacen() == null ? null
                : TenantService.empresaDe(ip.getInventario().getAlmacen().getSucursal()));
        regla(Clientes.class, c -> TenantService.empresaDe(c.getSucursal()));
        regla(MetodosPago.class, m -> TenantService.empresaDe(m.getSucursal()));
        regla(Ventas.class, TenantRules::empresaDeVenta);
        regla(DetallesVentas.class, d -> d.getVentas() == null ? null : empresaDeVenta(d.getVentas()));
        regla(Cotizaciones.class, TenantRules::empresaDeCotizacion);
        regla(DetallesCotizaciones.class, d -> d.getCotizaciones() == null ? null : empresaDeCotizacion(d.getCotizaciones()));
        regla(Devoluciones.class, d -> d.getVentas() == null ? null : empresaDeVenta(d.getVentas()));
        regla(Pagos.class, p -> p.getVentas() == null ? null : empresaDeVenta(p.getVentas()));
        regla(Compras.class, TenantRules::empresaDeCompra);
        regla(DetallesCompras.class, d -> d.getCompra() == null ? null : empresaDeCompra(d.getCompra()));
        regla(Caja.class, c -> TenantService.empresaDe(c.getSucursales()));
        regla(AperturaCaja.class, a -> a.getCaja() == null ? null : TenantService.empresaDe(a.getCaja().getSucursales()));
        regla(Delivery.class, d -> d.getVenta() != null ? empresaDeVenta(d.getVenta())
                : d.getCliente() == null ? null : TenantService.empresaDe(d.getCliente().getSucursal()));
    }

    @SuppressWarnings("unchecked")
    private <T> void regla(Class<T> tipo, Function<T, Integer> empresaDe) {
        reglas.put(tipo, (Function<Object, Integer>) empresaDe);
    }

    Integer empresaDeSucursalId(Integer id) {
        return id == null ? null : TenantService.empresaDe(em.find(Sucursales.class, id));
    }

    Integer empresaDeAlmacenId(Integer id) {
        Almacenes a = id == null ? null : em.find(Almacenes.class, id);
        return a == null ? null : TenantService.empresaDe(a.getSucursal());
    }

    Integer empresaDeInventarioId(Integer id) {
        Inventario i = id == null ? null : em.find(Inventario.class, id);
        return i == null || i.getAlmacen() == null ? null : TenantService.empresaDe(i.getAlmacen().getSucursal());
    }

    Integer empresaDeInventarioProductoId(Integer id) {
        InventarioProducto ip = id == null ? null : em.find(InventarioProducto.class, id);
        return ip == null ? null : empresaDe(ip);
    }

    private static Integer empresaDeVenta(Ventas v) {
        return v.getCliente() == null ? null : TenantService.empresaDe(v.getCliente().getSucursal());
    }

    private static Integer empresaDeCotizacion(Cotizaciones c) {
        return c.getCliente() == null ? null : TenantService.empresaDe(c.getCliente().getSucursal());
    }

    private static Integer empresaDeCompra(Compras c) {
        return c.getSucursal() != null ? TenantService.empresaDe(c.getSucursal())
                : c.getProveedor() == null ? null : TenantService.empresaDe(c.getProveedor().getEmpresa());
    }

    /** true si el tipo del valor esta protegido por empresa. */
    public boolean protegido(Object valor) {
        return valor != null && regla(valor) != null;
    }

    /** Empresa a la que pertenece el dato; null si no se sabe (dato antiguo sin relaciones). */
    public Integer empresaDe(Object valor) {
        Function<Object, Integer> f = regla(valor);
        return f == null ? null : f.apply(valor);
    }

    private Function<Object, Integer> regla(Object valor) {
        Class<?> c = org.hibernate.Hibernate.getClass(valor);
        for (Map.Entry<Class<?>, Function<Object, Integer>> e : reglas.entrySet()) {
            if (e.getKey().isAssignableFrom(c)) {
                return e.getValue();
            }
        }
        return null;
    }
}
