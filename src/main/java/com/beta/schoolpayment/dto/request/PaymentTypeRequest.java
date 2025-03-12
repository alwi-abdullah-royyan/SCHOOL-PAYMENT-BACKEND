package com.beta.schoolpayment.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PaymentTypeRequest {

    @NotBlank(message = "Payment type name is required")
    private String paymentTypeName;
}
