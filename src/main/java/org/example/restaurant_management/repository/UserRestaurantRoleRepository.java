package org.example.restaurant_management.repository;


import org.example.restaurant_management.entity.UserRestaurantRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRestaurantRoleRepository extends JpaRepository<UserRestaurantRole, Long> {

    Optional<UserRestaurantRole> findByUser_IdAndRestaurant_IdAndStatus(
            Long userId, Long restaurantId, UserRestaurantRole.MemberStatus status);

    List<UserRestaurantRole> findAllByUser_IdAndStatus(
            Long userId, UserRestaurantRole.MemberStatus status);

    @Query("SELECT urr FROM UserRestaurantRole urr " +
            "JOIN FETCH urr.restaurant " +
            "WHERE urr.user.id = :userId AND urr.status = :status")
    List<UserRestaurantRole> findActiveMembershipsWithRestaurant(
            @Param("userId") Long userId,
            @Param("status") UserRestaurantRole.MemberStatus status);

    Optional<UserRestaurantRole> findByUser_IdAndRestaurant_Id(Long userId, Long restaurantId);

    @Query("SELECT urr FROM UserRestaurantRole urr " +
            "JOIN FETCH urr.user " +
            "WHERE urr.restaurant.id = :restaurantId")
    List<UserRestaurantRole> findAllByRestaurantIdWithUser(@Param("restaurantId") Long restaurantId);
}
