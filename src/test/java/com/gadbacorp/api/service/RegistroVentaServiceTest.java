package com.gadbacorp.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.gadbacorp.api.entity.delivery.Delivery;
import com.gadbacorp.api.entity.delivery.DeliveryDTO;
import com.gadbacorp.api.entity.inventario.ProductoPresentacion;
import com.gadbacorp.api.entity.inventario.Productos;
import com.gadbacorp.api.entity.inventario.UnidadDeMedida;
import com.gadbacorp.api.entity.ventas.Clientes;
import com.gadbacorp.api.entity.ventas.DetallesVentas;
import com.gadbacorp.api.entity.ventas.DetallesVentasDTO;
import com.gadbacorp.api.entity.ventas.MetodosPago;
import com.gadbacorp.api.entity.ventas.VentaCompletaDTO;
import com.gadbacorp.api.entity.ventas.Ventas;
import com.gadbacorp.api.excepciones.StockInsuficienteException;
import com.gadbacorp.api.messaging.VentaCreadaEvent;
import com.gadbacorp.api.messaging.VentaEventPublisher;
import com.gadbacorp.api.repository.delivery.DeliveryRepository;
import com.gadbacorp.api.repository.inventario.ProductoPresentacionRepository;
import com.gadbacorp.api.repository.inventario.ProductosRepository;
import com.gadbacorp.api.repository.ventas.ClientesRepository;
import com.gadbacorp.api.repository.ventas.DetallesVentasRepository;
import com.gadbacorp.api.repository.ventas.MetodosPagoRepository;
import com.gadbacorp.api.repository.ventas.PagosRepository;
import com.gadbacorp.api.repository.ventas.VentasRepository;
import com.gadbacorp.api.service.jpa.cajas.CajaMovimientoService;
import com.gadbacorp.api.service.jpa.inventario.ConversionUnidadesService;
import com.gadbacorp.api.service.jpa.inventario.StockService;
import com.gadbacorp.api.service.jpa.ventas.NumeracionService;
import com.gadbacorp.api.service.jpa.ventas.RegistroVentaService;

@ExtendWith(MockitoExtension.class)
class RegistroVentaServiceTest {

    @Mock VentasRepository ventasRepository;
    @Mock ClientesRepository clientesRepository;
    @Mock MetodosPagoRepository metodosPagoRepository;
    @Mock ProductosRepository productosRepository;
    @Mock DetallesVentasRepository detallesVentasRepository;
    @Mock PagosRepository pagosRepository;
    @Mock VentaEventPublisher eventPublisher;
    @Mock NumeracionService numeracionService;
    @Mock CajaMovimientoService cajaMovimientoService;
    @Mock DeliveryRepository deliveryRepository;
    @Mock StockService stockService;
    @Mock com.gadbacorp.api.repository.caja.AperturaCajaRepository aperturaCajaRepository;
    @org.mockito.Spy com.gadbacorp.api.service.jpa.ventas.ImpuestoService impuestoService =
            new com.gadbacorp.api.service.jpa.ventas.ImpuestoService(new java.math.BigDecimal("0.18"));
    @Mock ProductoPresentacionRepository presentaciones;

    @InjectMocks RegistroVentaService service;

    private VentaCompletaDTO dto;
    private DetallesVentasDTO detalle;
    private Productos arroz;     // unidad base KG (admite decimales)
    private Productos galletas;  // unidad base UND (solo enteros)

    @BeforeEach
    void setUp() {
        // la conversion es real: asi se prueban juntas la venta y las unidades
        ReflectionTestUtils.setField(service, "conversionUnidadesService", new ConversionUnidadesService(presentaciones));

        UnidadDeMedida kg = new UnidadDeMedida();
        kg.setCodigo("KG");
        kg.setPermiteDecimales(true);
        arroz = new Productos();
        arroz.setIdproducto(10);
        arroz.setNombre("Arroz");
        arroz.setUnidadMedida(kg);

        UnidadDeMedida und = new UnidadDeMedida();
        und.setCodigo("UND");
        und.setPermiteDecimales(false);
        galletas = new Productos();
        galletas.setIdproducto(20);
        galletas.setNombre("Galletas");
        galletas.setUnidadMedida(und);

        detalle = new DetallesVentasDTO();
        detalle.setId_producto(10);
        detalle.setCantidad(new BigDecimal("3"));

        dto = new VentaCompletaDTO();
        dto.setId_cliente(1);
        dto.setId_metodo_pago(2);
        dto.setIdSucursal(5);
        dto.setTipo_comprobante("BOLETA");
        dto.setTotal_venta(30.0);
        dto.setDetalles(List.of(detalle));
    }

