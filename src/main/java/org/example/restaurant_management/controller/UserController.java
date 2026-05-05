package org.example.restaurant_management.controller;

import lombok.RequiredArgsConstructor;
import org.example.restaurant_management.dto.request.UserRequest;
import org.example.restaurant_management.dto.response.ApiResponse;
import org.example.restaurant_management.dto.response.UserResponse;
import org.example.restaurant_management.entity.User;
import org.example.restaurant_management.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/user")
    ApiResponse<User> createUser(@RequestBody UserRequest userRequest) {
        return ApiResponse.<User>builder()
                .result(userService.createUser(userRequest))
                .build();
    }
}
