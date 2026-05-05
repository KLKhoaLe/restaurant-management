package org.example.restaurant_management.service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.SignedJWT;
import org.example.restaurant_management.dto.request.IntrospectRequest;
import org.example.restaurant_management.dto.request.LoginRequest;
import org.example.restaurant_management.dto.response.IntrospectResponse;
import org.example.restaurant_management.dto.response.LoginResponse;
import org.example.restaurant_management.dto.response.RestaurantSessionResponse;
import org.example.restaurant_management.entity.User;

import java.text.ParseException;

public interface AuthenticationService {
    LoginResponse login(LoginRequest request);
    RestaurantSessionResponse createRestaurantSession(Long userId, Long restaurantId);
    IntrospectResponse introspect(IntrospectRequest request);
    void logout(String accessToken) throws JOSEException, ParseException;
    String generateAccessToken(User user);
    String generateContextToken(Long userId, Long restaurantId, String role);
    SignedJWT verifyToken(String token, String expectedType) throws JOSEException, ParseException;
}
