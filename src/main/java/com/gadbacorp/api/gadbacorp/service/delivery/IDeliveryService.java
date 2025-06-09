package com.gadbacorp.api.gadbacorp.service.delivery;

import com.gadbacorp.api.gadbacorp.entity.delivery.Delivery;

import java.util.List;
import java.util.Optional;

public interface IDeliveryService {
    List<Delivery> findAll();
    Optional<Delivery> findById(Long id);
    Delivery save(Delivery delivery);
    void deleteById(Long id);
}
