package org.example.restaurant_management.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.restaurant_management.dto.request.UserRequest;
import org.example.restaurant_management.dto.response.UserResponse;
import org.example.restaurant_management.entity.User;
import org.example.restaurant_management.exception.AppException;
import org.example.restaurant_management.exception.ErrorCode;
import org.example.restaurant_management.mapper.UserMapper;
import org.example.restaurant_management.repository.UserRepository;
import org.example.restaurant_management.service.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class UserServiceImpl implements UserService {

    UserRepository userRepository;

    UserMapper userMapper;

    PasswordEncoder passwordEncoder;

    @Override
    public User createUser(UserRequest userRequest) {
        User user = userMapper.toUser(userRequest);

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXISTS);
        }
        if (userRepository.existsByPhone(user.getPhone())) {
            throw new AppException(ErrorCode.PHONE_EXISTS);
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepository.save(user);

        return user;
    }


}
