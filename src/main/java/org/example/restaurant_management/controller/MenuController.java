package org.example.restaurant_management.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.restaurant_management.dto.request.MenuCategoryRequest;
import org.example.restaurant_management.dto.request.MenuItemRequest;
import org.example.restaurant_management.dto.response.ApiResponse;
import org.example.restaurant_management.dto.response.MenuCategoryResponse;
import org.example.restaurant_management.dto.response.MenuItemResponse;
import org.example.restaurant_management.service.MenuService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MenuController {

    MenuService menuService;

    // ============================================================ //
    //  MENU CATEGORY
    // ============================================================ //

    @PostMapping("/{restaurantId}/menu/categories")
    @PreAuthorize("hasRole('ADMIN') and #restaurantId == authentication.principal.restaurantId")
    ApiResponse<MenuCategoryResponse> createCategory(
            @PathVariable Long restaurantId,
            @RequestBody MenuCategoryRequest request) {
        return ApiResponse.<MenuCategoryResponse>builder()
                .result(menuService.createCategory(restaurantId, request))
                .build();
    }

    @PutMapping("/{restaurantId}/menu/categories/{categoryId}")
    @PreAuthorize("hasRole('ADMIN') and #restaurantId == authentication.principal.restaurantId")
    ApiResponse<MenuCategoryResponse> updateCategory(
            @PathVariable Long restaurantId,
            @PathVariable Long categoryId,
            @RequestBody MenuCategoryRequest request) {
        return ApiResponse.<MenuCategoryResponse>builder()
                .result(menuService.updateCategory(restaurantId, categoryId, request))
                .build();
    }

    @DeleteMapping("/{restaurantId}/menu/categories/{categoryId}")
    @PreAuthorize("hasRole('ADMIN') and #restaurantId == authentication.principal.restaurantId")
    ApiResponse<Void> deleteCategory(
            @PathVariable Long restaurantId,
            @PathVariable Long categoryId) {
        menuService.deleteCategory(restaurantId, categoryId);
        return ApiResponse.<Void>builder()
                .message("Category deleted successfully")
                .build();
    }

    @GetMapping("/{restaurantId}/menu/categories")
    @PreAuthorize("#restaurantId == authentication.principal.restaurantId")
    ApiResponse<List<MenuCategoryResponse>> getCategories(
            @PathVariable Long restaurantId) {
        return ApiResponse.<List<MenuCategoryResponse>>builder()
                .result(menuService.getCategories(restaurantId))
                .build();
    }

    @GetMapping("/{restaurantId}/menu/categories/{categoryId}")
    @PreAuthorize("#restaurantId == authentication.principal.restaurantId")
    ApiResponse<MenuCategoryResponse> getCategory(
            @PathVariable Long restaurantId,
            @PathVariable Long categoryId) {
        return ApiResponse.<MenuCategoryResponse>builder()
                .result(menuService.getCategoryById(restaurantId, categoryId))
                .build();
    }

    // ============================================================ //
    //  MENU ITEM
    // ============================================================ //

    @PostMapping("/{restaurantId}/menu/items")
    @PreAuthorize("hasRole('ADMIN') and #restaurantId == authentication.principal.restaurantId")
    ApiResponse<MenuItemResponse> createItem(
            @PathVariable Long restaurantId,
            @RequestBody MenuItemRequest request) {
        return ApiResponse.<MenuItemResponse>builder()
                .result(menuService.createItem(restaurantId, request))
                .build();
    }

    @PutMapping("/{restaurantId}/menu/items/{itemId}")
    @PreAuthorize("hasRole('ADMIN') and #restaurantId == authentication.principal.restaurantId")
    ApiResponse<MenuItemResponse> updateItem(
            @PathVariable Long restaurantId,
            @PathVariable Long itemId,
            @RequestBody MenuItemRequest request) {
        return ApiResponse.<MenuItemResponse>builder()
                .result(menuService.updateItem(restaurantId, itemId, request))
                .build();
    }

    @DeleteMapping("/{restaurantId}/menu/items/{itemId}")
    @PreAuthorize("hasRole('ADMIN') and #restaurantId == authentication.principal.restaurantId")
    ApiResponse<Void> deleteItem(
            @PathVariable Long restaurantId,
            @PathVariable Long itemId) {
        menuService.deleteItem(restaurantId, itemId);
        return ApiResponse.<Void>builder()
                .message("Item deleted successfully")
                .build();
    }

    @GetMapping("/{restaurantId}/menu/items")
    @PreAuthorize("#restaurantId == authentication.principal.restaurantId")
    ApiResponse<List<MenuItemResponse>> getItems(
            @PathVariable Long restaurantId) {
        return ApiResponse.<List<MenuItemResponse>>builder()
                .result(menuService.getItems(restaurantId))
                .build();
    }

    @GetMapping("/{restaurantId}/menu/categories/{categoryId}/items")
    @PreAuthorize("#restaurantId == authentication.principal.restaurantId")
    ApiResponse<List<MenuItemResponse>> getItemsByCategory(
            @PathVariable Long restaurantId,
            @PathVariable Long categoryId) {
        return ApiResponse.<List<MenuItemResponse>>builder()
                .result(menuService.getItemsByCategory(restaurantId, categoryId))
                .build();
    }

    @GetMapping("/{restaurantId}/menu/items/{itemId}")
    @PreAuthorize("#restaurantId == authentication.principal.restaurantId")
    ApiResponse<MenuItemResponse> getItem(
            @PathVariable Long restaurantId,
            @PathVariable Long itemId) {
        return ApiResponse.<MenuItemResponse>builder()
                .result(menuService.getItemById(restaurantId, itemId))
                .build();
    }
}