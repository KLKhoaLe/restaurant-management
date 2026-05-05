package org.example.restaurant_management.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuCategoryResponse {
    private Long id;

    private String name;

    private Long restaurantId;
}
