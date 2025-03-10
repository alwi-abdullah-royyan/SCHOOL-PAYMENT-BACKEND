package com.beta.schoolpayment.dto.request;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class SchoolYearRequest {
    private String schoolYear;
    private Date startDate;
    private Date endDate;
}
