package org.example.restaurant_management.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RestaurantSessionResponse {
    private String contextToken;
    private String restaurantName;
    private String role;
    private String tokenType;   // "Bearer"
}
