package com.beta.schoolpayment.dto.request;

import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Data
public class UserRequest {

    @Pattern(regexp = "\\d{6,10}", message = "NIS must be between 6-10 digits")
    private Long nis;

    @NotBlank(message = "Name is required")
    private String name;

    @Email(message = "Invalid email format")
    private String email;

    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;
    private String confirmPassword;
    private String profilePicture;
    private String role;
}
