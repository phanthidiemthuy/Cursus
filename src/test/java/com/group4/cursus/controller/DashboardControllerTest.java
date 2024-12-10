package com.group4.cursus.controller;

import com.group4.cursus.dto.IntroductorDashboardDTO;
import com.group4.cursus.service.DashboardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class DashboardControllerTest {

    @Mock
    private DashboardService dashboardService;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private DashboardController dashboardController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
    }

    @Test
    public void testGetDashboardData() throws Exception {
        String instructorEmail = "test@example.com";
        IntroductorDashboardDTO dashboardDTO = new IntroductorDashboardDTO();

        when(authentication.getName()).thenReturn(instructorEmail);
        when(dashboardService.getDashboardData(instructorEmail)).thenReturn(dashboardDTO);

        ResponseEntity<?> response = dashboardController.getDashboardData();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dashboardDTO, response.getBody());
    }

    @Test
    public void testGetDashboardDataException() throws Exception {
        String instructorEmail = "test@example.com";

        when(authentication.getName()).thenReturn(instructorEmail);
        when(dashboardService.getDashboardData(instructorEmail)).thenThrow(new RuntimeException("Error"));

        ResponseEntity<?> response = dashboardController.getDashboardData();
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Error: Error", response.getBody());
    }
}
