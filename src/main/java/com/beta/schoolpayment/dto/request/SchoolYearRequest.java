package com.beta.schoolpayment.dto.request;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Data
public class SchoolYearRequest {
    private String schoolYear;
    private LocalDate startDate;
    private LocalDate endDate;
}
