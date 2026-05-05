package org.example.restaurant_management.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuItemResponse {
    private Long id;

    private String name;

    private Double price;

    private String description;

    private String imageUrl;

    private Integer status;

    private Long menuCategoryId;

    private Long restaurantId;


}
