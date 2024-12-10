package com.group4.cursus.controller;

import com.group4.cursus.dto.EarningsDTO;
import com.group4.cursus.service.EarningsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/earnings")
public class EarningsController {

    @Autowired
    private EarningsService earningsService;

    @GetMapping("/instructor")
    public ResponseEntity<?> getEarningsByInstructor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String instructorEmail = authentication.getName();
        try {
            List<EarningsDTO> earnings = earningsService.getEarningsByInstructor(instructorEmail);
            return ResponseEntity.ok(earnings);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}

