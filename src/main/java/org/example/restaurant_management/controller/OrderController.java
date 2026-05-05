package org.example.restaurant_management.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.restaurant_management.configuration.ContextTokenPrincipal;
import org.example.restaurant_management.dto.request.AddOrderItemsRequest;
import org.example.restaurant_management.dto.request.CreateOrderRequest;
import org.example.restaurant_management.dto.request.PaymentRequest;
import org.example.restaurant_management.dto.request.UpdateOrderItemRequest;
import org.example.restaurant_management.dto.response.ApiResponse;
import org.example.restaurant_management.dto.response.OrderResponse;
import org.example.restaurant_management.dto.response.PaymentResponse;
import org.example.restaurant_management.service.OrderService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderController {

    OrderService orderService;

    // ============================================================ //
    //  ORDER LIFECYCLE
    // ============================================================ //

    @PostMapping("/{restaurantId}/orders")
    @PreAuthorize("#restaurantId == authentication.principal.restaurantId")
    ApiResponse<OrderResponse> createOrder(
            @PathVariable Long restaurantId,
            @RequestBody CreateOrderRequest request,
            @AuthenticationPrincipal ContextTokenPrincipal principal) {
        return ApiResponse.<OrderResponse>builder()
                .result(orderService.createOrder(restaurantId, principal.getUserId(), request))
                .build();
    }

    @GetMapping("/{restaurantId}/orders/{orderId}")
    @PreAuthorize("#restaurantId == authentication.principal.restaurantId")
    ApiResponse<OrderResponse> getOrder(
            @PathVariable Long restaurantId,
            @PathVariable Long orderId) {
        return ApiResponse.<OrderResponse>builder()
                .result(orderService.getOrderById(restaurantId, orderId))
                .build();
    }

    @GetMapping("/{restaurantId}/tables/{tableId}/active-order")
    @PreAuthorize("#restaurantId == authentication.principal.restaurantId")
    ApiResponse<OrderResponse> getActiveOrderByTable(
            @PathVariable Long restaurantId,
            @PathVariable Long tableId) {
        return ApiResponse.<OrderResponse>builder()
                .result(orderService.getActiveOrderByTable(restaurantId, tableId))
                .build();
    }

    @GetMapping("/{restaurantId}/orders")
    @PreAuthorize("#restaurantId == authentication.principal.restaurantId")
    ApiResponse<List<OrderResponse>> getOrders(@PathVariable Long restaurantId) {
        return ApiResponse.<List<OrderResponse>>builder()
                .result(orderService.getOrdersByRestaurant(restaurantId))
                .build();
    }

    @PostMapping("/{restaurantId}/orders/{orderId}/cancel")
    @PreAuthorize("#restaurantId == authentication.principal.restaurantId")
    ApiResponse<Void> cancelOrder(
            @PathVariable Long restaurantId,
            @PathVariable Long orderId) {
        orderService.cancelOrder(restaurantId, orderId);
        return ApiResponse.<Void>builder()
                .message("Order cancelled successfully")
                .build();
    }

    @PostMapping("/{restaurantId}/orders/{orderId}/start-serving")
    @PreAuthorize("#restaurantId == authentication.principal.restaurantId")
    ApiResponse<OrderResponse> startServing(
            @PathVariable Long restaurantId,
            @PathVariable Long orderId) {
        return ApiResponse.<OrderResponse>builder()
                .result(orderService.startServing(restaurantId, orderId))
                .build();
    }

    @PostMapping("/{restaurantId}/orders/{orderId}/mark-served")
    @PreAuthorize("#restaurantId == authentication.principal.restaurantId")
    ApiResponse<OrderResponse> markServed(
            @PathVariable Long restaurantId,
            @PathVariable Long orderId) {
        return ApiResponse.<OrderResponse>builder()
                .result(orderService.markServed(restaurantId, orderId))
                .build();
    }

    // ============================================================ //
    //  ORDER ITEMS
    // ============================================================ //

    @PostMapping("/{restaurantId}/orders/{orderId}/items")
    @PreAuthorize("#restaurantId == authentication.principal.restaurantId")
    ApiResponse<OrderResponse> addItems(
            @PathVariable Long restaurantId,
            @PathVariable Long orderId,
            @RequestBody AddOrderItemsRequest request) {
        return ApiResponse.<OrderResponse>builder()
                .result(orderService.addItems(restaurantId, orderId, request))
                .build();
    }

    @PutMapping("/{restaurantId}/orders/{orderId}/items/{itemId}")
    @PreAuthorize("#restaurantId == authentication.principal.restaurantId")
    ApiResponse<OrderResponse> updateItem(
            @PathVariable Long restaurantId,
            @PathVariable Long orderId,
            @PathVariable Long itemId,
            @RequestBody UpdateOrderItemRequest request) {
        return ApiResponse.<OrderResponse>builder()
                .result(orderService.updateItem(restaurantId, orderId, itemId, request))
                .build();
    }

    @DeleteMapping("/{restaurantId}/orders/{orderId}/items/{itemId}")
    @PreAuthorize("#restaurantId == authentication.principal.restaurantId")
    ApiResponse<OrderResponse> removeItem(
            @PathVariable Long restaurantId,
            @PathVariable Long orderId,
            @PathVariable Long itemId) {
        return ApiResponse.<OrderResponse>builder()
                .result(orderService.removeItem(restaurantId, orderId, itemId))
                .build();
    }

    // ============================================================ //
    //  PAYMENT
    // ============================================================ //

    @PostMapping("/{restaurantId}/orders/{orderId}/payment")
    @PreAuthorize("#restaurantId == authentication.principal.restaurantId")
    ApiResponse<PaymentResponse> payOrder(
            @PathVariable Long restaurantId,
            @PathVariable Long orderId,
            @RequestBody PaymentRequest request) {
        return ApiResponse.<PaymentResponse>builder()
                .result(orderService.payOrder(restaurantId, orderId, request))
                .build();
    }
}