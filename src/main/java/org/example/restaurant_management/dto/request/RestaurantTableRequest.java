package org.example.restaurant_management.dto.request;

import lombok.*;
import org.example.restaurant_management.entity.RestaurantTable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestaurantTableRequest {
    private String tableNumber;

    private Integer capacity;

    private RestaurantTable.TableStatus status;

}
