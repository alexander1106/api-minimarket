package com.gadbacorp.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.gadbacorp.api.entity.inventario.Almacenes;
import com.gadbacorp.api.entity.inventario.Inventario;
import com.gadbacorp.api.entity.inventario.InventarioProducto;
import com.gadbacorp.api.entity.inventario.ProductoPresentacion;
import com.gadbacorp.api.entity.inventario.Productos;
import com.gadbacorp.api.entity.inventario.UnidadDeMedida;
import com.gadbacorp.api.excepciones.StockInsuficienteException;
import com.gadbacorp.api.repository.inventario.AlmacenesRepository;
import com.gadbacorp.api.repository.inventario.InventarioProductoRepository;
import com.gadbacorp.api.repository.inventario.InventarioRepository;
import com.gadbacorp.api.repository.inventario.ProductoPresentacionRepository;
import com.gadbacorp.api.service.jpa.inventario.ConversionUnidadesService;
import com.gadbacorp.api.service.jpa.inventario.StockService;

class ConversionYStockTest {

    private static BigDecimal d(String v) {
        return new BigDecimal(v);
    }

    private ProductoPresentacionRepository presentaciones;
    private ConversionUnidadesService conversion;
    private Productos arroz;
    private Productos galletas;

    @BeforeEach
    void setUp() {
        presentaciones = mock(ProductoPresentacionRepository.class);
        conversion = new ConversionUnidadesService(presentaciones);

        arroz = producto(1, "Arroz", "KG", true);
        galletas = producto(2, "Galletas", "UND", false);

        when(presentaciones.findByIdPresentacionAndProductoIdproducto(10, 1))
                .thenReturn(Optional.of(presentacion(10, "Saco 50 kg", "50")));
        when(presentaciones.findByIdPresentacionAndProductoIdproducto(20, 2))
                .thenReturn(Optional.of(presentacion(20, "Paquete x12", "12")));
        when(presentaciones.findByIdPresentacionAndProductoIdproducto(21, 2))
                .thenReturn(Optional.of(presentacion(21, "Media docena", "6")));
    }

    private Productos producto(int id, String nombre, String codigoUnidad, boolean decimales) {
        UnidadDeMedida u = new UnidadDeMedida();
        u.setCodigo(codigoUnidad);
        u.setPermiteDecimales(decimales);
        Productos p = new Productos();
        p.setIdproducto(id);
        p.setNombre(nombre);
        p.setUnidadMedida(u);
        return p;
    }

    private ProductoPresentacion presentacion(int id, String nombre, String factor) {
        ProductoPresentacion p = new ProductoPresentacion();
        p.setIdPresentacion(id);
        p.setNombre(nombre);
        p.setFactor(d(factor));
        return p;
    }

    @Test
    void sinPresentacionLaCantidadYaEstaEnUnidadBase() {
        assertEquals(d("2.500"), conversion.aBase(arroz, null, d("2.5")));
        assertEquals(d("3.000"), conversion.aBase(galletas, null, d("3")));
    }

    @Test
    void unSacoDe50KilosSonCincuentaKilos() {
        assertEquals(d("50.000"), conversion.aBase(arroz, 10, d("1")));
        assertEquals(d("500.000"), conversion.aBase(arroz, 10, d("10")));
    }

    @Test
    void unaFraccionDeSacoEsValidaSiElProductoAdmiteDecimales() {
        assertEquals(d("25.000"), conversion.aBase(arroz, 10, d("0.5")));   // medio saco
        assertEquals(d("12.500"), conversion.aBase(arroz, 10, d("0.25")));
    }

    @Test
    void unPaqueteDeDoceSonDoceUnidades() {
        assertEquals(d("12.000"), conversion.aBase(galletas, 20, d("1")));
        assertEquals(d("36.000"), conversion.aBase(galletas, 20, d("3")));
    }

    @Test
    void unProductoEnteroRechazaFraccionesQueNoDanUnidadesEnteras() {
        assertThrows(IllegalArgumentException.class, () -> conversion.aBase(galletas, null, d("1.5")));
        // 0.5 paquete de 12 = 6 unidades: es entero, asi que si vale
        assertEquals(d("6.000"), conversion.aBase(galletas, 20, d("0.5")));
        // 0.25 de paquete = 3 unidades: valido; 0.3 = 3.6 unidades: invalido
        assertEquals(d("3.000"), conversion.aBase(galletas, 20, d("0.25")));
        assertThrows(IllegalArgumentException.class, () -> conversion.aBase(galletas, 20, d("0.3")));
    }

    @Test
    void rechazaCantidadesInvalidas() {
        assertThrows(IllegalArgumentException.class, () -> conversion.aBase(arroz, null, null));
        assertThrows(IllegalArgumentException.class, () -> conversion.aBase(arroz, null, BigDecimal.ZERO));
        assertThrows(IllegalArgumentException.class, () -> conversion.aBase(arroz, null, d("-1")));
        assertThrows(IllegalArgumentException.class, () -> conversion.aBase(arroz, null, d("1.2345")));
    }

