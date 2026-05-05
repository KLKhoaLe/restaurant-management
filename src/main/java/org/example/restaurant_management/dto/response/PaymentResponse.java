// PaymentResponse.java
package org.example.restaurant_management.dto.response;

import lombok.*;
import org.example.restaurant_management.entity.Payment;

import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PaymentResponse {
    private Long id;
    private Long orderId;
    private Double amount;
    private LocalDateTime paidAt;
    private Payment.PaymentMethod paymentMethod;
}