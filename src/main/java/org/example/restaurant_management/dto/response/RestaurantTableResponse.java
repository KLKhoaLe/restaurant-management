package org.example.restaurant_management.dto.response;

import jakarta.persistence.*;
import lombok.*;
import org.example.restaurant_management.entity.Restaurant;
import org.example.restaurant_management.entity.RestaurantTable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestaurantTableResponse {
    private Long id;

    private String tableNumber;

    private Integer capacity;

    private RestaurantTable.TableStatus status;

    private Long restaurantId;
}
