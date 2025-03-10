package com.beta.schoolpayment.dto.request;

import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;

@Data
public class PaymentRequest {
    private String paymentName;
    private UUID userId;
    private Long studentId;
    private Long paymentTypeId;
    private BigDecimal amount;
    private String paymentStatus;
    private String description;
}
