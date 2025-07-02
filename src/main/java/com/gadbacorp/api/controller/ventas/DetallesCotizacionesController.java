package com.gadbacorp.api.controller.ventas;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gadbacorp.api.entity.inventario.AjusteInventario;
import com.gadbacorp.api.entity.inventario.InventarioProducto;
import com.gadbacorp.api.entity.inventario.Productos;
import com.gadbacorp.api.entity.ventas.Cotizaciones;
import com.gadbacorp.api.entity.ventas.DetallesCotizaciones;
import com.gadbacorp.api.entity.ventas.DetallesCotizacionesDTO;
import com.gadbacorp.api.repository.inventario.AjusteInventarioRepository;
import com.gadbacorp.api.repository.inventario.InventarioProductoRepository;
import com.gadbacorp.api.repository.inventario.ProductosRepository;
import com.gadbacorp.api.repository.ventas.CotizacionesRepository;
import com.gadbacorp.api.service.ventas.IDetallesCotizacionesService;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/minimarket")
public class DetallesCotizacionesController {
	@Autowired
	private IDetallesCotizacionesService detallesCotizacionesService;

	@Autowired
	private ProductosRepository productosRepository;

	@Autowired
	private CotizacionesRepository cotizacionesRepository;

	@Autowired
	private InventarioProductoRepository inventarioProductoRepository;

	@Autowired
	private AjusteInventarioRepository ajusteInventarioRepository;

	@GetMapping("/detalles-cotizaciones")
	public List<DetallesCotizaciones> listarCotizacioneses() {
		return detallesCotizacionesService.listarDetallesCotizacioneses();
	}

	@GetMapping("/detalles-cotizaciones/{id}")
	public Optional<DetallesCotizaciones> buscarDeetallesCotizaciones(@PathVariable Integer id) {
		return detallesCotizacionesService.buscarDetallesCotizaciones(id);
	}

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

		Optional<InventarioProducto> inventarioOpt = inventarioProductoRepository.findFirstByProducto_Idproducto(dto.getId_producto());
		if (inventarioOpt.isEmpty()) {
			return ResponseEntity.badRequest().body("InventarioProducto no encontrado para el producto ID: " + dto.getId_producto());
		}

		InventarioProducto inventario = inventarioOpt.get();
		if (inventario.getStockactual() < dto.getCantidad()) {
			return ResponseEntity.badRequest().body("Stock insuficiente. Disponible: " + inventario.getStockactual());
		}

		// Actualizar stock
		inventario.setStockactual(inventario.getStockactual() - dto.getCantidad());
		inventarioProductoRepository.save(inventario);

		// Registrar ajuste de inventario
		AjusteInventario ajuste = new AjusteInventario();
		ajuste.setCantidad(dto.getCantidad());
		ajuste.setDescripcion("COTIZACIÓN");
		ajuste.setFechaAjuste(LocalDateTime.now());
		ajuste.setInventarioProducto(inventario);
		ajusteInventarioRepository.save(ajuste);

		// Registrar detalle de cotización
		DetallesCotizaciones detalle = new DetallesCotizaciones();
		detalle.setProductos(producto);
		detalle.setCotizaciones(cotizacion);
		detalle.setCantidad(dto.getCantidad());
		detalle.setSubTotal(dto.getSubTotal());
		detalle.setPrecioUnitario(dto.getPrecioUnitario());
		detalle.setFechaCotizacion(dto.getFechaCotizaciones());

