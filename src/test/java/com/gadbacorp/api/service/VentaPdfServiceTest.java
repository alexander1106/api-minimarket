package com.gadbacorp.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.gadbacorp.api.entity.administrable.Empresas;
import com.gadbacorp.api.entity.administrable.Sucursales;
import com.gadbacorp.api.entity.inventario.Productos;
import com.gadbacorp.api.entity.ventas.Clientes;
import com.gadbacorp.api.entity.ventas.DetallesVentas;
import com.gadbacorp.api.entity.ventas.Ventas;
import com.gadbacorp.api.service.jpa.ventas.VentaPdfService;

class VentaPdfServiceTest {

    private final VentaPdfService service = new VentaPdfService();

    @Test
    void generaUnPdfValidoConTodosLosDatos() {
        Empresas empresa = new Empresas();
        empresa.setRazonsocial("Minimarket SAC");
        Sucursales sucursal = new Sucursales();
        sucursal.setEmpresa(empresa);
        Clientes cliente = new Clientes();
        cliente.setNombre("Ana");
        cliente.setDocumento("12345678");
        cliente.setSucursal(sucursal);

        Ventas venta = new Ventas();
        venta.setCliente(cliente);
        venta.setTipo_comprobante("BOLETA");
        venta.setNro_comrprobante("B-000001");
        venta.setFecha_venta(LocalDate.of(2026, 1, 15));
        venta.setTotal_venta(25.5);

        Productos producto = new Productos();
        producto.setNombre("Arroz 1kg");
        DetallesVentas detalle = new DetallesVentas();
        detalle.setProductos(producto);
        detalle.setCantidad(new java.math.BigDecimal("3"));
        detalle.setPrecioUnitario(8.5);
        detalle.setSubTotal(25.5);

        byte[] pdf = service.generar(venta, List.of(detalle));

        assertEquals("%PDF", new String(pdf, 0, 4, StandardCharsets.US_ASCII));
        assertTrue(pdf.length > 500);
    }

    @Test
    void noFallaConDatosIncompletos() {
        Ventas venta = new Ventas();
        byte[] pdf = service.generar(venta, List.of());
        assertEquals("%PDF", new String(pdf, 0, 4, StandardCharsets.US_ASCII));
    }

    @Test
    void muestraLaCantidadDecimalYLaPresentacion() {
        Productos arroz = new Productos();
        arroz.setNombre("Arroz");
        com.gadbacorp.api.entity.inventario.ProductoPresentacion saco = new com.gadbacorp.api.entity.inventario.ProductoPresentacion();
        saco.setNombre("Saco 50 kg");

        DetallesVentas suelto = new DetallesVentas();
        suelto.setProductos(arroz);
        suelto.setCantidad(new java.math.BigDecimal("2.500"));
        suelto.setPrecioUnitario(4.0);
        suelto.setSubTotal(10.0);
        DetallesVentas enSaco = new DetallesVentas();
        enSaco.setProductos(arroz);
        enSaco.setPresentacion(saco);
        enSaco.setCantidad(new java.math.BigDecimal("1"));
        enSaco.setPrecioUnitario(180.0);
        enSaco.setSubTotal(180.0);

        Ventas venta = new Ventas();
        venta.setTotal_venta(190.0);
        byte[] pdf = service.generar(venta, List.of(suelto, enSaco));

        assertEquals("%PDF", new String(pdf, 0, 4, StandardCharsets.US_ASCII));
    }
}
