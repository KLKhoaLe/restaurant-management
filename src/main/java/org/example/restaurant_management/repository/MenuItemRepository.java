package org.example.restaurant_management.repository;

import org.example.restaurant_management.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
    List<MenuItem> findByRestaurant_Id(Long restaurantId);
    List<MenuItem> findByCategory_Id(Long categoryId);
    boolean existsByCategory_Id(Long categoryId);
}
