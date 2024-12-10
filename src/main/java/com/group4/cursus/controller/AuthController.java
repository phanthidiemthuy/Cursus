package com.group4.cursus.controller;

import com.group4.cursus.dto.*;
import com.group4.cursus.service.AuthService;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/user")
public class AuthController {

    @Autowired
    private AuthService authService;

    @GetMapping("/")
    public String home(Model model, @AuthenticationPrincipal OAuth2User principal) {
        if (principal != null) {
            model.addAttribute("profile", principal.getAttributes());
        }
        return "home";
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> authenticateUser(@RequestBody LoginRequest loginRequest, HttpSession session) {
        JwtResponse jwtResponse = authService.authenticateUser(loginRequest);
        Long studentId = Long.valueOf(jwtResponse.getId());
        Long instructorId = Long.valueOf(jwtResponse.getId());
        session.setAttribute("studentId", studentId);
        session.setAttribute("instructorId", instructorId);
        return ResponseEntity.ok(jwtResponse);
    }

    @PostMapping("/register/instructor")
    public ResponseEntity<MyCustomResponse> registerInstructor(@RequestBody SignUpRequest signUpRequest) {
        authService.registerInstructor(signUpRequest);
        MyCustomResponse response = new
                MyCustomResponse("Instructor registered successfully! Please check Email to Confirm.", true);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/register/student")
    public ResponseEntity<MyCustomResponse> registerStudent(@RequestBody SignUpStudentRequest signUpRequest) {
        authService.registerStudent(signUpRequest);
        MyCustomResponse response = new
                MyCustomResponse("Student registered successfully! Please check Email to Confirm.", true);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest) {
        boolean result = authService.changePassword(changePasswordRequest);
        if (result) {
            return ResponseEntity.ok(new MyCustomResponse("Password changed successfully!", true));
        } else {
            return ResponseEntity.badRequest().body(new MyCustomResponse("Current password is incorrect.", false));
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        boolean result = authService.forgotPassword(forgotPasswordRequest.getEmail());
        if (result) {
            return ResponseEntity.ok(new MyCustomResponse("Reset password email sent successfully!", true));
        } else {
            return ResponseEntity.badRequest().body(new MyCustomResponse("Email not found.", false));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) {
        boolean result = authService.resetPassword(resetPasswordRequest);
        if (result) {
            return ResponseEntity.ok(new MyCustomResponse("Password reset successfully!", true));
        } else {
            return ResponseEntity.badRequest().body(new MyCustomResponse("Invalid token.", false));
        }
    }


    @PutMapping("/update-student-profile")
    public ResponseEntity<?> updateStudentProfile(@RequestBody UpdateStudentProfileRequest updateProfileRequest) {
        authService.updateStudentProfile(updateProfileRequest);
        return ResponseEntity.ok(new MyCustomResponse("Student profile updated successfully", true));
    }

    @PutMapping("/update-instructor-profile")
    public ResponseEntity<?> updateInstructorProfile(@RequestBody UpdateInstructorProfileRequest updateProfileRequest) {
        authService.updateInstructorProfile(updateProfileRequest);
        return ResponseEntity.ok(new MyCustomResponse("Instructor profile updated successfully", true));
    }

    @GetMapping("/verify-email")
    public ResponseEntity<MyCustomResponse> verifyEmailToken(@RequestParam String token) {
        boolean result = authService.verifyEmailToken(token);
        if (result) {
            return ResponseEntity.ok(new MyCustomResponse("Email verified successfully!", true));
        } else {
            return ResponseEntity.badRequest().body(new MyCustomResponse("Invalid token or email already verified.", false));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token) {
        String jwt = token.substring(7); // Remove "Bearer " prefix
        authService.logout(jwt);
        return ResponseEntity.ok(new MyCustomResponse("Successfully logged out", true));
    }
}
