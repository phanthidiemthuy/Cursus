package com.group4.cursus.controller;

import com.group4.cursus.dto.IntroductorDashboardDTO;
import com.group4.cursus.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/instructor")
    public ResponseEntity<?> getDashboardData() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String instructorEmail = authentication.getName();
        try {
            IntroductorDashboardDTO dashboardData = dashboardService.getDashboardData(instructorEmail);
            return ResponseEntity.ok(dashboardData);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}