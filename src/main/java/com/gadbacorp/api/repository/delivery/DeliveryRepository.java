package com.gadbacorp.api.repository.delivery;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gadbacorp.api.entity.delivery.Delivery;

public interface DeliveryRepository extends JpaRepository<Delivery, Integer> {

}
