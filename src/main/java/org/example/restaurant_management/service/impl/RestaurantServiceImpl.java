package org.example.restaurant_management.service.impl;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.restaurant_management.dto.request.JoinRestaurantRequest;
import org.example.restaurant_management.dto.request.RestaurantRequest;
import org.example.restaurant_management.dto.response.InviteCodeResponse;
import org.example.restaurant_management.dto.response.JoinRestaurantResponse;
import org.example.restaurant_management.dto.response.MyRestaurantResponse;
import org.example.restaurant_management.dto.response.RestaurantResponse;
import org.example.restaurant_management.entity.Restaurant;
import org.example.restaurant_management.entity.User;
import org.example.restaurant_management.entity.UserRestaurantRole;
import org.example.restaurant_management.exception.AppException;
import org.example.restaurant_management.exception.ErrorCode;
import org.example.restaurant_management.mapper.RestaurantMapper;
import org.example.restaurant_management.repository.RestaurantRepository;
import org.example.restaurant_management.repository.UserRepository;
import org.example.restaurant_management.repository.UserRestaurantRoleRepository;
import org.example.restaurant_management.service.RestaurantService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class RestaurantServiceImpl implements RestaurantService {

    UserRepository userRepository;

    RestaurantRepository restaurantRepository;

    RestaurantMapper restaurantMapper;

    UserRestaurantRoleRepository userRestaurantRoleRepository;


    @Transactional
    @Override
    public RestaurantResponse createRestaurant(RestaurantRequest restaurantRequest, Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Restaurant restaurant = restaurantMapper.toRestaurant(restaurantRequest);

        restaurant.setOwner(user);
        restaurant.setInviteCode(generateInviteCode());
        restaurant.setCreatedAt(LocalDateTime.now());
        restaurantRepository.save(restaurant);

        UserRestaurantRole userRestaurantRole = UserRestaurantRole.builder()
                .user(user)
                .restaurant(restaurant)
                .role(UserRestaurantRole.Role.ADMIN)
                .status(UserRestaurantRole.MemberStatus.ACTIVE)
                .build();
        userRestaurantRoleRepository.save(userRestaurantRole);

        return restaurantMapper.toRestaurantResponse(restaurant);
    }

    @Transactional
    @Override
    public RestaurantResponse updateRestaurant(Long restaurantId, RestaurantRequest restaurantRequest) {

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new AppException(ErrorCode.RESTAURANT_NOT_EXISTED));

        restaurantMapper.updateRestaurant(restaurant, restaurantRequest);
        restaurantRepository.save(restaurant);

        return restaurantMapper.toRestaurantResponse(restaurant);
    }

    private   String generateRandomCode() {
        return UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 8)   // lấy 8 ký tự
                .toUpperCase();
    }

    private String generateInviteCode() {
        String newInviteCode;
        do {
            newInviteCode = generateRandomCode();
        }while (restaurantRepository.existsByInviteCode(newInviteCode));

        return newInviteCode;
    }

    @Override
    public List<MyRestaurantResponse> getMyRestaurants(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }

        return userRestaurantRoleRepository
                .findActiveMembershipsWithRestaurant(userId, UserRestaurantRole.MemberStatus.ACTIVE)
                .stream()
                .map(restaurantMapper::toMyRestaurantResponse)
                .toList();
    }

    @Transactional
    @Override
    public JoinRestaurantResponse joinRestaurant(Long userId, JoinRestaurantRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Restaurant restaurant = restaurantRepository.findByInviteCode(request.getInviteCode())
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_INVITE_CODE));

        // Kiểm tra membership hiện tại (nếu có)
        UserRestaurantRole membership = userRestaurantRoleRepository
                .findByUser_IdAndRestaurant_Id(userId, restaurant.getId())
                .orElse(null);

        String message;

        if (membership == null) {
            // Lần đầu join → tạo mới với role STAFF
            membership = UserRestaurantRole.builder()
                    .user(user)
                    .restaurant(restaurant)
                    .role(UserRestaurantRole.Role.STAFF)
                    .status(UserRestaurantRole.MemberStatus.ACTIVE)
                    .build();
            userRestaurantRoleRepository.save(membership);
            message = "Joined restaurant successfully";

        } else if (membership.getStatus() == UserRestaurantRole.MemberStatus.ACTIVE) {
            // Đã là member ACTIVE → từ chối
            throw new AppException(ErrorCode.ALREADY_A_MEMBER);

        } else {
            // Trước đây bị kick / inactive → reactivate
            membership.setStatus(UserRestaurantRole.MemberStatus.ACTIVE);
            userRestaurantRoleRepository.save(membership);
            message = "Rejoined restaurant successfully";
        }

        return JoinRestaurantResponse.builder()
                .restaurantId(restaurant.getId())
                .restaurantName(restaurant.getName())
                .role(membership.getRole())
                .message(message)
                .build();
    }

    @Override
    public InviteCodeResponse getInviteCode(Long restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new AppException(ErrorCode.RESTAURANT_NOT_EXISTED));

        return InviteCodeResponse.builder()
                .restaurantId(restaurant.getId())
                .inviteCode(restaurant.getInviteCode())
                .build();
    }

    @Transactional
    @Override
    public InviteCodeResponse regenerateInviteCode(Long restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new AppException(ErrorCode.RESTAURANT_NOT_EXISTED));

        restaurant.setInviteCode(generateInviteCode());
        restaurantRepository.save(restaurant);

        return InviteCodeResponse.builder()
                .restaurantId(restaurant.getId())
                .inviteCode(restaurant.getInviteCode())
                .build();
    }
}
