package org.example.restaurant_management.service;

import org.example.restaurant_management.dto.request.AddOrderItemsRequest;
import org.example.restaurant_management.dto.request.CreateOrderRequest;
import org.example.restaurant_management.dto.request.PaymentRequest;
import org.example.restaurant_management.dto.request.UpdateOrderItemRequest;
import org.example.restaurant_management.dto.response.OrderResponse;
import org.example.restaurant_management.dto.response.PaymentResponse;

import java.util.List;

public interface OrderService {

    // Order lifecycle
    OrderResponse createOrder(Long restaurantId, Long userId, CreateOrderRequest request);
    OrderResponse getOrderById(Long restaurantId, Long orderId);
    OrderResponse getActiveOrderByTable(Long restaurantId, Long tableId);
    List<OrderResponse> getOrdersByRestaurant(Long restaurantId);
    void cancelOrder(Long restaurantId, Long orderId);
    OrderResponse startServing(Long restaurantId, Long orderId);
    OrderResponse markServed(Long restaurantId, Long orderId);

    // Order items
    OrderResponse addItems(Long restaurantId, Long orderId, AddOrderItemsRequest request);
    OrderResponse updateItem(Long restaurantId, Long orderId, Long itemId, UpdateOrderItemRequest request);
    OrderResponse removeItem(Long restaurantId, Long orderId, Long itemId);

    // Payment
    PaymentResponse payOrder(Long restaurantId, Long orderId, PaymentRequest request);
}