package org.example.restaurant_management.mapper;

import org.example.restaurant_management.dto.request.UserRequest;
import org.example.restaurant_management.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    public User toUser(UserRequest userRequest);
}
