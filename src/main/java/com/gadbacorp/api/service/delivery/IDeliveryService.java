package com.gadbacorp.api.service.delivery;

import com.gadbacorp.api.entity.delivery.Delivery;

import java.util.List;
import java.util.Optional;

public interface IDeliveryService {
    List<Delivery> findAll();
    Optional<Delivery> findById(Long id);
    Delivery save(Delivery delivery);
    void deleteById(Long id);
}
