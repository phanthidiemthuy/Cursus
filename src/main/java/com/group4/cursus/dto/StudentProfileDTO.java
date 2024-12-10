package com.group4.cursus.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class StudentProfileDTO {
    private int userId;
    private String fullName;
    private String email;
    private String address;
}
