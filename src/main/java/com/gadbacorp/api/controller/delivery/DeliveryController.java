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

import io.swagger.v3.oas.annotations.tags.Tag;
@Tag(name = "Delivery - Delivery")
@RestController
@RequestMapping("/api/minimarket")
public class DeliveryController {

    @Autowired
    private DeliveryRepository deliveryRepo;

    @Autowired
    private VentasRepository ventasRepo;

    @GetMapping("/delivery")
    public List<Delivery> listar() {
        return deliveryRepo.findAll();
    }

    @GetMapping("/delivery/{id}")
    public Optional<Delivery> obtener(@PathVariable Integer id) {
        return deliveryRepo.findById(id);
    }

    @PostMapping("/delivery")
    public ResponseEntity<?> crear(@RequestBody DeliveryDTO dto) {
    Ventas venta = ventasRepo.findById(dto.getIdVenta())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Venta no encontrada con ID: " + dto.getIdVenta()));

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

    Delivery saved = deliveryRepo.save(nuevo);

    return ResponseEntity.status(HttpStatus.CREATED).body(saved);
}
@PutMapping("/delivery/{id}")
public ResponseEntity<?> actualizar(@PathVariable Integer id, @RequestBody DeliveryDTO dto) {
    Delivery existente = deliveryRepo.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Delivery no encontrado con ID: " + id));

    if (dto.getIdVenta() != null) {
        Ventas venta = ventasRepo.findById(dto.getIdVenta())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Venta no encontrada con ID: " + dto.getIdVenta()));
        existente.setVenta(venta);
    }

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

    @DeleteMapping("/delivery/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Integer id) {
        deliveryRepo.deleteById(id);
        return ResponseEntity.ok("Delivery eliminado correctamente");
    }
}
