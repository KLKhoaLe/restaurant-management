package org.example.restaurant_management.mapper;

import org.example.restaurant_management.dto.request.RestaurantRequest;
import org.example.restaurant_management.dto.response.MemberResponse;
import org.example.restaurant_management.dto.response.MyRestaurantResponse;
import org.example.restaurant_management.dto.response.RestaurantResponse;
import org.example.restaurant_management.entity.Restaurant;
import org.example.restaurant_management.entity.UserRestaurantRole;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RestaurantMapper {
    public Restaurant toRestaurant(RestaurantRequest restaurantRequest);

    public RestaurantResponse toRestaurantResponse(Restaurant restaurant);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "inviteCode", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateRestaurant(@MappingTarget Restaurant restaurant, RestaurantRequest restaurantRequest);

    @Mapping(source = "restaurant.id", target = "id")
    @Mapping(source = "restaurant.name", target = "name")
    @Mapping(source = "restaurant.address", target = "address")
    @Mapping(source = "role", target = "role")
    MyRestaurantResponse toMyRestaurantResponse(UserRestaurantRole membership);

    @Mapping(source = "id", target = "membershipId")
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "user.fullName", target = "fullName")
    @Mapping(target = "isOwner", ignore = true)  // set thủ công trong service
    MemberResponse toMemberResponse(UserRestaurantRole membership);
}
