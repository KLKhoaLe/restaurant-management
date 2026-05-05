package org.example.restaurant_management.dto.response;

import lombok.*;
import org.example.restaurant_management.entity.UserRestaurantRole;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyRestaurantResponse {
    private Long id;
    private String name;
    private String address;
    private UserRestaurantRole.Role role;
}
