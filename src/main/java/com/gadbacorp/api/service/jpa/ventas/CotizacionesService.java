package com.gadbacorp.api.service.jpa.ventas;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gadbacorp.api.entity.inventario.Almacenes;
import com.gadbacorp.api.entity.inventario.Inventario;
import com.gadbacorp.api.entity.inventario.InventarioProducto;
import com.gadbacorp.api.entity.ventas.Cotizaciones;
import com.gadbacorp.api.entity.ventas.DetallesCotizaciones;
import com.gadbacorp.api.entity.ventas.DetallesVentas;
import com.gadbacorp.api.entity.ventas.Ventas;
import com.gadbacorp.api.repository.inventario.AlmacenesRepository;
import com.gadbacorp.api.repository.inventario.InventarioProductoRepository;
import com.gadbacorp.api.repository.inventario.InventarioRepository;
import com.gadbacorp.api.repository.inventario.ProductosRepository;
import com.gadbacorp.api.repository.ventas.ClientesRepository;
import com.gadbacorp.api.repository.ventas.CotizacionesRepository;
import com.gadbacorp.api.repository.ventas.DetallesCotizacionesRepository;
import com.gadbacorp.api.repository.ventas.DetallesVentasRepository;
import com.gadbacorp.api.repository.ventas.VentasRepository;
import com.gadbacorp.api.service.ventas.ICotizacionesService;
@Service
public class CotizacionesService implements  ICotizacionesService {

    @Autowired
    private DetallesCotizacionesRepository detallesCotizacionesRepository;

        @Autowired
    private CotizacionesRepository cotizacionesRepository;
    @Autowired
    private ClientesRepository clientesRepository;
    @Autowired
    private ImpuestoService impuestoService;
    @Autowired
    private VentasRepository ventasRepository;
    @Autowired
    private DetallesVentasRepository detallesVentasRepository;
    @Autowired
    private ProductosRepository productoRepo;
    @Autowired
    private com.gadbacorp.api.service.jpa.inventario.StockService stockService;
    @Autowired
    private InventarioProductoRepository inventarioProductoRepository;
    @Autowired
    private InventarioRepository inventarioRepository;
    @Autowired
    private AlmacenesRepository almacenRepository;

    @Override
    public Cotizaciones guardarCotizacion(Cotizaciones cotizacion) {
        return cotizacionesRepository.save(cotizacion);
    }

    @Override
    public Cotizaciones editarCotizaciones(Cotizaciones contizacion) {
        return cotizacionesRepository.save(contizacion);
    }

        public void marcarCotizacionComoPagada(Integer idCotizacion) {
            Cotizaciones cotizacion = cotizacionesRepository.findById(idCotizacion)
                    .orElseThrow(() -> new IllegalArgumentException("Cotización no encontrada"));
            cotizacion.setEstadoCotizacion("PAGADA");
            cotizacionesRepository.save(cotizacion);
        }
            @Override
    public void eliminarCotizaciones(Integer id) {
        cotizacionesRepository.deleteById(id);
    }

    @Override
    public List<Cotizaciones> listarCotizacioneses() {
        return  cotizacionesRepository.findAll();
    }
    @Override
    public List<Cotizaciones> listarCotizacionesPendientesPorSucursal(Integer idSucursal) {
        return cotizacionesRepository.findByCliente_Sucursal_IdSucursalAndEstadoCotizacion(idSucursal, "PENDIENTE");
    }

    @Override
    public Optional<Cotizaciones> buscarCotizacion(Integer id) {
        return cotizacionesRepository.findById(id);
    }

    @Override
    public List<DetallesCotizaciones> buscarDetallesPorCotizacion(Integer id) {
        return detallesCotizacionesRepository.findByCotizaciones_IdCotizaciones(id);

    }

    @Override
    public void eliminarDetalleCotizacion(Integer idDetallesCotizaciones) {
        detallesCotizacionesRepository.deleteById(idDetallesCotizaciones);
    }

    @Override
    public void guardarDetalleCotizacion(DetallesCotizaciones nuevoDetalle) {
        detallesCotizacionesRepository.save(nuevoDetalle);
    }

    @Override
    public List<Cotizaciones> findByClienteSucursal(Integer idSucursal) {
    return cotizacionesRepository.findByCliente_Sucursal_IdSucursal(idSucursal);
    }
    @Override
    public Ventas convertirCotizacionAVenta(Integer idCotizacion, Integer idSucursal) {
        Cotizaciones cotizacion = cotizacionesRepository.findById(idCotizacion)
                .orElseThrow(() -> new IllegalArgumentException("Cotización no encontrada"));

        if (cotizacion.getCliente() == null) {
            throw new IllegalArgumentException("La cotización no tiene cliente asignado");
        }

        Ventas venta = new Ventas();
        venta.setCliente(cotizacion.getCliente());
        venta.setFecha_venta(LocalDate.now());
        venta.setEstado_venta("PAGADA");
        venta.setEstado(1);
        venta.setTotal_venta(cotizacion.getTotalCotizacion());
        venta.setTipo_comprobante("BOLETA"); // o como prefieras
        venta.setNro_comrprobante(null); // Puedes agregar generación correlativa aquí

        venta = ventasRepository.save(venta);

        ImpuestoService.Desglose igv = impuestoService.nuevo();
        for (DetallesCotizaciones detalleCot : cotizacion.getDetallesCotizaciones()) {
            igv.agregar(detalleCot.getSubTotal() != null ? detalleCot.getSubTotal() : 0,
                    !Boolean.FALSE.equals(detalleCot.getProductos().getAfectoIgv()));
            DetallesVentas detalleVenta = new DetallesVentas();
            detalleVenta.setVentas(venta);
            detalleVenta.setProductos(detalleCot.getProductos());
            detalleVenta.setCantidad(detalleCot.getCantidad());
            detalleVenta.setPresentacion(detalleCot.getPresentacion());
            detalleVenta.setCantidadBase(detalleCot.cantidadEnBase());
            detalleVenta.setPrecioUnitario(detalleCot.getPrecioUnitario());
            detalleVenta.setSubTotal(detalleCot.getSubTotal());
            detalleVenta.setFechaVenta(LocalDate.now());
            detalleVenta.setEstado(1);

            detallesVentasRepository.save(detalleVenta);

            stockService.descontar(idSucursal, detalleCot.getProductos().getIdproducto(), detalleCot.cantidadEnBase(),
                    com.gadbacorp.api.entity.inventario.MovimientoStock.VENTA, "Cotizacion " + idCotizacion + " convertida en venta");
        }

        venta.setOpGravada(igv.opGravada());
        venta.setOpInafecta(igv.opInafecta());
        venta.setIgv(igv.igv());
        return ventasRepository.save(venta);
    }

  

}
