// OrderRepository.java
package org.example.restaurant_management.repository;

import org.example.restaurant_management.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    boolean existsByTable_IdAndStatus(Long tableId, Order.OrderStatus status);
    Optional<Order> findByTable_IdAndStatus(Long tableId, Order.OrderStatus status);
    List<Order> findByRestaurant_Id(Long restaurantId);
    List<Order> findByRestaurant_IdAndStatus(Long restaurantId, Order.OrderStatus status);

    boolean existsByTable_IdAndStatusIn(Long tableId, Collection<Order.OrderStatus> statuses);
    Optional<Order> findByTable_IdAndStatusIn(Long tableId, Collection<Order.OrderStatus> statuses);
}