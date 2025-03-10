package com.beta.schoolpayment.dto.request;

import lombok.Data;

@Data
public class AuthRequest {
    private String identifier; // Bisa berupa email atau NIS
    private String password;
}
