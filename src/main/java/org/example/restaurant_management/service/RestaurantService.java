package org.example.restaurant_management.service;

import org.example.restaurant_management.dto.request.JoinRestaurantRequest;
import org.example.restaurant_management.dto.request.RestaurantRequest;
import org.example.restaurant_management.dto.response.InviteCodeResponse;
import org.example.restaurant_management.dto.response.JoinRestaurantResponse;
import org.example.restaurant_management.dto.response.MyRestaurantResponse;
import org.example.restaurant_management.dto.response.RestaurantResponse;

import java.util.List;

public interface RestaurantService {
    RestaurantResponse createRestaurant(RestaurantRequest restaurantRequest, Long userId);

    RestaurantResponse updateRestaurant(Long restaurantId, RestaurantRequest restaurantRequest);
    List<MyRestaurantResponse> getMyRestaurants(Long userId);
    JoinRestaurantResponse joinRestaurant(Long userId, JoinRestaurantRequest request);
    InviteCodeResponse getInviteCode(Long restaurantId);
    InviteCodeResponse regenerateInviteCode(Long restaurantId);
}
