package com.gadbacorp.api.service.delivery;

import java.util.List;
import java.util.Optional;

import com.gadbacorp.api.entity.delivery.Delivery;

public interface IDeliveryService {
    
    List<Delivery> buscarTodos();

    Delivery guardar(Delivery delivery);

    Delivery modificar(Delivery delivery);

    Optional<Delivery> buscarId(Integer id);

    void eliminar(Integer id);
}
