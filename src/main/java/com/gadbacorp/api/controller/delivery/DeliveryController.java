package com.gadbacorp.api.controller.delivery;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.gadbacorp.api.entity.delivery.Delivery;
import com.gadbacorp.api.entity.delivery.DeliveryDTO;
import com.gadbacorp.api.entity.delivery.Vehiculo;
import com.gadbacorp.api.entity.ventas.Ventas;
import com.gadbacorp.api.repository.delivery.DeliveryRepository;
import com.gadbacorp.api.repository.delivery.VehiculoRepository;
import com.gadbacorp.api.repository.ventas.VentasRepository;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/minimarket")
public class DeliveryController {

    @Autowired
    private DeliveryRepository deliveryRepo;

    @Autowired
    private VentasRepository ventasRepo;

    @Autowired
    private VehiculoRepository vehiculoRepo;

    private DeliveryDTO toDTO(Delivery d) {
        return new DeliveryDTO(
            d.getIddelivery(),
            d.getDireccion(),
            d.getFechaEnvio(),
            d.getFechaEntrega(),
            d.getEncargado(),
            d.getCostoEnvio(),
            d.getObservaciones(),
            d.getEstado(),
            d.getVenta(), 
            d.getVehiculo().getIdvehiculo()
        );
    }

  private Delivery toEntity(DeliveryDTO dto) {
    Delivery d = new Delivery();
    if (dto.getIddelivery() != null) d.setIddelivery(dto.getIddelivery());
    d.setDireccion(dto.getDireccion());
    d.setFechaEnvio(dto.getFechaEnvio());
    d.setFechaEntrega(dto.getFechaEntrega());
    d.setEncargado(dto.getEncargado());
    d.setCostoEnvio(dto.getCostoEnvio());
    d.setObservaciones(dto.getObservaciones());
    if (dto.getEstado() != null) d.setEstado(dto.getEstado());

    if (dto.getVenta() != null && dto.getVenta().getIdVenta() != null) {
        Ventas venta = ventasRepo.findById(dto.getVenta().getIdVenta())
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Venta no encontrada id=" + dto.getVenta().getIdVenta()));
        d.setVenta(venta);
    }

    Vehiculo veh = vehiculoRepo.findById(dto.getIdvehiculo())
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.BAD_REQUEST, "Vehículo no encontrado id=" + dto.getIdvehiculo()));
    d.setVehiculo(veh);

    return d;
}

    @GetMapping("/delivery")
    public List<DeliveryDTO> listar() {
        return deliveryRepo.findAll().stream().map(this::toDTO).toList();
    }

    @GetMapping("/delivery/{id}")
    public DeliveryDTO obtener(@PathVariable Integer id) {
        return deliveryRepo.findById(id)
            .map(this::toDTO)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Delivery no encontrado id=" + id));
    }

    @PostMapping("/delivery")
    public ResponseEntity<DeliveryDTO> crear(@RequestBody DeliveryDTO dto) {
        Delivery nuevo = toEntity(dto);
        Delivery saved = deliveryRepo.save(nuevo);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(saved));
    }

 
    @DeleteMapping("/delivery/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Integer id) {
        deliveryRepo.deleteById(id);
        return ResponseEntity.ok("Delivery eliminado correctamente");
    }
}