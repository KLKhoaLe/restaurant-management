package org.example.restaurant_management.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.example.restaurant_management.entity.User;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RestaurantResponse {
    private Long id;

    private String name;

    private String address;

    private String phone;

    private String inviteCode;

    private LocalDateTime createdAt;

    private User owner;
}
