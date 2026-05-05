package org.example.restaurant_management.mapper;

import org.example.restaurant_management.dto.request.RestaurantTableRequest;
import org.example.restaurant_management.dto.response.RestaurantTableResponse;
import org.example.restaurant_management.entity.RestaurantTable;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RestaurantTableMapper {
    @Mapping(source = "restaurant.id", target = "restaurantId")
    RestaurantTableResponse toRestaurantTableResponse(RestaurantTable entity);

    RestaurantTable toRestaurantTable(RestaurantTableRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "restaurant", ignore = true)
    void updateRestaurantTable(@MappingTarget RestaurantTable table, RestaurantTableRequest request);
}
