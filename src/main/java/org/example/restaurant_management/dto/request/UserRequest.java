package org.example.restaurant_management.dto.request;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequest {
    @NotBlank(message = "USERNAME_CANNOT_BE_EMPTY")
    @Size(min = 3, max = 50, message = "USERNAME_INVALID")
    private String username;

    @NotBlank(message = "PASSWORD_CANNOT_BE_EMPTY")
    @Size(min = 6, max = 100, message = "PASSWORD_INVALID")
    private String password;

    @NotBlank(message = "FULLNAME_CANNOT_BE_EMPTY")
    @Size(min = 3, max = 100, message = "FULLNAME_INVALID")
    private String fullName;

    @NotBlank(message = "EMAIL_CANNOT_BE_EMPTY")
    @Email(message = "EMAIL_INVALID")
    private String email;

    @NotBlank(message = "PHONE_CANNOT_BE_EMPTY")
    @Pattern(
            regexp = "^(0|\\+84)[0-9]{9}$",
            message = "PHONE_INVALID"
    )
    private String phone;
}
