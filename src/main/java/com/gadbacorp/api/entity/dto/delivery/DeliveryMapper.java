package com.gadbacorp.api.entity.dto.delivery;

import com.gadbacorp.api.entity.delivery.Delivery;
import com.gadbacorp.api.entity.ventas.Ventas;

public class DeliveryMapper {
public static DeliveryDTO toDTO(Delivery delivery) {
    if (delivery == null) return null;

    DeliveryDTO dto = new DeliveryDTO();
    dto.setId(delivery.getId());
    dto.setDireccion(delivery.getDireccion());
    dto.setEstado(delivery.getEstado());
    dto.setFechaEntrega(delivery.getFechaEntrega());
    dto.setFechaEnvio(delivery.getFechaEnvio());
    dto.setCostoEnvio(delivery.getCostoEnvio());
    dto.setObservaciones(delivery.getObservaciones());

    if (delivery.getVenta() != null && delivery.getVenta().getIdVenta() != null) {
        dto.setIdVenta(delivery.getVenta().getIdVenta().longValue());
    }

    return dto;
}

// De DTO a entidad
public static Delivery toEntity(DeliveryDTO dto) {
    if (dto == null) return null;

    Delivery delivery = new Delivery();
    delivery.setId(dto.getId());
    delivery.setDireccion(dto.getDireccion());
    delivery.setEstado(dto.getEstado());
    delivery.setFechaEntrega(dto.getFechaEntrega());
    delivery.setFechaEnvio(dto.getFechaEnvio());
    delivery.setCostoEnvio(dto.getCostoEnvio());
    delivery.setObservaciones(dto.getObservaciones());

    if (dto.getIdVenta() != null) {
        Ventas venta = new Ventas();
        venta.setIdVenta(dto.getIdVenta().intValue()); // Convertimos Long a Integer
        delivery.setVenta(venta);
    }

    return delivery;
}
}