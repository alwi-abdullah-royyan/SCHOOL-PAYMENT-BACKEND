package com.beta.schoolpayment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponseDTO {
    private UUID id;
    private String paymentName;
    private String paymentStatus;
    private LocalDate paymentDate;
    private String studentName;
    private String userName;
}