    @Test
    void rechazaUnaPresentacionQueNoEsDelProducto() {
        when(presentaciones.findByIdPresentacionAndProductoIdproducto(10, 2)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> conversion.aBase(galletas, 10, d("1")));
    }

    @Test
    void sinUnidadAsignadaSeTrataComoUnidadEntera() {
        Productos sinUnidad = new Productos();
        sinUnidad.setIdproducto(3);
        assertFalse(ConversionUnidadesService.admiteDecimales(sinUnidad));
        assertThrows(IllegalArgumentException.class, () -> conversion.aBase(sinUnidad, null, d("0.5")));
    }

    @Test
    void validarFraccionParaMovimientosEnUnidadBase() {
        ConversionUnidadesService.validarFraccion(arroz, d("0.125"));          // kg: admite
        ConversionUnidadesService.validarFraccion(galletas, d("24"));          // und entero: admite
        assertThrows(IllegalArgumentException.class, () -> ConversionUnidadesService.validarFraccion(galletas, d("0.5")));
        assertThrows(IllegalArgumentException.class, () -> ConversionUnidadesService.validarFraccion(arroz, d("0.1234")));
        assertThrows(IllegalArgumentException.class, () -> ConversionUnidadesService.validarFraccion(arroz, null));
    }

    private AlmacenesRepository almacenes;
    private InventarioRepository inventarios;
    private InventarioProductoRepository stocks;
    private StockService stockService;

    private InventarioProducto fila(String stock) {
        InventarioProducto ip = new InventarioProducto();
        ip.setStockactual(d(stock));
        return ip;
    }

    /** Una sucursal con tantos inventarios como filas de stock se indiquen. */
    private void sucursalConStocks(int idProducto, InventarioProducto... filas) {
        almacenes = mock(AlmacenesRepository.class);
        inventarios = mock(InventarioRepository.class);
        stocks = mock(InventarioProductoRepository.class);
        stockService = new StockService(almacenes, inventarios, stocks, mock(com.gadbacorp.api.service.jpa.inventario.KardexService.class),
                mock(com.gadbacorp.api.service.jpa.inventario.LoteService.class),
                mock(com.gadbacorp.api.service.jpa.notificaciones.NotificacionService.class));

        Almacenes almacen = new Almacenes();
        almacen.setIdalmacen(1);
        when(almacenes.findBySucursalIdSucursal(5)).thenReturn(List.of(almacen));
        java.util.ArrayList<Inventario> lista = new java.util.ArrayList<>();
        for (int i = 0; i < filas.length; i++) {
            Inventario inv = mock(Inventario.class);
            when(inv.getIdinventario()).thenReturn(100 + i);
            lista.add(inv);
            when(stocks.findByProductoIdproductoAndInventarioIdinventario(idProducto, 100 + i)).thenReturn(Optional.of(filas[i]));
        }
        when(inventarios.findByAlmacen_Idalmacen(1)).thenReturn(lista);
    }

    @Test
    void descuentaKilosFraccionados() {
        InventarioProducto f = fila("100.000");
        sucursalConStocks(1, f);
        stockService.descontar(5, 1, d("2.5"));
        assertEquals(d("97.500"), f.getStockactual());
        verify(stocks).save(f);
    }

    @Test
    void repartePorVariosInventariosHastaCubrirLaCantidad() {
        InventarioProducto a = fila("10");
        InventarioProducto b = fila("30");
        sucursalConStocks(1, a, b);

        stockService.descontar(5, 1, d("25.5"));

        assertEquals(0, a.getStockactual().compareTo(BigDecimal.ZERO));
        assertEquals(0, b.getStockactual().compareTo(d("14.5")));
    }

    @Test
    void siNoAlcanzaNoDescuentaNadaYDicePorQue() {
        InventarioProducto a = fila("10");
        InventarioProducto b = fila("5");
        sucursalConStocks(1, a, b);

        StockInsuficienteException ex = assertThrows(StockInsuficienteException.class,
                () -> stockService.descontar(5, 1, d("20")));

        assertTrue(ex.getMessage().contains("Disponible: 15"), ex.getMessage());
        assertTrue(ex.getMessage().contains("solicitado: 20"), ex.getMessage());
        assertEquals(d("10"), a.getStockactual());   // nada se modifico
        assertEquals(d("5"), b.getStockactual());
        verify(stocks, never()).save(any());
    }

    @Test
    void descontarExactamenteTodoElStockEsValido() {
        InventarioProducto a = fila("7.250");
        sucursalConStocks(1, a);
        stockService.descontar(5, 1, d("7.25"));
        assertEquals(0, a.getStockactual().compareTo(BigDecimal.ZERO));
    }

    @Test
    void unProductoSinStockEnLaSucursalEsInsuficiente() {
        sucursalConStocks(1);
        assertThrows(StockInsuficienteException.class, () -> stockService.descontar(5, 1, d("1")));
    }
}
