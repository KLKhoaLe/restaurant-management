package org.example.restaurant_management.controller;

import org.example.restaurant_management.dto.request.RestaurantTableRequest;
import org.example.restaurant_management.dto.response.ApiResponse;
import org.example.restaurant_management.dto.response.RestaurantTableResponse;
import org.example.restaurant_management.service.RestaurantTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class RestaurantTableController {
    @Autowired
    private RestaurantTableService restaurantTableService;

    @PostMapping("/{restaurantId}/table")
    @PreAuthorize("hasRole('ADMIN') and #restaurantId == authentication.principal.restaurantId")
    ApiResponse<RestaurantTableResponse> createTable(
            @PathVariable Long restaurantId,
            @RequestBody RestaurantTableRequest restaurantTableRequest) {
        return ApiResponse.<RestaurantTableResponse>builder()
                .result(restaurantTableService.createRestaurantTable(restaurantId, restaurantTableRequest))
                .build();
    }

    @PutMapping("/{restaurantId}/table/{tableId}")
    @PreAuthorize("hasRole('ADMIN') and #restaurantId == authentication.principal.restaurantId")
    ApiResponse<RestaurantTableResponse> updateTable(
            @PathVariable Long restaurantId,
            @PathVariable Long tableId,
            @RequestBody RestaurantTableRequest restaurantTableRequest) {
        return ApiResponse.<RestaurantTableResponse>builder()
                .result(restaurantTableService.updateRestaurantTable(restaurantId, tableId, restaurantTableRequest))
                .build();
    }

    @DeleteMapping("/{restaurantId}/table/{tableId}")
    @PreAuthorize("hasRole('ADMIN') and #restaurantId == authentication.principal.restaurantId")
    ApiResponse<Void> deleteTable(
            @PathVariable Long restaurantId,
            @PathVariable Long tableId) {
        restaurantTableService.deleteRestaurantTable(restaurantId, tableId);
        return ApiResponse.<Void>builder()
                .message("Table deleted successfully")
                .build();
    }

    @GetMapping("/{restaurantId}/table")
    @PreAuthorize("#restaurantId == authentication.principal.restaurantId")
    ApiResponse<List<RestaurantTableResponse>> getTables(
            @PathVariable Long restaurantId) {
        return ApiResponse.<List<RestaurantTableResponse>>builder()
                .result(restaurantTableService.getTablesByRestaurant(restaurantId))
                .build();
    }

    @GetMapping("/{restaurantId}/table/{tableId}")
    @PreAuthorize("#restaurantId == authentication.principal.restaurantId")
    ApiResponse<RestaurantTableResponse> getTable(
            @PathVariable Long restaurantId,
            @PathVariable Long tableId) {
        return ApiResponse.<RestaurantTableResponse>builder()
                .result(restaurantTableService.getTableById(restaurantId, tableId))
                .build();
    }
}
