package com.gadbacorp.api.controller.delivery;

import com.gadbacorp.api.entity.Vehiculo.Vehiculo;
import com.gadbacorp.api.entity.delivery.Delivery;
import com.gadbacorp.api.entity.dto.delivery.DeliveryDTO;
import com.gadbacorp.api.entity.empleados.Empleado;
import com.gadbacorp.api.enums.Estado.EstadoDelivery;
import com.gadbacorp.api.entity.ventas.Ventas;
import com.gadbacorp.api.service.Vehiculo.VehiculoService;
import com.gadbacorp.api.service.delivery.IDeliveryService;
import com.gadbacorp.api.service.ventas.IVentasService;
import com.gadbacorp.api.service.empleados.IEmpleadoServices;
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

@Autowired
private VehiculoService vehiculoService;

@Autowired
private IEmpleadoServices empleadoService;

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
        if (deliveryDTO.getIdVenta() == null) {
            return ResponseEntity.badRequest().body("El ID de venta es obligatorio");
        }

        Optional<Ventas> ventaOpt = ventasService.buscarVenta(deliveryDTO.getIdVenta().intValue());
        if (ventaOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Venta no encontrada con ID: " + deliveryDTO.getIdVenta());
        }

        Vehiculo vehiculo = null;
        if (deliveryDTO.getIdVehiculo() != null) {
            Optional<Vehiculo> vehiculoOpt = vehiculoService.getVehiculoById(deliveryDTO.getIdVehiculo());
            if (vehiculoOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Vehículo no encontrado con ID: " + deliveryDTO.getIdVehiculo());
            }
            vehiculo = vehiculoOpt.get();
        } else {
            List<Vehiculo> todosVehiculos = vehiculoService.getAllVehiculos();
            if (!todosVehiculos.isEmpty()) {
                vehiculo = todosVehiculos.get(0);
            }
        }

        Empleado empleado = null;
        if (deliveryDTO.getIdEmpleado() != null) {
            Optional<Empleado> empleadoOpt = empleadoService.buscarId(deliveryDTO.getIdEmpleado());
            if (empleadoOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Empleado no encontrado con ID: " + deliveryDTO.getIdEmpleado());
            }
            empleado = empleadoOpt.get();
        }

        Integer codigoEstado = deliveryDTO.getEstado();
        if (codigoEstado == null) {
            codigoEstado = EstadoDelivery.PENDIENTE.getCodigo();
        }

        EstadoDelivery estadoEnum;
        try {
            estadoEnum = EstadoDelivery.fromCodigo(codigoEstado);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body("Estado inválido: " + codigoEstado);
        }

        BigDecimal costoEnvio = deliveryDTO.getCostoEnvio() != null ? deliveryDTO.getCostoEnvio() : BigDecimal.ZERO;

        Delivery delivery = new Delivery();
        delivery.setVenta(ventaOpt.get());
        delivery.setDireccion(deliveryDTO.getDireccion());
        delivery.setEstado(estadoEnum);
        delivery.setFechaEntrega(deliveryDTO.getFechaEntrega());
        delivery.setFechaEnvio(deliveryDTO.getFechaEnvio());
        delivery.setCostoEnvio(costoEnvio);
        delivery.setObservaciones(deliveryDTO.getObservaciones());
        delivery.setVehiculo(vehiculo);
        delivery.setEmpleado(empleado);

        Delivery deliveryGuardado = deliveryService.save(delivery);
        return ResponseEntity.status(HttpStatus.CREATED).body(deliveryGuardado);

    } catch (Exception e) {
        return ResponseEntity.internalServerError()
                .body("Error al crear delivery: " + e.getMessage());
    }
}

@PutMapping("/{id}")
public ResponseEntity<?> actualizarDelivery(@PathVariable Long id, @RequestBody DeliveryDTO deliveryDTO) {
    try {
        Optional<Delivery> deliveryExistenteOpt = deliveryService.findById(id);
        if (deliveryExistenteOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Delivery delivery = deliveryExistenteOpt.get();

        if (deliveryDTO.getIdVenta() != null) {
            Optional<Ventas> ventaOpt = ventasService.buscarVenta(deliveryDTO.getIdVenta().intValue());
            if (ventaOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Venta no encontrada con ID: " + deliveryDTO.getIdVenta());
            }
            delivery.setVenta(ventaOpt.get());
        }

        if (deliveryDTO.getIdVehiculo() != null) {
            Optional<Vehiculo> vehiculoOpt = vehiculoService.getVehiculoById(deliveryDTO.getIdVehiculo());
            if (vehiculoOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Vehículo no encontrado con ID: " + deliveryDTO.getIdVehiculo());
            }
            delivery.setVehiculo(vehiculoOpt.get());
        }

        if (deliveryDTO.getIdEmpleado() != null) {
            Optional<Empleado> empleadoOpt = empleadoService.buscarId(deliveryDTO.getIdEmpleado());
            if (empleadoOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Empleado no encontrado con ID: " + deliveryDTO.getIdEmpleado());
            }
            delivery.setEmpleado(empleadoOpt.get());
        }

        if (deliveryDTO.getDireccion() != null) {
            delivery.setDireccion(deliveryDTO.getDireccion());
        }

        if (deliveryDTO.getEstado() != null) {
            try {
                EstadoDelivery estadoEnum = EstadoDelivery.fromCodigo(deliveryDTO.getEstado());
                delivery.setEstado(estadoEnum);
            } catch (IllegalArgumentException ex) {
                return ResponseEntity.badRequest().body("Estado inválido: " + deliveryDTO.getEstado());
            }
        }

        if (deliveryDTO.getFechaEntrega() != null) {
            delivery.setFechaEntrega(deliveryDTO.getFechaEntrega());
        }

        if (deliveryDTO.getFechaEnvio() != null) {
            delivery.setFechaEnvio(deliveryDTO.getFechaEnvio());
        }

        if (deliveryDTO.getCostoEnvio() != null) {
            delivery.setCostoEnvio(deliveryDTO.getCostoEnvio());
        }

        if (deliveryDTO.getObservaciones() != null) {
            delivery.setObservaciones(deliveryDTO.getObservaciones());
        }

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
        Optional<Delivery> deliveryExistente = deliveryService.findById(id);
        if (deliveryExistente.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        deliveryService.deleteById(id);
        return ResponseEntity.ok("Delivery eliminado exitosamente");

    } catch (Exception e) {
        return ResponseEntity.internalServerError()
                .body("Error al eliminar delivery: " + e.getMessage());
    }
}
}