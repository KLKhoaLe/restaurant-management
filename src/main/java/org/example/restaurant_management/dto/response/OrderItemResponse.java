// OrderItemResponse.java
package org.example.restaurant_management.dto.response;

import lombok.*;
import org.example.restaurant_management.entity.OrderItem;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OrderItemResponse {
    private Long id;
    private Long menuItemId;
    private String menuItemName;
    private Integer quantity;
    private Double price;
    private Double subtotal;
    private String note;
}