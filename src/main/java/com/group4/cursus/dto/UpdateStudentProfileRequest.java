package com.group4.cursus.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UpdateStudentProfileRequest {
    private String fullName;
    private String address;
    //private String phoneNumber;
}
