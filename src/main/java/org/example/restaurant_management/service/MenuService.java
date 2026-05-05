package org.example.restaurant_management.service;

import org.example.restaurant_management.dto.request.MenuCategoryRequest;
import org.example.restaurant_management.dto.request.MenuItemRequest;
import org.example.restaurant_management.dto.response.MenuCategoryResponse;
import org.example.restaurant_management.dto.response.MenuItemResponse;

import java.util.List;

public interface MenuService {

    // MenuCategory
    MenuCategoryResponse createCategory(Long restaurantId, MenuCategoryRequest request);
    MenuCategoryResponse updateCategory(Long restaurantId, Long categoryId, MenuCategoryRequest request);
    void deleteCategory(Long restaurantId, Long categoryId);
    List<MenuCategoryResponse> getCategories(Long restaurantId);
    MenuCategoryResponse getCategoryById(Long restaurantId, Long categoryId);

    // MenuItem
    MenuItemResponse createItem(Long restaurantId, MenuItemRequest request);
    MenuItemResponse updateItem(Long restaurantId, Long itemId, MenuItemRequest request);
    void deleteItem(Long restaurantId, Long itemId);
    List<MenuItemResponse> getItems(Long restaurantId);
    List<MenuItemResponse> getItemsByCategory(Long restaurantId, Long categoryId);
    MenuItemResponse getItemById(Long restaurantId, Long itemId);
}
