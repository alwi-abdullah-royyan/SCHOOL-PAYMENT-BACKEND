package com.beta.schoolpayment.dto.request;

import lombok.Data;

@Data
public class AuthRequest {
    private String email;
    private Long nis;
    private String password;
}
