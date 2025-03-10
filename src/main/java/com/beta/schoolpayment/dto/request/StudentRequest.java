package com.beta.schoolpayment.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

@Data
public class StudentRequest {
    private Long nis;
    private String name;
    private Long classId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate birthdate;
    private String address;
    private String phoneNumber;
}
