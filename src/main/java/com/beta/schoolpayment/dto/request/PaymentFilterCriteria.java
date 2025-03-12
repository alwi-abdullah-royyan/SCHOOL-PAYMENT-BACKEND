package com.beta.schoolpayment.dto.request;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

@Data
public class PaymentFilterCriteria {
    private String paymentName;
    private String studentName;
    private String userName;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate schoolYearStartDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate schoolYearEndDate;

    private String paymentStatus;
}
