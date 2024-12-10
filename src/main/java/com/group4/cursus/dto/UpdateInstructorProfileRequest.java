package com.group4.cursus.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UpdateInstructorProfileRequest {
    private String fullName;
    private String address;
    private String experience;
    private BigDecimal salary;

}
