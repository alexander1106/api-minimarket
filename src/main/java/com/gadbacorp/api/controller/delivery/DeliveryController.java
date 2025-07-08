package com.gadbacorp.api.controller.delivery;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.gadbacorp.api.entity.delivery.Delivery;
import com.gadbacorp.api.entity.delivery.DeliveryDTO;
import com.gadbacorp.api.entity.ventas.Ventas;
import com.gadbacorp.api.repository.delivery.DeliveryRepository;
import com.gadbacorp.api.repository.ventas.VentasRepository;
import org.springframework.web.server.ResponseStatusException;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/minimarket")
public class DeliveryController {

    @Autowired
    private DeliveryRepository deliveryRepo;

    @Autowired
    private VentasRepository ventasRepo;


    // Listar todos los deliveries
    @GetMapping("/delivery")
    public List<Delivery> listar() {
        return deliveryRepo.findAll();
    }

    // Obtener un delivery por ID
    @GetMapping("/delivery/{id}")
    public Optional<Delivery> obtener(@PathVariable Integer id) {
        return deliveryRepo.findById(id);
    }

    @PostMapping("/delivery")
    public ResponseEntity<?> crear(@RequestBody DeliveryDTO dto) {
    // Validar que la venta exista
    Ventas venta = ventasRepo.findById(dto.getIdVenta())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Venta no encontrada con ID: " + dto.getIdVenta()));

    // Crear y poblar el entity Delivery
    Delivery nuevo = new Delivery();
    nuevo.setDireccion(dto.getDireccion());
        nuevo.setEstadoDelivery("Pendiente");

    nuevo.setFechaEnvio(dto.getFechaEnvio());
    nuevo.setFechaEntrega(dto.getFechaEntrega());
    nuevo.setEncargado(dto.getEncargado());
    nuevo.setCostoEnvio(dto.getCostoEnvio());
    nuevo.setObservaciones(dto.getObservaciones());
    nuevo.setEstado(dto.getEstado());
    nuevo.setVenta(venta); // Relación con la venta

    // Guardar el delivery
    Delivery saved = deliveryRepo.save(nuevo);

    return ResponseEntity.status(HttpStatus.CREATED).body(saved);
}
@PutMapping("/delivery/{id}")
public ResponseEntity<?> actualizar(@PathVariable Integer id, @RequestBody DeliveryDTO dto) {
    Delivery existente = deliveryRepo.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Delivery no encontrado con ID: " + id));

    // Si idVenta fue proporcionado, validamos y actualizamos la relación con Ventas
    if (dto.getIdVenta() != null) {
        Ventas venta = ventasRepo.findById(dto.getIdVenta())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Venta no encontrada con ID: " + dto.getIdVenta()));
        existente.setVenta(venta);
    }

    // Actualizar campos
    existente.setDireccion(dto.getDireccion());
    existente.setFechaEnvio(dto.getFechaEnvio());
    existente.setFechaEntrega(dto.getFechaEntrega());
        existente.setEstadoDelivery(dto.getEstadoDelivery());

    existente.setEncargado(dto.getEncargado());
    existente.setCostoEnvio(dto.getCostoEnvio());
    existente.setObservaciones(dto.getObservaciones());
    existente.setEstado(dto.getEstado());

    Delivery actualizado = deliveryRepo.save(existente);
    return ResponseEntity.ok(actualizado);
}


    // Eliminar un delivery por ID
    @DeleteMapping("/delivery/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Integer id) {
        deliveryRepo.deleteById(id);
        return ResponseEntity.ok("Delivery eliminado correctamente");
    }
}
