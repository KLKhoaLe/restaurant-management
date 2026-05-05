// OrderResponse.java
package org.example.restaurant_management.dto.response;

import lombok.*;
import org.example.restaurant_management.entity.Order;

import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OrderResponse {
    private Long id;
    private Double totalAmount;
    private LocalDateTime createdAt;
    private Order.OrderStatus status;

    private Long tableId;
    private String tableNumber;

    private Long createdById;
    private String createdByName;

    private List<OrderItemResponse> items;
}