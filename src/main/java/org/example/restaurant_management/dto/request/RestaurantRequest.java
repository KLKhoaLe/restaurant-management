package org.example.restaurant_management.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestaurantRequest {

    @NotBlank(message = "NAME_CANNOT_BE_EMPTY")
    private String name;

    @NotBlank(message = "ADDRESS_CANNOT_BE_EMPTY")
    private String address;

    @NotBlank(message = "PHONE_CANNOT_BE_EMPTY")
    @Pattern(
            regexp = "^(0|\\+84)[0-9]{9}$",
            message = "PHONE_INVALID"
    )
    private String phone;

}
