package com.beta.schoolpayment.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class SchoolYearResponse {
    private Long schoolYearId;
    private String schoolYear;
    private Date startDate;
    private Date endDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
