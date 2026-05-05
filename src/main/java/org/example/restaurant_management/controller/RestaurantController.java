package org.example.restaurant_management.controller;

import org.example.restaurant_management.configuration.AccessTokenPrincipal;
import org.example.restaurant_management.dto.request.JoinRestaurantRequest;
import org.example.restaurant_management.dto.request.RestaurantRequest;
import org.example.restaurant_management.dto.response.*;
import org.example.restaurant_management.entity.Restaurant;
import org.example.restaurant_management.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController()
@RequestMapping("/api")
public class RestaurantController {
    @Autowired
    private RestaurantService restaurantService;

    @PostMapping("/restaurants")
    ApiResponse<RestaurantResponse> createRestaurant(@RequestBody RestaurantRequest restaurantRequest,
                                                     @AuthenticationPrincipal AccessTokenPrincipal accessTokenPrincipal)
    {
        Long userId = accessTokenPrincipal.getUserId();
        return ApiResponse.<RestaurantResponse>builder()
                .result(
                        restaurantService.createRestaurant(restaurantRequest, userId)
                )
                .build();
    }

    @PutMapping("/restaurants/{restaurantId}")
    @PreAuthorize("hasRole('ADMIN') and #restaurantId == authentication.principal.restaurantId")
    ApiResponse<RestaurantResponse> updateRestaurant(
            @PathVariable Long restaurantId,
            @RequestBody RestaurantRequest restaurantRequest) {

        return ApiResponse.<RestaurantResponse>builder()
                .result(
                        restaurantService.updateRestaurant(restaurantId, restaurantRequest)
                )
                .build();
    }

    @GetMapping("/restaurants/my-list")
    ApiResponse<List<MyRestaurantResponse>> getMyRestaurants(
            @AuthenticationPrincipal AccessTokenPrincipal accessTokenPrincipal) {

        Long userId = accessTokenPrincipal.getUserId();
        return ApiResponse.<List<MyRestaurantResponse>>builder()
                .result(restaurantService.getMyRestaurants(userId))
                .build();
    }

    @PostMapping("/restaurants/join")
    ApiResponse<JoinRestaurantResponse> joinRestaurant(
            @RequestBody JoinRestaurantRequest request,
            @AuthenticationPrincipal AccessTokenPrincipal accessTokenPrincipal) {

        Long userId = accessTokenPrincipal.getUserId();
        return ApiResponse.<JoinRestaurantResponse>builder()
                .result(restaurantService.joinRestaurant(userId, request))
                .build();
    }

    @GetMapping("/restaurants/{restaurantId}/invite-code")
    @PreAuthorize("hasRole('ADMIN') and #restaurantId == authentication.principal.restaurantId")
    ApiResponse<InviteCodeResponse> getInviteCode(@PathVariable Long restaurantId) {
        return ApiResponse.<InviteCodeResponse>builder()
                .result(restaurantService.getInviteCode(restaurantId))
                .build();
    }

    @PostMapping("/restaurants/{restaurantId}/invite-code/regenerate")
    @PreAuthorize("hasRole('ADMIN') and #restaurantId == authentication.principal.restaurantId")
    ApiResponse<InviteCodeResponse> regenerateInviteCode(@PathVariable Long restaurantId) {
        return ApiResponse.<InviteCodeResponse>builder()
                .result(restaurantService.regenerateInviteCode(restaurantId))
                .build();
    }
}
