package com.group4.cursus.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignUpStudentRequest {
    private String fullName;
    private String email;
    private String password;
    private String address;
}
