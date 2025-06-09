package com.gadbacorp.api.service.Vehiculo;

import com.gadbacorp.api.entity.delivery.Vehiculo;
import com.gadbacorp.api.repository.Vehiculo.VehiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VehiculoService {
    @Autowired
    private VehiculoRepository vehiculoRepository;

    public List<Vehiculo> getAllVehiculos() {
        return vehiculoRepository.findAll();
    }

    public Optional<Vehiculo> getVehiculoById(Long id) {
        return vehiculoRepository.findById(id);
    }

    public Vehiculo saveVehiculo(Vehiculo vehiculo) {
        return vehiculoRepository.save(vehiculo);
    }

    public void deleteVehiculo(Long id) {
        vehiculoRepository.deleteById(id);
    }

    public Vehiculo updateVehiculo(Long id, Vehiculo vehiculoDetails) {
        Vehiculo vehiculo = vehiculoRepository.findById(id).orElseThrow();
        vehiculo.setPlaca(vehiculoDetails.getPlaca());
        vehiculo.setModelo(vehiculoDetails.getModelo());
        vehiculo.setMarca(vehiculoDetails.getMarca());
        vehiculo.setAnio(vehiculoDetails.getAnio());
        vehiculo.setColor(vehiculoDetails.getColor());
        vehiculo.setCapacidadKg(vehiculoDetails.getCapacidadKg());
        vehiculo.setEstado(vehiculoDetails.getEstado());
        vehiculo.setObservaciones(vehiculoDetails.getObservaciones());
        return vehiculoRepository.save(vehiculo);
    }

    // Métodos personalizados
    public List<Vehiculo> getVehiculosByEstado(String estado) {
        return vehiculoRepository.findByEstado(estado);
    }
}