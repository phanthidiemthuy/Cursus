package com.group4.cursus.controller;

import com.group4.cursus.dto.EarningsDTO;
import com.group4.cursus.service.EarningsService;
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

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class EarningsControllerTest {

    @Mock
    private EarningsService earningsService;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private EarningsController earningsController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
    }

    @Test
    public void testGetEarningsByInstructor() throws Exception {
        String instructorEmail = "test@example.com";
        List<EarningsDTO> earningsDTOList = Collections.singletonList(new EarningsDTO());

        when(authentication.getName()).thenReturn(instructorEmail);
        when(earningsService.getEarningsByInstructor(instructorEmail)).thenReturn(earningsDTOList);

        ResponseEntity<?> response = earningsController.getEarningsByInstructor();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(earningsDTOList, response.getBody());
    }

    @Test
    public void testGetEarningsByInstructorException() throws Exception {
        String instructorEmail = "test@example.com";

        when(authentication.getName()).thenReturn(instructorEmail);
        when(earningsService.getEarningsByInstructor(instructorEmail)).thenThrow(new RuntimeException("Error"));

        ResponseEntity<?> response = earningsController.getEarningsByInstructor();
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Error: Error", response.getBody());
    }
}
