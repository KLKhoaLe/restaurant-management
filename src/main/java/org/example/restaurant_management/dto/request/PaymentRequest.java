// PaymentRequest.java
package org.example.restaurant_management.dto.request;

import lombok.*;
import org.example.restaurant_management.entity.Payment;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PaymentRequest {
    private Payment.PaymentMethod paymentMethod;
    private Double amount;
}