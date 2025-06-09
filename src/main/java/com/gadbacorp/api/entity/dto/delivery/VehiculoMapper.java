package com.gadbacorp.api.entity.dto.delivery;

import com.gadbacorp.api.entity.delivery.Vehiculo;

public class VehiculoMapper {

    public static VehiculoDTO toDTO(Vehiculo vehiculo) {
        if (vehiculo == null) return null;

        VehiculoDTO dto = new VehiculoDTO();
        dto.setId(vehiculo.getId());
        dto.setPlaca(vehiculo.getPlaca());
        dto.setModelo(vehiculo.getModelo());
        dto.setMarca(vehiculo.getMarca());
        dto.setAnio(vehiculo.getAnio());
        dto.setColor(vehiculo.getColor());
        dto.setCapacidadKg(vehiculo.getCapacidadKg());
        dto.setEstado(vehiculo.getEstado());
        dto.setObservaciones(vehiculo.getObservaciones());
        return dto;
    }

    public static Vehiculo toEntity(VehiculoDTO dto) {
        if (dto == null) return null;

        Vehiculo vehiculo = new Vehiculo();
        vehiculo.setId(dto.getId());
        vehiculo.setPlaca(dto.getPlaca());
        vehiculo.setModelo(dto.getModelo());
        vehiculo.setMarca(dto.getMarca());
        vehiculo.setAnio(dto.getAnio());
        vehiculo.setColor(dto.getColor());
        vehiculo.setCapacidadKg(dto.getCapacidadKg());
        vehiculo.setEstado(dto.getEstado());
        vehiculo.setObservaciones(dto.getObservaciones());
        return vehiculo;
    }
} 
