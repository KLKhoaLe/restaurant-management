package org.example.restaurant_management.mapper;

import org.example.restaurant_management.dto.request.MenuCategoryRequest;
import org.example.restaurant_management.dto.request.MenuItemRequest;
import org.example.restaurant_management.dto.response.MenuCategoryResponse;
import org.example.restaurant_management.dto.response.MenuItemResponse;
import org.example.restaurant_management.entity.MenuCategory;
import org.example.restaurant_management.entity.MenuItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface MenuMapper {
    //Menu category
    @Mapping(target = "restaurant", ignore = true)
    public MenuCategory toMenuCategory(MenuCategoryRequest menuCategoryRequest);

    @Mapping(source = "restaurant.id", target = "restaurantId")
    public MenuCategoryResponse toMenuCategoryResponse(MenuCategory menuCategory);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "restaurant", ignore = true)
    void updateMenuCategory(@MappingTarget MenuCategory category, MenuCategoryRequest request);

    //MenuItem
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "restaurant", ignore = true)
    public MenuItem toMenuItem(MenuItemRequest menuItemRequest);

    @Mapping(source = "category.id", target = "menuCategoryId")
    @Mapping(source = "restaurant.id", target = "restaurantId")
    public MenuItemResponse toMenuItemResponse(MenuItem menuItem);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "restaurant", ignore = true)
    @Mapping(target = "category", ignore = true)
    void updateMenuItem(@MappingTarget MenuItem item, MenuItemRequest request);
}
