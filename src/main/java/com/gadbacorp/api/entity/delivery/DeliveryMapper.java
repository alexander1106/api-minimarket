package com.gadbacorp.api.entity.delivery;

public class DeliveryMapper {
    public static DeliveryDTO toDTO(Delivery delivery) {
    if (delivery == null) return null;

    DeliveryDTO dto = new DeliveryDTO();
    dto.setId(delivery.getId());
    dto.setDireccion(delivery.getDireccion());
    dto.setFechaEntrega(delivery.getFechaEntrega());
    dto.setFechaEnvio(delivery.getFechaEnvio());
    dto.setCostoEnvio(delivery.getCostoEnvio());
    dto.setObservaciones(delivery.getObservaciones());

    if (delivery.getEstado() != null) {
        dto.setEstado(delivery.getEstado().ordinal());
    }

    if (delivery.getVenta() != null) {
        dto.setIdVenta(Long.valueOf(delivery.getVenta().getIdVenta()));
    }

    if (delivery.getVehiculo() != null) {
        dto.setIdVehiculo(delivery.getVehiculo().getId());
    }

    if (delivery.getEmpleado() != null) {
        dto.setIdEmpleado(delivery.getEmpleado().getIdEmpleado());
    }

    return dto;
}

public static Delivery toEntity(DeliveryDTO dto) {
    if (dto == null) return null;

    Delivery delivery = new Delivery();
    delivery.setId(dto.getId());
    delivery.setDireccion(dto.getDireccion());
    delivery.setFechaEntrega(dto.getFechaEntrega());
    delivery.setFechaEnvio(dto.getFechaEnvio());
    delivery.setCostoEnvio(dto.getCostoEnvio());
    delivery.setObservaciones(dto.getObservaciones());

    if (dto.getEstado() != null) {
        EstadoDelivery[] estados = EstadoDelivery.values();
        if (dto.getEstado() >= 0 && dto.getEstado() < estados.length) {
            delivery.setEstado(estados[dto.getEstado()]);
        }
    }

    // Venta, Vehiculo y Empleado se asignan en el controlador o servicio

    return delivery;
}
}