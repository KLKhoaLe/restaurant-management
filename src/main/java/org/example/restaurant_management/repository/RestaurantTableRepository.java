package org.example.restaurant_management.repository;

import org.example.restaurant_management.entity.RestaurantTable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RestaurantTableRepository extends JpaRepository<RestaurantTable, Long> {
    List<RestaurantTable> findByRestaurant_Id(Long restaurantId);
}
