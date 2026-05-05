package org.example.restaurant_management.repository;

import org.example.restaurant_management.entity.MenuCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenuCategoryRepository extends JpaRepository<MenuCategory, Long> {
    List<MenuCategory> findByRestaurant_Id(Long restaurantId);
}
