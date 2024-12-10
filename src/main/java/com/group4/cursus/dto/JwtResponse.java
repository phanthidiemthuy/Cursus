package com.group4.cursus.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private Integer id;
    private String email;
    private String fullName;

    public JwtResponse(String token, Integer id, String email, String fullName) {
        this.token = token;
        this.type = "Bearer";
        this.id = id;
        this.email = email;
        this.fullName = fullName;
    }
}
