package org.example.restaurant_management.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.restaurant_management.configuration.ContextTokenPrincipal;
import org.example.restaurant_management.dto.request.UpdateMemberRoleRequest;
import org.example.restaurant_management.dto.response.ApiResponse;
import org.example.restaurant_management.dto.response.MemberResponse;
import org.example.restaurant_management.service.MemberService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MemberController {

    MemberService memberService;

    // Tất cả member trong restaurant đều xem được danh sách (đồng nghiệp)
    @GetMapping("/{restaurantId}/members")
    @PreAuthorize("#restaurantId == authentication.principal.restaurantId")
    ApiResponse<List<MemberResponse>> getMembers(@PathVariable Long restaurantId) {
        return ApiResponse.<List<MemberResponse>>builder()
                .result(memberService.getMembers(restaurantId))
                .build();
    }

    // Đổi role — chỉ ADMIN
    @PutMapping("/{restaurantId}/members/{userId}/role")
    @PreAuthorize("hasRole('ADMIN') and #restaurantId == authentication.principal.restaurantId")
    ApiResponse<MemberResponse> updateRole(
            @PathVariable Long restaurantId,
            @PathVariable Long userId,
            @RequestBody UpdateMemberRoleRequest request,
            @AuthenticationPrincipal ContextTokenPrincipal principal) {

        return ApiResponse.<MemberResponse>builder()
                .result(memberService.updateRole(restaurantId, principal.getUserId(), userId, request))
                .build();
    }

    // Kick — chỉ ADMIN
    @DeleteMapping("/{restaurantId}/members/{userId}")
    @PreAuthorize("hasRole('ADMIN') and #restaurantId == authentication.principal.restaurantId")
    ApiResponse<Void> kickMember(
            @PathVariable Long restaurantId,
            @PathVariable Long userId,
            @AuthenticationPrincipal ContextTokenPrincipal principal) {

        memberService.kickMember(restaurantId, principal.getUserId(), userId);
        return ApiResponse.<Void>builder()
                .message("Member removed successfully")
                .build();
    }
}