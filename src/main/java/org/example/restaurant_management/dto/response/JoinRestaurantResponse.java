package org.example.restaurant_management.dto.response;

import lombok.*;
import org.example.restaurant_management.entity.UserRestaurantRole;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class JoinRestaurantResponse {
    private Long restaurantId;
    private String restaurantName;
    private UserRestaurantRole.Role role;
    private String message;
}