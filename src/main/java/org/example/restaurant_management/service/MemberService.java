package org.example.restaurant_management.service;

import org.example.restaurant_management.dto.request.UpdateMemberRoleRequest;
import org.example.restaurant_management.dto.response.MemberResponse;

import java.util.List;

public interface MemberService {
    List<MemberResponse> getMembers(Long restaurantId);
    MemberResponse updateRole(Long restaurantId, Long actorUserId, Long targetUserId,
                              UpdateMemberRoleRequest request);
    void kickMember(Long restaurantId, Long actorUserId, Long targetUserId);
}