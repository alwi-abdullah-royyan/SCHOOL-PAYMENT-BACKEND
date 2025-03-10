package com.beta.schoolpayment.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ClassesResponse {
    private Long classesId;
    private String classesName;
    private Long schoolYearId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
