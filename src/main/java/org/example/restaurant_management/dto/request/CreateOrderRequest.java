// CreateOrderRequest.java
package org.example.restaurant_management.dto.request;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CreateOrderRequest {
    private Long tableId;
}