		return ResponseEntity.ok(detallesCotizacionesService.guardarDetallesCotizaciones(detalle));
	}

	@PutMapping("/detalles-cotizaciones")
	public ResponseEntity<?> actualizarDetalleCotizacion(@RequestBody DetallesCotizacionesDTO dto) {
		// Buscar detalle original
		Integer id = dto.getIdDetallesCotizaciones();
		Optional<DetallesCotizaciones> detalleOpt = detallesCotizacionesService.buscarDetallesCotizaciones(id);
		if (detalleOpt.isEmpty()) {
			return ResponseEntity.notFound().build();
		}

		DetallesCotizaciones detalle = detalleOpt.get();
		Productos productoAnterior = detalle.getProductos();

		// Obtener nuevo producto
		Productos nuevoProducto = productosRepository.findById(dto.getId_producto()).orElse(null);
		if (nuevoProducto == null) {
			return ResponseEntity.badRequest().body("Producto nuevo no encontrado con ID: " + dto.getId_producto());
		}

		Cotizaciones nuevaCotizacion = cotizacionesRepository.findById(dto.getId_cotizacion()).orElse(null);
		if (nuevaCotizacion == null) {
			return ResponseEntity.badRequest().body("Cotización nueva no encontrada con ID: " + dto.getId_cotizacion());
		}

		// Revertir stock del producto anterior
		InventarioProducto inventarioAnterior = inventarioProductoRepository
				.findFirstByProducto_Idproducto(productoAnterior.getIdproducto()).orElse(null);
		if (inventarioAnterior == null) {
			return ResponseEntity.badRequest().body("Inventario no encontrado para el producto anterior.");
		}
		inventarioAnterior.setStockactual(inventarioAnterior.getStockactual() + detalle.getCantidad());
		inventarioProductoRepository.save(inventarioAnterior);

		// Ajustar stock del nuevo producto
		InventarioProducto inventarioNuevo = inventarioProductoRepository
				.findFirstByProducto_Idproducto(nuevoProducto.getIdproducto()).orElse(null);
		if (inventarioNuevo == null) {
			return ResponseEntity.badRequest().body("Inventario no encontrado para el nuevo producto.");
		}

		if (inventarioNuevo.getStockactual() < dto.getCantidad()) {
			return ResponseEntity.badRequest().body("Stock insuficiente para el nuevo producto. Disponible: " + inventarioNuevo.getStockactual());
		}

		inventarioNuevo.setStockactual(inventarioNuevo.getStockactual() - dto.getCantidad());
		inventarioProductoRepository.save(inventarioNuevo);

		// Registrar ajuste de inventario
		AjusteInventario ajuste = new AjusteInventario();
		ajuste.setCantidad(dto.getCantidad());
		ajuste.setDescripcion("AJUSTE POR ACTUALIZACIÓN DE COTIZACIÓN");
		ajuste.setFechaAjuste(LocalDateTime.now());
		ajuste.setInventarioProducto(inventarioNuevo);
		ajusteInventarioRepository.save(ajuste);

		// Actualizar los campos del detalle
		detalle.setCantidad(dto.getCantidad());
		detalle.setPrecioUnitario(dto.getPrecioUnitario());
		detalle.setSubTotal(dto.getSubTotal());
		detalle.setFechaCotizacion(dto.getFechaCotizaciones());
		detalle.setProductos(nuevoProducto);
		detalle.setCotizaciones(nuevaCotizacion);

		return ResponseEntity.ok(detallesCotizacionesService.guardarDetallesCotizaciones(detalle));
	}

	@DeleteMapping("/detalles-cotizaciones/{id}")
	public ResponseEntity<?> eliminarDetalleCotizacion(@PathVariable Integer id) {
		Optional<DetallesCotizaciones> detalleOpt = detallesCotizacionesService.buscarDetallesCotizaciones(id);
		if (detalleOpt.isEmpty()) {
			return ResponseEntity.notFound().build();
		}

		DetallesCotizaciones detalle = detalleOpt.get();
		Optional<InventarioProducto> inventarioOpt = inventarioProductoRepository.findFirstByProducto_Idproducto(detalle.getProductos().getIdproducto());

		if (inventarioOpt.isEmpty()) {
			return ResponseEntity.badRequest().body("Inventario no encontrado para el producto.");
		}

		InventarioProducto inventario = inventarioOpt.get();

		// Restaurar stock
		inventario.setStockactual(inventario.getStockactual() + detalle.getCantidad());
		inventarioProductoRepository.save(inventario);

		// Ajuste de inventario
		AjusteInventario ajuste = new AjusteInventario();
		ajuste.setCantidad(detalle.getCantidad());
		ajuste.setDescripcion("AJUSTE POR ELIMINACIÓN DE COTIZACIÓN");
		ajuste.setFechaAjuste(LocalDateTime.now());
		ajuste.setInventarioProducto(inventario);
		ajusteInventarioRepository.save(ajuste);

		detallesCotizacionesService.eliminarDetallesCotizaciones(id);
		return ResponseEntity.ok().body("Detalle de cotización eliminado y stock actualizado.");
	}
}
