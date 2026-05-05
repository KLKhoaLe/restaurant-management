package org.example.restaurant_management.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuItemRequest {
    private String name;

    private Double price;

    private String description;

    private String imageUrl;

    private Integer status;

    private Long menuCategoryId;
}
