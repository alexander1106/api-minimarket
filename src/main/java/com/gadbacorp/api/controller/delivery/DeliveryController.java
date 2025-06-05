package com.gadbacorp.api.controller.delivery;

import com.gadbacorp.api.entity.delivery.Delivery;
import com.gadbacorp.api.entity.dto.delivery.DeliveryDTO;
import com.gadbacorp.api.entity.ventas.Ventas;
import com.gadbacorp.api.service.delivery.IDeliveryService;
import com.gadbacorp.api.service.ventas.IVentasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/minimarket/delivery")
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class DeliveryController {

    @Autowired
    private IDeliveryService deliveryService;
    
    @Autowired
    private IVentasService ventasService;

    @GetMapping
    public ResponseEntity<?> listarTodosDelivery() {
        try {
            List<Delivery> deliveries = deliveryService.findAll();
            return new ResponseEntity<>(deliveries, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error al listar deliveries: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> crearDelivery(@RequestBody DeliveryDTO deliveryDTO) {
        try {
            // Validar campos obligatorios
            if(deliveryDTO.getIdVenta() == null) {
                return ResponseEntity.badRequest().body("El ID de venta es obligatorio");
            }
            
            // Validar que existe la venta
            Optional<Ventas> venta = ventasService.buscarVenta(deliveryDTO.getIdVenta().intValue());
            if(venta.isEmpty()) {
                return ResponseEntity.badRequest().body("Venta no encontrada con ID: " + deliveryDTO.getIdVenta());
            }
            
            // Valores por defecto
            if(deliveryDTO.getEstado() == null) {
                deliveryDTO.setEstado(1); // 1 = Pendiente
            }
            if(deliveryDTO.getCostoEnvio() == null) {
                deliveryDTO.setCostoEnvio(BigDecimal.ZERO);
            }
            
            // Mapeo a entidad
            Delivery delivery = new Delivery();
            delivery.setVenta(venta.get());
            delivery.setDireccion(deliveryDTO.getDireccion());
            delivery.setEstado(deliveryDTO.getEstado());
            delivery.setFechaEntrega(deliveryDTO.getFechaEntrega());
            delivery.setFechaEnvio(deliveryDTO.getFechaEnvio());
            delivery.setCostoEnvio(deliveryDTO.getCostoEnvio());
            delivery.setObservaciones(deliveryDTO.getObservaciones());
            
            // Guardar
            Delivery deliveryGuardado = deliveryService.save(delivery);
            return ResponseEntity.status(HttpStatus.CREATED).body(deliveryGuardado);
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error al crear delivery: " + e.getMessage());
        }
    }

    @PutMapping
    public ResponseEntity<?> actualizarDelivery(@RequestBody DeliveryDTO deliveryDTO) {
        try {
            // Validación de ID
            if(deliveryDTO.getId() == null) {
                return ResponseEntity.badRequest().body("El ID del delivery es requerido");
            }
            
            // Verificar existencia
            Optional<Delivery> deliveryExistente = deliveryService.findById(deliveryDTO.getId());
            if(deliveryExistente.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            // Validar venta si viene en el DTO
            if(deliveryDTO.getIdVenta() != null) {
                Optional<Ventas> venta = ventasService.buscarVenta(deliveryDTO.getIdVenta().intValue());
                if(venta.isEmpty()) {
                    return ResponseEntity.badRequest().body("Venta no encontrada con ID: " + deliveryDTO.getIdVenta());
                }
                deliveryExistente.get().setVenta(venta.get());
            }
            
            // Actualizar campos
            Delivery delivery = deliveryExistente.get();
            if(deliveryDTO.getDireccion() != null) {
                delivery.setDireccion(deliveryDTO.getDireccion());
            }
            if(deliveryDTO.getEstado() != null) {
                delivery.setEstado(deliveryDTO.getEstado());
            }
            if(deliveryDTO.getFechaEntrega() != null) {
                delivery.setFechaEntrega(deliveryDTO.getFechaEntrega());
            }
            if(deliveryDTO.getFechaEnvio() != null) {
                delivery.setFechaEnvio(deliveryDTO.getFechaEnvio());
            }
            if(deliveryDTO.getCostoEnvio() != null) {
                delivery.setCostoEnvio(deliveryDTO.getCostoEnvio());
            }
            if(deliveryDTO.getObservaciones() != null) {
                delivery.setObservaciones(deliveryDTO.getObservaciones());
            }
            
            // Guardar cambios
            Delivery deliveryActualizado = deliveryService.save(delivery);
            return ResponseEntity.ok(deliveryActualizado);
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error al actualizar delivery: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarDelivery(@PathVariable Long id) {
        try {
            if(!deliveryService.findById(id).isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            deliveryService.deleteById(id);
            return ResponseEntity.ok("Delivery con ID " + id + " eliminado correctamente");
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error al eliminar delivery: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerDeliveryPorId(@PathVariable Long id) {
        try {
            Optional<Delivery> delivery = deliveryService.findById(id);
            return delivery.map(ResponseEntity::ok)
                          .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error al obtener delivery: " + e.getMessage());
        }
    }
}