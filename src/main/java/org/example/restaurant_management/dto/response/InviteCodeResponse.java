package org.example.restaurant_management.dto.response;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class InviteCodeResponse {
    private Long restaurantId;
    private String inviteCode;
}