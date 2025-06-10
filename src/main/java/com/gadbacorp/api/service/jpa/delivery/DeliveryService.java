package com.gadbacorp.api.service.jpa.delivery;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gadbacorp.api.entity.delivery.Delivery;
import com.gadbacorp.api.repository.delivery.DeliveryRepository;
import com.gadbacorp.api.service.delivery.IDeliveryService;

@Service
public class DeliveryService implements IDeliveryService {

    @Autowired
    private DeliveryRepository deliveryRepository;

    @Override
    public List<Delivery> buscarTodos() {
        return deliveryRepository.findAll();
    }

    @Override
    public Delivery guardar(Delivery vehiculo) {
        return deliveryRepository.save(vehiculo);
    }

    @Override
    public Delivery modificar(Delivery vehiculo) {
        return deliveryRepository.save(vehiculo);
    }

    @Override
    public Optional<Delivery> buscarId(Integer id) {
        return deliveryRepository.findById(id);
    }

    @Override
    public void eliminar(Integer id) {
        deliveryRepository.deleteById(id);
    }
}
