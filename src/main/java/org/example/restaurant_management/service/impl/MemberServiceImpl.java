package org.example.restaurant_management.service.impl;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.restaurant_management.dto.request.UpdateMemberRoleRequest;
import org.example.restaurant_management.dto.response.MemberResponse;
import org.example.restaurant_management.entity.Restaurant;
import org.example.restaurant_management.entity.UserRestaurantRole;
import org.example.restaurant_management.exception.AppException;
import org.example.restaurant_management.exception.ErrorCode;
import org.example.restaurant_management.mapper.RestaurantMapper;
import org.example.restaurant_management.repository.RestaurantRepository;
import org.example.restaurant_management.repository.UserRestaurantRoleRepository;
import org.example.restaurant_management.service.MemberService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MemberServiceImpl implements MemberService {

    UserRestaurantRoleRepository userRestaurantRoleRepository;
    RestaurantRepository restaurantRepository;
    RestaurantMapper restaurantMapper;
    AuthenticationServiceImpl authenticationService;  // để gọi evictMembershipCache

    // ============================================================ //
    //  LIST MEMBERS
    // ============================================================ //

    @Override
    public List<MemberResponse> getMembers(Long restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new AppException(ErrorCode.RESTAURANT_NOT_EXISTED));

        Long ownerId = restaurant.getOwner().getId();

        return userRestaurantRoleRepository.findAllByRestaurantIdWithUser(restaurantId)
                .stream()
                .map(membership -> {
                    MemberResponse response = restaurantMapper.toMemberResponse(membership);
                    response.setOwner(membership.getUser().getId().equals(ownerId));
                    return response;
                })
                .toList();
    }

    // ============================================================ //
    //  UPDATE ROLE
    // ============================================================ //

    @Transactional
    @Override
    public MemberResponse updateRole(Long restaurantId, Long actorUserId, Long targetUserId,
                                     UpdateMemberRoleRequest request) {
        // 1. Không cho thao tác lên chính mình
        if (actorUserId.equals(targetUserId)) {
            throw new AppException(ErrorCode.CANNOT_MODIFY_SELF);
        }

        // 2. Lấy actor membership & target membership
        UserRestaurantRole actorMembership = findActiveMembership(actorUserId, restaurantId);
        UserRestaurantRole targetMembership = findAnyMembership(targetUserId, restaurantId);

        // 3. Lấy restaurant để check owner
        Restaurant restaurant = targetMembership.getRestaurant();
        Long ownerId = restaurant.getOwner().getId();

        // 4. Không được sửa OWNER
        if (targetUserId.equals(ownerId)) {
            throw new AppException(ErrorCode.CANNOT_MODIFY_OWNER);
        }

        // 5. Quy tắc: chỉ OWNER mới được đổi role của ADMIN khác
        boolean actorIsOwner = actorUserId.equals(ownerId);
        boolean targetIsAdmin = targetMembership.getRole() == UserRestaurantRole.Role.ADMIN;
        boolean willBeAdmin = request.getRole() == UserRestaurantRole.Role.ADMIN;

        if ((targetIsAdmin || willBeAdmin) && !actorIsOwner) {
            throw new AppException(ErrorCode.INSUFFICIENT_PERMISSION);
        }

        // 6. Update
        targetMembership.setRole(request.getRole());
        userRestaurantRoleRepository.save(targetMembership);

        // 7. Evict cache để context token mới được cấp với role mới
        authenticationService.evictMembershipCache(targetUserId, restaurantId);

        MemberResponse response = restaurantMapper.toMemberResponse(targetMembership);
        response.setOwner(false);
        return response;
    }

    // ============================================================ //
    //  KICK MEMBER
    // ============================================================ //

    @Transactional
    @Override
    public void kickMember(Long restaurantId, Long actorUserId, Long targetUserId) {
        // 1. Không tự kick mình
        if (actorUserId.equals(targetUserId)) {
            throw new AppException(ErrorCode.CANNOT_MODIFY_SELF);
        }

        UserRestaurantRole targetMembership = findAnyMembership(targetUserId, restaurantId);
        Restaurant restaurant = targetMembership.getRestaurant();
        Long ownerId = restaurant.getOwner().getId();

        // 2. Không kick OWNER
        if (targetUserId.equals(ownerId)) {
            throw new AppException(ErrorCode.CANNOT_MODIFY_OWNER);
        }

        // 3. Chỉ OWNER mới kick được ADMIN
        boolean actorIsOwner = actorUserId.equals(ownerId);
        boolean targetIsAdmin = targetMembership.getRole() == UserRestaurantRole.Role.ADMIN;
        if (targetIsAdmin && !actorIsOwner) {
            throw new AppException(ErrorCode.INSUFFICIENT_PERMISSION);
        }

        // 4. Set INACTIVE thay vì xóa
        if (targetMembership.getStatus() != UserRestaurantRole.MemberStatus.ACTIVE) {
            // Đã inactive sẵn → idempotent, không cần làm gì
            return;
        }

        targetMembership.setStatus(UserRestaurantRole.MemberStatus.INACTIVE);
        userRestaurantRoleRepository.save(targetMembership);

        // 5. Evict cache
        authenticationService.evictMembershipCache(targetUserId, restaurantId);
    }

    // ============================================================ //
    //  PRIVATE HELPERS
    // ============================================================ //

    private UserRestaurantRole findActiveMembership(Long userId, Long restaurantId) {
        return userRestaurantRoleRepository
                .findByUser_IdAndRestaurant_IdAndStatus(
                        userId, restaurantId, UserRestaurantRole.MemberStatus.ACTIVE)
                .orElseThrow(() -> new AppException(ErrorCode.MEMBERSHIP_NOT_EXISTED));
    }

    private UserRestaurantRole findAnyMembership(Long userId, Long restaurantId) {
        return userRestaurantRoleRepository
                .findByUser_IdAndRestaurant_Id(userId, restaurantId)
                .orElseThrow(() -> new AppException(ErrorCode.MEMBERSHIP_NOT_EXISTED));
    }
}