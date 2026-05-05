package org.example.restaurant_management.service;

import org.example.restaurant_management.dto.request.RestaurantTableRequest;
import org.example.restaurant_management.dto.response.RestaurantTableResponse;

import java.util.List;

public interface RestaurantTableService {
    RestaurantTableResponse createRestaurantTable(Long restaurantId, RestaurantTableRequest request);
    RestaurantTableResponse updateRestaurantTable(Long restaurantId, Long tableId, RestaurantTableRequest request);
    void deleteRestaurantTable(Long restaurantId, Long tableId);
    List<RestaurantTableResponse> getTablesByRestaurant(Long restaurantId);
    RestaurantTableResponse getTableById(Long restaurantId, Long tableId);
}
