package com.group4.cursus.controller;

import com.group4.cursus.dto.*;
import com.group4.cursus.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.ui.Model;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;


import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @Mock
    private Model model;

    @Mock
    private OAuth2User principal;

    @Mock
    private HttpSession session;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testHome() {
        when(principal.getAttributes()).thenReturn(Map.of("name", "Test User"));
        String viewName = authController.home(model, principal);
        verify(model, times(1)).addAttribute(eq("profile"), any());
        assertEquals("home", viewName);
    }

    @Test
    public void testAuthenticateUser() {
        LoginRequest loginRequest = new LoginRequest("test@example.com", "password");
        JwtResponse jwtResponse = new JwtResponse("token", 1, "test@example.com", "Test User");
        when(authService.authenticateUser(any(LoginRequest.class))).thenReturn(jwtResponse);

        ResponseEntity<JwtResponse> response = authController.authenticateUser(loginRequest, session);
        verify(session, times(1)).setAttribute(eq("studentId"), eq(1L));
        verify(session, times(1)).setAttribute(eq("instructorId"), eq(1L));
        assertEquals(jwtResponse, response.getBody());
    }

    @Test
    public void testRegisterInstructor() {
        SignUpRequest signUpRequest = new SignUpRequest("test@example.com", "password", "Name", "Address", "Experience", BigDecimal.ZERO);
        doNothing().when(authService).registerInstructor(any(SignUpRequest.class));

        ResponseEntity<MyCustomResponse> response = authController.registerInstructor(signUpRequest);
        assertEquals("Instructor registered successfully! Please check Email to Confirm.", response.getBody().getMessage());
        assertTrue(response.getBody().isSuccess());
    }

    @Test
    public void testRegisterStudent() {
        SignUpStudentRequest signUpStudentRequest = new SignUpStudentRequest("test@example.com", "password", "First", "Last");
        doNothing().when(authService).registerStudent(any(SignUpStudentRequest.class));

        ResponseEntity<MyCustomResponse> response = authController.registerStudent(signUpStudentRequest);
        assertEquals("Student registered successfully! Please check Email to Confirm.", response.getBody().getMessage());
        assertTrue(response.getBody().isSuccess());
    }

    @Test
    public void testChangePasswordSuccess() {
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest("oldPassword", "newPassword", "newPassword");
        when(authService.changePassword(any(ChangePasswordRequest.class))).thenReturn(true);

        ResponseEntity<?> response = authController.changePassword(changePasswordRequest);
        assertEquals("Password changed successfully!", ((MyCustomResponse) response.getBody()).getMessage());
        assertTrue(((MyCustomResponse) response.getBody()).isSuccess());
    }

    @Test
    public void testChangePasswordFailure() {
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest("oldPassword", "newPassword", "newPassword");
        when(authService.changePassword(any(ChangePasswordRequest.class))).thenReturn(false);

        ResponseEntity<?> response = authController.changePassword(changePasswordRequest);
        assertEquals("Current password is incorrect.", ((MyCustomResponse) response.getBody()).getMessage());
        assertFalse(((MyCustomResponse) response.getBody()).isSuccess());
    }

    @Test
    public void testForgotPasswordSuccess() {
        ForgotPasswordRequest forgotPasswordRequest = new ForgotPasswordRequest("test@example.com");
        when(authService.forgotPassword(anyString())).thenReturn(true);

        ResponseEntity<?> response = authController.forgotPassword(forgotPasswordRequest);
        assertEquals("Reset password email sent successfully!", ((MyCustomResponse) response.getBody()).getMessage());
        assertTrue(((MyCustomResponse) response.getBody()).isSuccess());
    }

    @Test
    public void testForgotPasswordFailure() {
        ForgotPasswordRequest forgotPasswordRequest = new ForgotPasswordRequest("test@example.com");
        when(authService.forgotPassword(anyString())).thenReturn(false);

        ResponseEntity<?> response = authController.forgotPassword(forgotPasswordRequest);
        assertEquals("Email not found.", ((MyCustomResponse) response.getBody()).getMessage());
        assertFalse(((MyCustomResponse) response.getBody()).isSuccess());
    }

    @Test
    public void testResetPasswordSuccess() {
        ResetPasswordRequest resetPasswordRequest = new ResetPasswordRequest("token", "newPassword");
        when(authService.resetPassword(any(ResetPasswordRequest.class))).thenReturn(true);

        ResponseEntity<?> response = authController.resetPassword(resetPasswordRequest);
        assertEquals("Password reset successfully!", ((MyCustomResponse) response.getBody()).getMessage());
        assertTrue(((MyCustomResponse) response.getBody()).isSuccess());
    }

    @Test
    public void testResetPasswordFailure() {
        ResetPasswordRequest resetPasswordRequest = new ResetPasswordRequest("token", "newPassword");
        when(authService.resetPassword(any(ResetPasswordRequest.class))).thenReturn(false);

        ResponseEntity<?> response = authController.resetPassword(resetPasswordRequest);
        assertEquals("Invalid token.", ((MyCustomResponse) response.getBody()).getMessage());
        assertFalse(((MyCustomResponse) response.getBody()).isSuccess());
    }

    @Test
    public void testUpdateStudentProfile() {
        UpdateStudentProfileRequest updateStudentProfileRequest = new UpdateStudentProfileRequest("name", "TP.HCM");
        doNothing().when(authService).updateStudentProfile(any(UpdateStudentProfileRequest.class));

        ResponseEntity<?> response = authController.updateStudentProfile(updateStudentProfileRequest);
        assertEquals("Student profile updated successfully", ((MyCustomResponse) response.getBody()).getMessage());
        assertTrue(((MyCustomResponse) response.getBody()).isSuccess());
    }

    @Test
    public void testUpdateInstructorProfile() {
        UpdateInstructorProfileRequest updateInstructorProfileRequest = new UpdateInstructorProfileRequest("Name", "TH.HCM", "Experience", BigDecimal.ZERO);
        doNothing().when(authService).updateInstructorProfile(any(UpdateInstructorProfileRequest.class));

        ResponseEntity<?> response = authController.updateInstructorProfile(updateInstructorProfileRequest);
        assertEquals("Instructor profile updated successfully", ((MyCustomResponse) response.getBody()).getMessage());
        assertTrue(((MyCustomResponse) response.getBody()).isSuccess());
    }

    @Test
    public void testVerifyEmailTokenSuccess() {
        when(authService.verifyEmailToken(anyString())).thenReturn(true);

        ResponseEntity<MyCustomResponse> response = authController.verifyEmailToken("token");
        assertEquals("Email verified successfully!", response.getBody().getMessage());
        assertTrue(response.getBody().isSuccess());
    }

    @Test
    public void testVerifyEmailTokenFailure() {
        when(authService.verifyEmailToken(anyString())).thenReturn(false);

        ResponseEntity<MyCustomResponse> response = authController.verifyEmailToken("token");
        assertEquals("Invalid token or email already verified.", response.getBody().getMessage());
        assertFalse(response.getBody().isSuccess());
    }

    @Test
    public void testLogout() {
        doNothing().when(authService).logout(anyString());

        ResponseEntity<?> response = authController.logout("Bearer token");
        assertEquals("Successfully logged out", ((MyCustomResponse) response.getBody()).getMessage());
        assertTrue(((MyCustomResponse) response.getBody()).isSuccess());
    }

}
