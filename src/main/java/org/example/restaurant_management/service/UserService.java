package org.example.restaurant_management.service;

import org.example.restaurant_management.dto.request.UserRequest;
import org.example.restaurant_management.dto.response.UserResponse;
import org.example.restaurant_management.entity.User;

public interface UserService {
    public User createUser(UserRequest userRequest);
}
