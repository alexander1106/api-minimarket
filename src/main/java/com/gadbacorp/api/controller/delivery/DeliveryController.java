package com.gadbacorp.api.controller.delivery;

import com.gadbacorp.api.entity.delivery.Delivery;
import com.gadbacorp.api.service.delivery.IDeliveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/minimarket")
public class DeliveryController {

    @Autowired
    private IDeliveryService deliveryService;

    @GetMapping("/delivery")
    public List<Delivery> getAllDeliveries() {
        return deliveryService.findAll();
    }

    @GetMapping("/delivery/{id}")
    public ResponseEntity<Delivery> getDeliveryById(@PathVariable Long id) {
        return deliveryService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/delivery")
    public ResponseEntity<Delivery> createDelivery(@RequestBody Delivery delivery) {
        Delivery created = deliveryService.save(delivery);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/delivery/{id}")
    public ResponseEntity<Delivery> updateDelivery(@PathVariable Long id, @RequestBody Delivery deliveryDetails) {
        Optional<Delivery> deliveryOptional = deliveryService.findById(id);
        if (!deliveryOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Delivery delivery = deliveryOptional.get();
        delivery.setDireccion(deliveryDetails.getDireccion());
        delivery.setEstado(deliveryDetails.getEstado());
        delivery.setFechaEntrega(deliveryDetails.getFechaEntrega());
        delivery.setFechaEnvio(deliveryDetails.getFechaEnvio());
        delivery.setCostoEnvio(deliveryDetails.getCostoEnvio());
        delivery.setObservaciones(deliveryDetails.getObservaciones());
      
        delivery.setVenta(deliveryDetails.getVenta());

        Delivery updated = deliveryService.save(delivery);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/delivery/{id}")
    public ResponseEntity<String> deleteDelivery(@PathVariable Long id) {
        if (!deliveryService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        deliveryService.deleteById(id);
        return ResponseEntity.ok("Delivery eliminado correctamente");
    }
}
