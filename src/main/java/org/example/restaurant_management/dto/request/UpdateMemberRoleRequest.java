// UpdateMemberRoleRequest.java
package org.example.restaurant_management.dto.request;

import lombok.*;
import org.example.restaurant_management.entity.UserRestaurantRole;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UpdateMemberRoleRequest {
    private UserRestaurantRole.Role role;
}