package com.gadbacorp.api.gadbacorp.repository.delivery;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.gadbacorp.api.gadbacorp.entity.delivery.Delivery;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
}
