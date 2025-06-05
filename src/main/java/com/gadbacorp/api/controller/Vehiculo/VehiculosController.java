package com.gadbacorp.api.controller.Vehiculo;

import com.gadbacorp.api.entity.Vehiculo.Vehiculo;
import com.gadbacorp.api.service.Vehiculo.VehiculoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/minimarket/vehiculos")
public class VehiculosController {
    @Autowired
    private VehiculoService vehiculoService;

    @GetMapping
    public List<Vehiculo> getAllVehiculos() {
        return vehiculoService.getAllVehiculos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Vehiculo> getVehiculoById(@PathVariable Long id) {
        return vehiculoService.getVehiculoById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Vehiculo createVehiculo(@RequestBody Vehiculo vehiculo) {
        return vehiculoService.saveVehiculo(vehiculo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Vehiculo> updateVehiculo(@PathVariable Long id, @RequestBody Vehiculo vehiculoDetails) {
        Vehiculo updatedVehiculo = vehiculoService.updateVehiculo(id, vehiculoDetails);
        return ResponseEntity.ok(updatedVehiculo);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVehiculo(@PathVariable Long id) {
        vehiculoService.deleteVehiculo(id);
        return ResponseEntity.noContent().build();
    }

    // Endpoint personalizado
    @GetMapping("/estado/{estado}")
    public List<Vehiculo> getVehiculosByEstado(@PathVariable String estado) {
        return vehiculoService.getVehiculosByEstado(estado);
    }
}
