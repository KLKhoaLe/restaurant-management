package org.example.restaurant_management.configuration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AccessTokenPrincipal {
    private Long userId;
    private String username;
}