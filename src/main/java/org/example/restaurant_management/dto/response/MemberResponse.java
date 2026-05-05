// MemberResponse.java
package org.example.restaurant_management.dto.response;

import lombok.*;
import org.example.restaurant_management.entity.UserRestaurantRole;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MemberResponse {
    private Long membershipId;
    private Long userId;
    private String username;
    private String fullName;
    private UserRestaurantRole.Role role;
    private UserRestaurantRole.MemberStatus status;
    private boolean isOwner;
}