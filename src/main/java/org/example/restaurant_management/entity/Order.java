package org.example.restaurant_management.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double totalAmount;

    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "table_id")
    private RestaurantTable table;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    public enum OrderStatus {
        OPEN,        // Vừa mở, đang nhận món
        SERVING,     // Bếp đang làm / đang phục vụ
        SERVED,      // Đã phục vụ xong, chờ thanh toán
        PAID,        // Đã thanh toán
        CANCELLED;   // Đã hủy

        /** Order còn đang được xử lý — chưa kết thúc */
        public boolean isActive() {
            return this == OPEN || this == SERVING || this == SERVED;
        }

        /** Order đã đóng (không sửa được nữa) */
        public boolean isClosed() {
            return this == PAID || this == CANCELLED;
        }
    }
}
