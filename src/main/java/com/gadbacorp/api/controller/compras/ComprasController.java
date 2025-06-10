package com.gadbacorp.api.controller.compras;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.gadbacorp.api.entity.compras.Compras;
import com.gadbacorp.api.entity.compras.ComprasDTO;
import com.gadbacorp.api.entity.compras.Proveedores;
import com.gadbacorp.api.entity.ventas.MetodosPago;
import com.gadbacorp.api.service.compras.IComprasService;
import com.gadbacorp.api.service.compras.IProveedoresService;
import com.gadbacorp.api.service.ventas.IMetodosPagoService;

@RestController
@RequestMapping("/api/minimarket/compras")
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class ComprasController {

    @Autowired
    private IComprasService comprasService;
    
    @Autowired
    private IProveedoresService proveedoresService;
    
    @Autowired
    private IMetodosPagoService metodosPagoService;

    @GetMapping
    public ResponseEntity<List<Compras>> listarTodasCompras() {
        List<Compras> compras = comprasService.buscarTodos();
        return new ResponseEntity<>(compras, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> crearCompra(@RequestBody ComprasDTO compraDTO) {
        try {
            // Validar campos obligatorios
            if(compraDTO.getIdProveedor() == null || compraDTO.getIdMetodoPago() == null) {
                return ResponseEntity.badRequest().body("Proveedor y método de pago son obligatorios");
            }
            
            // Validar proveedor
            Optional<Proveedores> proveedor = proveedoresService.buscarId(compraDTO.getIdProveedor());
            if(proveedor.isEmpty()) {
                return ResponseEntity.badRequest().body("Proveedor no encontrado");
            }
            
            // Validar método de pago
            Optional<MetodosPago> metodoPago = metodosPagoService.obtenerMetodoPago(compraDTO.getIdMetodoPago());
            if(metodoPago.isEmpty()) {
                return ResponseEntity.badRequest().body("Método de pago no encontrado");
            }
            
            // Establecer estado por defecto si no viene
            if(compraDTO.getEstado() == null) {
                compraDTO.setEstado(1);
            }
            
            // Mapear DTO a entidad
            Compras compra = new Compras();
            compra.setProveedor(proveedor.get());
            compra.setMetodoPago(metodoPago.get());
            compra.setTotal(compraDTO.getTotal());
            compra.setDescripcion(compraDTO.getDescripcion());
            compra.setFechaCompra(compraDTO.getFechaCompra());
            compra.setEstado(compraDTO.getEstado());
            
            // Guardar compra
            comprasService.guardar(compra);
            return ResponseEntity.status(HttpStatus.CREATED).body(compra);
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("Error al crear compra: " + e.getMessage());
        }
    }

    @PutMapping
    public ResponseEntity<?> actualizarCompra(@RequestBody ComprasDTO compraDTO) {
        try {
            // Validar que se envió el ID de la compra
            if(compraDTO.getIdCompra() == null) {
                return ResponseEntity.badRequest().body("El ID de la compra es requerido");
            }
            
            // Validar que existe la compra
            Optional<Compras> compraExistente = comprasService.buscarId(compraDTO.getIdCompra());
            if(compraExistente.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            // Validar proveedor
            Optional<Proveedores> proveedor = proveedoresService.buscarId(compraDTO.getIdProveedor());
            if(proveedor.isEmpty()) {
                return ResponseEntity.badRequest().body("Proveedor no encontrado");
            }
            
            // Validar método de pago
            Optional<MetodosPago> metodoPago = metodosPagoService.obtenerMetodoPago(compraDTO.getIdMetodoPago());
            if(metodoPago.isEmpty()) {
                return ResponseEntity.badRequest().body("Método de pago no encontrado");
            }
            
            // Actualizar entidad
            Compras compra = compraExistente.get();
            compra.setProveedor(proveedor.get());
            compra.setMetodoPago(metodoPago.get());
            compra.setTotal(compraDTO.getTotal());
            compra.setDescripcion(compraDTO.getDescripcion());
            compra.setFechaCompra(compraDTO.getFechaCompra());
            compra.setEstado(compraDTO.getEstado());
            
            // Guardar cambios
            comprasService.modificar(compra);
            return ResponseEntity.ok(compra);
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("Error al actualizar compra: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarCompra(@PathVariable Integer id) {
        try {
            Optional<Compras> compra = comprasService.buscarId(id);
            if(compra.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            comprasService.eliminar(id);
            return ResponseEntity.ok("Compra eliminada correctamente");
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("Error al eliminar compra: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerCompraPorId(@PathVariable Integer id) {
        try {
            Optional<Compras> compra = comprasService.buscarId(id);
            return compra.map(ResponseEntity::ok)
                        .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("Error al obtener compra: " + e.getMessage());
        }
    }
}