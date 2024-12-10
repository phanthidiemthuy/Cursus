package com.group4.cursus.dto;

import lombok.Data;

@Data
public class InstructorDTO {
    private Long userId;
    private String fullName;
    private String thumbnail;
    private String email;
    private String address;
}