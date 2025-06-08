package com.gadbacorp.api.repository.delivery;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.gadbacorp.api.entity.delivery.Delivery;
import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

    @EntityGraph(attributePaths = {"vehiculo", "venta"})
    List<Delivery> findAll();
}
