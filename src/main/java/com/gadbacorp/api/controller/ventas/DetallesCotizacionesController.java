package com.gadbacorp.api.controller.ventas;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import com.gadbacorp.api.service.jpa.inventario.ConversionUnidadesService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gadbacorp.api.entity.inventario.Productos;
import com.gadbacorp.api.entity.ventas.Cotizaciones;
import com.gadbacorp.api.entity.ventas.DetallesCotizaciones;
import com.gadbacorp.api.entity.ventas.DetallesCotizacionesDTO;
import com.gadbacorp.api.repository.inventario.ProductosRepository;
import com.gadbacorp.api.repository.ventas.CotizacionesRepository;
import com.gadbacorp.api.repository.ventas.DetallesCotizacionesRepository;
import com.gadbacorp.api.service.ventas.IDetallesCotizacionesService;

import io.swagger.v3.oas.annotations.tags.Tag;
@Tag(name = "Ventas - Detalles de cotizaciones")
@RestController
@RequestMapping("/api/minimarket")
public class DetallesCotizacionesController {
	@Autowired
	private IDetallesCotizacionesService detallesCotizacionesService;

	@Autowired
	private ProductosRepository productosRepository;

	@Autowired
	private DetallesCotizacionesRepository detallesCotizacionesRepository;

	@Autowired
	private CotizacionesRepository cotizacionesRepository;

	@Autowired
	private ConversionUnidadesService conversionUnidadesService;

	@GetMapping("/detalles-cotizaciones")
	public List<DetallesCotizaciones> listarCotizacioneses() {
		return detallesCotizacionesService.listarDetallesCotizacioneses();
	}

	@GetMapping("/detalles-cotizaciones/{id}")
	public Optional<DetallesCotizaciones> buscarDeetallesCotizaciones(@PathVariable Integer id) {
		return detallesCotizacionesService.buscarDetallesCotizaciones(id);
	}
	@GetMapping("/detalles-cotizaciones/cotizacion/{idCotizacion}")
public List<DetallesCotizaciones> listarDetallesPorCotizacion(@PathVariable Integer idCotizacion) {
    return detallesCotizacionesRepository.findByCotizaciones_IdCotizaciones(idCotizacion);
}


	// Una cotizacion NO reserva ni descuenta stock: el stock se descuenta una sola vez, al convertirla en venta.

	@PostMapping("/detalles-cotizaciones")
	public ResponseEntity<?> guardarDetalleCotizacion(@RequestBody DetallesCotizacionesDTO dto) {
		Productos producto = productosRepository.findById(dto.getId_producto()).orElse(null);
		if (producto == null) {
			return ResponseEntity.badRequest().body("Producto no encontrado con ID: " + dto.getId_producto());
		}

		var cotizacion = cotizacionesRepository.findById(dto.getId_cotizacion()).orElse(null);
		if (cotizacion == null) {
			return ResponseEntity.badRequest().body("Cotización no encontrada con ID: " + dto.getId_cotizacion());
		}

		// la cantidad puede venir en una presentacion (2 sacos) o en unidad base (2.5 kg)
		ConversionUnidadesService.Conversion conversion =
				conversionUnidadesService.convertir(producto, dto.getIdPresentacion(), dto.getCantidad());

		DetallesCotizaciones detalle = new DetallesCotizaciones();
		detalle.setProductos(producto);
		detalle.setCotizaciones(cotizacion);
		detalle.setCantidad(dto.getCantidad());
		detalle.setPresentacion(conversion.presentacion());
		detalle.setCantidadBase(conversion.cantidadBase());
		detalle.setPrecioUnitario(dto.getPrecioUnitario());
		detalle.setSubTotal(dto.getSubTotal());
		detalle.setFechaCotizacion(dto.getFechaCotizaciones());

		return ResponseEntity.ok(detallesCotizacionesService.guardarDetallesCotizaciones(detalle));
	}

	@PutMapping("/detalles-cotizaciones")
	public ResponseEntity<?> actualizarDetalleCotizacion(@RequestBody DetallesCotizacionesDTO dto) {
		Optional<DetallesCotizaciones> detalleOpt = detallesCotizacionesService.buscarDetallesCotizaciones(dto.getIdDetallesCotizaciones());
		if (detalleOpt.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		DetallesCotizaciones detalle = detalleOpt.get();

		Productos nuevoProducto = productosRepository.findById(dto.getId_producto()).orElse(null);
		if (nuevoProducto == null) {
			return ResponseEntity.badRequest().body("Producto nuevo no encontrado con ID: " + dto.getId_producto());
		}
		Cotizaciones nuevaCotizacion = cotizacionesRepository.findById(dto.getId_cotizacion()).orElse(null);
		if (nuevaCotizacion == null) {
			return ResponseEntity.badRequest().body("Cotización nueva no encontrada con ID: " + dto.getId_cotizacion());
		}

		ConversionUnidadesService.Conversion conversion =
				conversionUnidadesService.convertir(nuevoProducto, dto.getIdPresentacion(), dto.getCantidad());

		detalle.setCantidad(dto.getCantidad());
		detalle.setPresentacion(conversion.presentacion());
		detalle.setCantidadBase(conversion.cantidadBase());
		detalle.setPrecioUnitario(dto.getPrecioUnitario());
		detalle.setSubTotal(dto.getSubTotal());
		detalle.setFechaCotizacion(dto.getFechaCotizaciones());
		detalle.setProductos(nuevoProducto);
		detalle.setCotizaciones(nuevaCotizacion);

		return ResponseEntity.ok(detallesCotizacionesService.guardarDetallesCotizaciones(detalle));
	}

	@DeleteMapping("/detalles-cotizaciones/{id}")
	public ResponseEntity<?> eliminarDetalleCotizacion(@PathVariable Integer id) {
		if (detallesCotizacionesService.buscarDetallesCotizaciones(id).isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		detallesCotizacionesService.eliminarDetallesCotizaciones(id);
		return ResponseEntity.ok().body("Detalle de cotización eliminado.");
	}
}
