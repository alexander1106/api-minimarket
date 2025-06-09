package com.gadbacorp.api.gadbacorp.service.delivery.impl;

import com.gadbacorp.api.gadbacorp.entity.delivery.Delivery;
import com.gadbacorp.api.gadbacorp.repository.delivery.DeliveryRepository;
import com.gadbacorp.api.gadbacorp.service.delivery.IDeliveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DeliveryServiceImpl implements IDeliveryService {

    @Autowired
    private DeliveryRepository deliveryRepository;

    @Override
    public List<Delivery> findAll() {
        return deliveryRepository.findAll();
    }

    @Override
    public Optional<Delivery> findById(Long id) {
        return deliveryRepository.findById(id);
    }

    @Override
    public Delivery save(Delivery delivery) {
        return deliveryRepository.save(delivery);
    }

    @Override
    public void deleteById(Long id) {
        deliveryRepository.deleteById(id);
    }
}
