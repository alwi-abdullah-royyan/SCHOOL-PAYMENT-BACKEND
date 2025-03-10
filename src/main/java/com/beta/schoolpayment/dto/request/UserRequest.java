package com.beta.schoolpayment.dto.request;

import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UserRequest {
    private Long nis;
    private String name;
    private String email;
    private String password;
    private String confirmPassword;
    private MultipartFile profilePicture;
    private String role;
}