    @Test
    void rechazaVentaSinCliente() {
        dto.setId_cliente(null);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.registrar(dto));
        assertEquals("El id_cliente es obligatorio.", ex.getMessage());
        verify(ventasRepository, never()).save(any());
    }

    @Test
    void rechazaVentaSinDetalles() {
        dto.setDetalles(List.of());
        assertThrows(IllegalArgumentException.class, () -> service.registrar(dto));
    }

    @Test
    void rechazaClienteInexistente() {
        when(clientesRepository.findById(1)).thenReturn(Optional.empty());
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.registrar(dto));
        assertEquals("Cliente no encontrado.", ex.getMessage());
    }

    @Test
    void rechazaCantidadCeroONegativa() {
        prepararEntidadesBase();
        detalle.setCantidad(BigDecimal.ZERO);
        assertThrows(IllegalArgumentException.class, () -> service.registrar(dto));
        detalle.setCantidad(new BigDecimal("-1"));
        assertThrows(IllegalArgumentException.class, () -> service.registrar(dto));
        verify(stockService, never()).descontar(any(), any(), any(), any(), any());
    }

    @Test
    void generaCorrelativoSiguienteYDescuentaStock() {
        prepararEntidadesBase();
        when(numeracionService.siguienteComprobante("BOLETA")).thenReturn("B-000042");

        Ventas venta = service.registrar(dto);

        assertEquals("B-000042", venta.getNro_comrprobante());
        verify(stockService).descontar(eq(5), eq(10), eq(new BigDecimal("3.000")), eq("VENTA"), any());
        ArgumentCaptor<VentaCreadaEvent> evento = ArgumentCaptor.forClass(VentaCreadaEvent.class);
        verify(eventPublisher).publicarVentaCreada(evento.capture());
        assertEquals("B-000042", evento.getValue().nroComprobante());
        assertEquals(30.0, evento.getValue().total());
    }

    @Test
    void lanzaStockInsuficienteYNoPublicaEvento() {
        prepararEntidadesBase();
        doThrow(new StockInsuficienteException("Stock insuficiente para producto ID 10"))
                .when(stockService).descontar(any(), any(), any(), any(), any());

        assertThrows(StockInsuficienteException.class, () -> service.registrar(dto));
        verify(eventPublisher, never()).publicarVentaCreada(any());
        verify(pagosRepository, never()).save(any());
    }

    @Test
    void vendeKilosFraccionadosDeUnProductoPesable() {
        prepararEntidadesBase();
        detalle.setCantidad(new BigDecimal("2.5"));

        service.registrar(dto);

        verify(stockService).descontar(eq(5), eq(10), eq(new BigDecimal("2.500")), eq("VENTA"), any());
        DetallesVentas guardado = capturarDetalle();
        assertEquals(new BigDecimal("2.5"), guardado.getCantidad());
        assertEquals(new BigDecimal("2.500"), guardado.getCantidadBase());
        assertNull(guardado.getPresentacion());
    }

    @Test
    void vendeUnSacoDe50KilosYDescuentaCincuentaDeStock() {
        prepararEntidadesBase();
        ProductoPresentacion saco = presentacion(7, "Saco 50 kg", "50");
        when(presentaciones.findByIdPresentacionAndProductoIdproducto(7, 10)).thenReturn(Optional.of(saco));
        detalle.setCantidad(new BigDecimal("2"));
        detalle.setIdPresentacion(7);

        service.registrar(dto);

        verify(stockService).descontar(eq(5), eq(10), eq(new BigDecimal("100.000")), eq("VENTA"), any());
        DetallesVentas guardado = capturarDetalle();
        assertEquals(new BigDecimal("2"), guardado.getCantidad());       // lo que se vendio: 2 sacos
        assertEquals(new BigDecimal("100.000"), guardado.getCantidadBase()); // lo que sale del stock: 100 kg
        assertEquals(saco, guardado.getPresentacion());
    }

    @Test
    void vendeUnPaqueteDe12UnidadesDeUnProductoEntero() {
        prepararEntidadesBase(galletas);
        ProductoPresentacion paquete = presentacion(8, "Paquete x12", "12");
        when(presentaciones.findByIdPresentacionAndProductoIdproducto(8, 20)).thenReturn(Optional.of(paquete));
        detalle.setId_producto(20);
        detalle.setCantidad(new BigDecimal("3"));
        detalle.setIdPresentacion(8);

        service.registrar(dto);

        verify(stockService).descontar(eq(5), eq(20), eq(new BigDecimal("36.000")), eq("VENTA"), any());
    }

    @Test
    void unProductoEnteroNoAdmiteFracciones() {
        prepararEntidadesBase(galletas);
        detalle.setId_producto(20);
        detalle.setCantidad(new BigDecimal("1.5"));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.registrar(dto));
        assertEquals(true, ex.getMessage().contains("unidades enteras"), ex.getMessage());
        verify(stockService, never()).descontar(any(), any(), any(), any(), any());
    }

    @Test
    void rechazaUnaPresentacionDeOtroProducto() {
        prepararEntidadesBase();
        when(presentaciones.findByIdPresentacionAndProductoIdproducto(99, 10)).thenReturn(Optional.empty());
        detalle.setIdPresentacion(99);
        assertThrows(IllegalArgumentException.class, () -> service.registrar(dto));
    }

    @Test
    void registraDeliveryYMovimientoDeCajaEnLaMismaTransaccion() {
        prepararEntidadesBase();
        when(numeracionService.siguienteComprobante("BOLETA")).thenReturn("B-000001");
        DeliveryDTO envio = new DeliveryDTO();
        envio.setDireccion("Jr. Lima 123");
        dto.setDelivery(envio);
        turnoAbierto(55);
        dto.setId_apertura_caja(55);
        dto.setMontoPagado(30.0);

        service.registrar(dto);

        ArgumentCaptor<Delivery> delivery = ArgumentCaptor.forClass(Delivery.class);
        verify(deliveryRepository).save(delivery.capture());
        assertEquals("Jr. Lima 123", delivery.getValue().getDireccion());
        assertEquals("Pendiente", delivery.getValue().getEstadoDelivery());
        verify(cajaMovimientoService).registrar(eq(55), any(MetodosPago.class), eq("INGRESO"), eq(30.0), any());
    }

    @Test
    void sinDeliveryNiAperturaNoTocaCajaNiDelivery() {
        prepararEntidadesBase();
        when(numeracionService.siguienteComprobante("BOLETA")).thenReturn("B-000001");

        service.registrar(dto);

        verify(deliveryRepository, never()).save(any());
        verify(cajaMovimientoService, never()).registrar(any(), any(), any(), any(), any());
    }

    @Test
    void siFallaLaCajaLaVentaFalla() {
        prepararEntidadesBase();
        when(numeracionService.siguienteComprobante("BOLETA")).thenReturn("B-000001");
        turnoAbierto(55);
        dto.setId_apertura_caja(55);
        when(cajaMovimientoService.registrar(any(), any(), any(), any(), any()))
                .thenThrow(new IllegalArgumentException("La apertura de caja no está abierta."));

        assertThrows(IllegalArgumentException.class, () -> service.registrar(dto));
    }

    private void prepararEntidadesBase() {
        prepararEntidadesBase(arroz);
    }

    /** Un turno de caja abierto en la sucursal 5, la misma de la venta. */
    private void turnoAbierto(Integer idApertura) {
        com.gadbacorp.api.entity.administrable.Sucursales sucursal = new com.gadbacorp.api.entity.administrable.Sucursales();
        sucursal.setIdSucursal(5);
        com.gadbacorp.api.entity.caja.Caja caja = new com.gadbacorp.api.entity.caja.Caja();
        caja.setSucursales(sucursal);
        com.gadbacorp.api.entity.caja.AperturaCaja apertura = new com.gadbacorp.api.entity.caja.AperturaCaja();
        apertura.setEstadoCaja("ABIERTA");
        apertura.setCaja(caja);
        when(aperturaCajaRepository.findById(idApertura)).thenReturn(Optional.of(apertura));
    }

    private void prepararEntidadesBase(Productos producto) {
        Clientes cliente = mock(Clientes.class);
        lenient().when(clientesRepository.findById(1)).thenReturn(Optional.of(cliente));
        lenient().when(metodosPagoRepository.findById(2)).thenReturn(Optional.of(mock(MetodosPago.class)));
        lenient().when(productosRepository.findById(producto.getIdproducto())).thenReturn(Optional.of(producto));
        lenient().when(ventasRepository.save(any(Ventas.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    private ProductoPresentacion presentacion(int id, String nombre, String factor) {
        ProductoPresentacion p = new ProductoPresentacion();
        p.setIdPresentacion(id);
        p.setNombre(nombre);
        p.setFactor(new BigDecimal(factor));
        return p;
    }

    private DetallesVentas capturarDetalle() {
        ArgumentCaptor<DetallesVentas> captor = ArgumentCaptor.forClass(DetallesVentas.class);
        verify(detallesVentasRepository).save(captor.capture());
        return captor.getValue();
    }
}
