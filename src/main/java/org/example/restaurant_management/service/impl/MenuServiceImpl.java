package org.example.restaurant_management.service.impl;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.restaurant_management.dto.request.MenuCategoryRequest;
import org.example.restaurant_management.dto.request.MenuItemRequest;
import org.example.restaurant_management.dto.response.MenuCategoryResponse;
import org.example.restaurant_management.dto.response.MenuItemResponse;
import org.example.restaurant_management.entity.MenuCategory;
import org.example.restaurant_management.entity.MenuItem;
import org.example.restaurant_management.entity.Restaurant;
import org.example.restaurant_management.exception.AppException;
import org.example.restaurant_management.exception.ErrorCode;
import org.example.restaurant_management.mapper.MenuMapper;
import org.example.restaurant_management.repository.MenuCategoryRepository;
import org.example.restaurant_management.repository.MenuItemRepository;
import org.example.restaurant_management.repository.RestaurantRepository;
import org.example.restaurant_management.service.MenuService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MenuServiceImpl implements MenuService {

    MenuCategoryRepository menuCategoryRepository;
    MenuItemRepository menuItemRepository;
    RestaurantRepository restaurantRepository;
    MenuMapper menuMapper;

    // ============================================================ //
    //  MENU CATEGORY
    // ============================================================ //

    @Transactional
    @Override
    public MenuCategoryResponse createCategory(Long restaurantId, MenuCategoryRequest request) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new AppException(ErrorCode.RESTAURANT_NOT_EXISTED));

        MenuCategory category = menuMapper.toMenuCategory(request);
        category.setRestaurant(restaurant);

        menuCategoryRepository.save(category);

        return menuMapper.toMenuCategoryResponse(category);
    }

    @Transactional
    @Override
    public MenuCategoryResponse updateCategory(Long restaurantId, Long categoryId,
                                               MenuCategoryRequest request) {
        MenuCategory category = findCategoryInRestaurant(restaurantId, categoryId);

        menuMapper.updateMenuCategory(category, request);
        menuCategoryRepository.save(category);

        return menuMapper.toMenuCategoryResponse(category);
    }

    @Transactional
    @Override
    public void deleteCategory(Long restaurantId, Long categoryId) {
        MenuCategory category = findCategoryInRestaurant(restaurantId, categoryId);

        // Chặn xóa nếu category còn item
        if (menuItemRepository.existsByCategory_Id(categoryId)) {
            throw new AppException(ErrorCode.MENU_CATEGORY_HAS_ITEMS);
        }

        menuCategoryRepository.delete(category);
    }

    @Override
    public List<MenuCategoryResponse> getCategories(Long restaurantId) {
        if (!restaurantRepository.existsById(restaurantId)) {
            throw new AppException(ErrorCode.RESTAURANT_NOT_EXISTED);
        }

        return menuCategoryRepository.findByRestaurant_Id(restaurantId)
                .stream()
                .map(menuMapper::toMenuCategoryResponse)
                .toList();
    }

    @Override
    public MenuCategoryResponse getCategoryById(Long restaurantId, Long categoryId) {
        MenuCategory category = findCategoryInRestaurant(restaurantId, categoryId);
        return menuMapper.toMenuCategoryResponse(category);
    }

    // ============================================================ //
    //  MENU ITEM
    // ============================================================ //

    @Transactional
    @Override
    public MenuItemResponse createItem(Long restaurantId, MenuItemRequest request) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new AppException(ErrorCode.RESTAURANT_NOT_EXISTED));

        // Category phải thuộc đúng restaurant này
        MenuCategory category = findCategoryInRestaurant(restaurantId, request.getMenuCategoryId());

        MenuItem item = menuMapper.toMenuItem(request);
        item.setRestaurant(restaurant);
        item.setCategory(category);

        menuItemRepository.save(item);

        return menuMapper.toMenuItemResponse(item);
    }

    @Transactional
    @Override
    public MenuItemResponse updateItem(Long restaurantId, Long itemId, MenuItemRequest request) {
        MenuItem item = findItemInRestaurant(restaurantId, itemId);

        menuMapper.updateMenuItem(item, request);

        // Nếu request đổi category → check category mới có thuộc cùng restaurant
        if (request.getMenuCategoryId() != null
                && !request.getMenuCategoryId().equals(item.getCategory().getId())) {
            MenuCategory newCategory = findCategoryInRestaurant(restaurantId, request.getMenuCategoryId());
            item.setCategory(newCategory);
        }

        menuItemRepository.save(item);

        return menuMapper.toMenuItemResponse(item);
    }

    @Transactional
    @Override
    public void deleteItem(Long restaurantId, Long itemId) {
        MenuItem item = findItemInRestaurant(restaurantId, itemId);
        menuItemRepository.delete(item);
    }

    @Override
    public List<MenuItemResponse> getItems(Long restaurantId) {
        if (!restaurantRepository.existsById(restaurantId)) {
            throw new AppException(ErrorCode.RESTAURANT_NOT_EXISTED);
        }

        return menuItemRepository.findByRestaurant_Id(restaurantId)
                .stream()
                .map(menuMapper::toMenuItemResponse)
                .toList();
    }

    @Override
    public List<MenuItemResponse> getItemsByCategory(Long restaurantId, Long categoryId) {
        // Đảm bảo category thuộc đúng restaurant
        findCategoryInRestaurant(restaurantId, categoryId);

        return menuItemRepository.findByCategory_Id(categoryId)
                .stream()
                .map(menuMapper::toMenuItemResponse)
                .toList();
    }

    @Override
    public MenuItemResponse getItemById(Long restaurantId, Long itemId) {
        MenuItem item = findItemInRestaurant(restaurantId, itemId);
        return menuMapper.toMenuItemResponse(item);
    }

    // ============================================================ //
    //  PRIVATE HELPERS
    // ============================================================ //

    private MenuCategory findCategoryInRestaurant(Long restaurantId, Long categoryId) {
        MenuCategory category = menuCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new AppException(ErrorCode.MENU_CATEGORY_NOT_EXISTED));

        if (!category.getRestaurant().getId().equals(restaurantId)) {
            throw new AppException(ErrorCode.MENU_CATEGORY_NOT_EXISTED);
        }

        return category;
    }

    private MenuItem findItemInRestaurant(Long restaurantId, Long itemId) {
        MenuItem item = menuItemRepository.findById(itemId)
                .orElseThrow(() -> new AppException(ErrorCode.MENU_ITEM_NOT_EXISTED));

        if (!item.getRestaurant().getId().equals(restaurantId)) {
            throw new AppException(ErrorCode.MENU_ITEM_NOT_EXISTED);
        }

        return item;
    }
}