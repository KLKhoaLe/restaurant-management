// UpdateOrderItemRequest.java
package org.example.restaurant_management.dto.request;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UpdateOrderItemRequest {
    private Integer quantity;
    private String note;
}