package org.example.restaurant_management.controller;

import com.nimbusds.jose.JOSEException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.restaurant_management.configuration.AccessTokenPrincipal;
import org.example.restaurant_management.dto.request.IntrospectRequest;
import org.example.restaurant_management.dto.request.LoginRequest;
import org.example.restaurant_management.dto.response.IntrospectResponse;
import org.example.restaurant_management.dto.response.LoginResponse;
import org.example.restaurant_management.dto.response.RestaurantSessionResponse;
import org.example.restaurant_management.service.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    final AuthenticationService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request)
            throws JOSEException, ParseException {
        String token = request.getHeader("Authorization").substring(7);
        authService.logout(token);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/introspect")
    public ResponseEntity<IntrospectResponse> introspect(@RequestBody IntrospectRequest request) {
        return ResponseEntity.ok(authService.introspect(request));
    }

    @PostMapping("/restaurants/{restaurantId}/session")
    public ResponseEntity<RestaurantSessionResponse> createRestaurantSession(
            @PathVariable Long restaurantId,
            @AuthenticationPrincipal AccessTokenPrincipal principal) {

        Long userId = principal.getUserId();
        return ResponseEntity.ok(
                authService.createRestaurantSession(userId, restaurantId)
        );
    }
}
