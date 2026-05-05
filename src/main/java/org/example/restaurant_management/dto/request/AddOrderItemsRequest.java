// AddOrderItemsRequest.java
package org.example.restaurant_management.dto.request;

import lombok.*;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AddOrderItemsRequest {
    private List<OrderItemLine> items;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class OrderItemLine {
        private Long menuItemId;
        private Integer quantity;
        private String note;
    }
}