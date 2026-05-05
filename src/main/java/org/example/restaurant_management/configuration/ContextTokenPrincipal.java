package org.example.restaurant_management.configuration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ContextTokenPrincipal {
    private Long userId;
    private Long restaurantId;
    private String role;
}