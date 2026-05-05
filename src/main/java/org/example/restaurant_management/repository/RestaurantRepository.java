package org.example.restaurant_management.repository;

import org.example.restaurant_management.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    Boolean existsByOwner_Id(Long userId);
    Boolean existsByInviteCode(String inviteCode);
    Optional<Restaurant> findByInviteCode(String inviteCode);
}
