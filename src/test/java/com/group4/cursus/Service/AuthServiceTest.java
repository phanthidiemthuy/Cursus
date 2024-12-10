package com.group4.cursus.service;

import com.group4.cursus.dto.*;
import com.group4.cursus.entity.Instructor;
import com.group4.cursus.entity.Student;
import com.group4.cursus.entity.User;
import com.group4.cursus.repository.InstructorRepository;
import com.group4.cursus.repository.StudentRepository;
import com.group4.cursus.repository.UserRepository;
import com.group4.cursus.security.JwtUtils;
import com.group4.cursus.security.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private InstructorRepository instructorRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private EmailService emailService;

    @Mock
    private TokenBlacklistService tokenBlacklistService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testEmailExists() {
        String email = "test@example.com";
        when(userRepository.existsByEmail(email)).thenReturn(true);

        assertTrue(authService.emailExists(email));
        verify(userRepository, times(1)).existsByEmail(email);
    }

    @Test
    void testRegisterInstructor_EmailAlreadyExists() {
        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setEmail("test@example.com");

        when(userRepository.existsByEmail(signUpRequest.getEmail())).thenReturn(true);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            authService.registerInstructor(signUpRequest);
        });

        assertEquals("400 BAD_REQUEST \"Email already exists.\"", exception.getMessage());
    }

    @Test
    void testRegisterInstructor_Success() {
        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setFullName("John Doe");
        signUpRequest.setEmail("john.doe@example.com");
        signUpRequest.setPassword("password123");
        signUpRequest.setAddress("123 Street");
        signUpRequest.setExperience("5 years");
        signUpRequest.setSalary(new BigDecimal("50000.00"));

        when(userRepository.existsByEmail(signUpRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(signUpRequest.getPassword())).thenReturn("encodedPassword");

        authService.registerInstructor(signUpRequest);

        verify(userRepository, times(1)).save(any(Instructor.class));
        verify(emailService, times(1)).sendApplicationUnderReviewEmail(signUpRequest.getEmail());
    }

    @Test
    void testRegisterStudent_EmailAlreadyExists() {
        SignUpStudentRequest signUpRequest = new SignUpStudentRequest();
        signUpRequest.setEmail("test@example.com");

        when(userRepository.existsByEmail(signUpRequest.getEmail())).thenReturn(true);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            authService.registerStudent(signUpRequest);
        });

        assertEquals("400 BAD_REQUEST \"Email already exists.\"", exception.getMessage());
    }

    @Test
    void testRegisterStudent_Success() {
        SignUpStudentRequest signUpRequest = new SignUpStudentRequest();
        signUpRequest.setFullName("Jane Doe");
        signUpRequest.setEmail("jane.doe@example.com");
        signUpRequest.setPassword("password123");
        signUpRequest.setAddress("456 Street");

        when(userRepository.existsByEmail(signUpRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(signUpRequest.getPassword())).thenReturn("encodedPassword");
        when(jwtUtils.generateJwtToken(signUpRequest.getEmail())).thenReturn("token");

        authService.registerStudent(signUpRequest);

        verify(userRepository, times(1)).save(any(Student.class));
        verify(emailService, times(1)).sendConfirmationEmail(signUpRequest.getEmail(), "token");
    }

    @Test
    void testUpdateStudentProfile_Success() {
        UpdateStudentProfileRequest request = new UpdateStudentProfileRequest();
        request.setFullName("Updated Name");
        request.setAddress("Updated Address");

        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(authentication.getName()).thenReturn("student@example.com");

        Student student = new Student();
        student.setEmail("student@example.com");

        when(studentRepository.findByEmail("student@example.com")).thenReturn(Optional.of(student));
        when(jwtUtils.generateJwtToken("student@example.com")).thenReturn("token");

        authService.updateStudentProfile(request);

        verify(studentRepository, times(1)).save(student);
        verify(emailService, times(1)).sendConfirmationEmailUpdateProfile(eq("student@example.com"), eq("token"));
    }


    @Test
    void testUpdateInstructorProfile_Success() {
        UpdateInstructorProfileRequest request = new UpdateInstructorProfileRequest();
        request.setFullName("Updated Name");
        request.setAddress("Updated Address");
        request.setExperience("Updated Experience");
        request.setSalary(new BigDecimal("60000.00"));

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(authentication.getName()).thenReturn("instructor@example.com");
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("instructor@example.com");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        Instructor instructor = new Instructor();
        instructor.setEmail("instructor@example.com");

        when(instructorRepository.findByEmail("instructor@example.com")).thenReturn(Optional.of(instructor));
        when(jwtUtils.generateJwtToken("instructor@example.com")).thenReturn("token");

        authService.updateInstructorProfile(request);

        verify(instructorRepository, times(1)).save(instructor);
        verify(emailService, times(1)).sendConfirmationEmailUpdateProfile(eq("instructor@example.com"), eq("token"));
    }



    @Test
    void testAuthenticateUser_Success() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("user@example.com");
        loginRequest.setPassword("password");

        Authentication authentication = mock(Authentication.class);
        UserDetailsImpl userDetails = mock(UserDetailsImpl.class);

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getId()).thenReturn(1);
        when(userDetails.getUsername()).thenReturn("user@example.com");
        when(userDetails.getFullName()).thenReturn("User Fullname");
        when(jwtUtils.generateJwtToken(authentication)).thenReturn("jwtToken");

        JwtResponse response = authService.authenticateUser(loginRequest);

        assertEquals("jwtToken", response.getToken());
        assertEquals(1, response.getId());
        assertEquals("user@example.com", response.getEmail());
        assertEquals("User Fullname", response.getFullName());
    }

    @Test
    void testChangePassword_Success() {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword("currentPassword");
        request.setNewPassword("newPassword");
        request.setConfirmPassword("newPassword");

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(authentication.getName()).thenReturn("user@example.com");
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("user@example.com");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        User user = new User();
        user.setEmail("user@example.com");
        user.setPassword("encodedCurrentPassword");

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("currentPassword", user.getPassword())).thenReturn(true);
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");

        assertTrue(authService.changePassword(request));

        verify(userRepository, times(1)).save(user);
        verify(emailService, times(1)).sendSimpleMessage(eq("user@example.com"), eq("Password Changed Successfully"), anyString());
    }


    @Test
    void testForgotPassword_Success() {
        String email = "user@example.com";
        User user = new User();
        user.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(jwtUtils.generateJwtToken(email)).thenReturn("token");

        assertTrue(authService.forgotPassword(email));

        verify(emailService, times(1)).sendForgotPasswordEmail(email, "token");
    }

    @Test
    void testResetPassword_Success() {
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setToken("token");
        request.setNewPassword("newPassword");

        String email = "user@example.com";
        User user = new User();
        user.setEmail(email);

        when(jwtUtils.getUserNameFromJwtToken("token")).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");

        assertTrue(authService.resetPassword(request));

        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testVerifyEmailToken_Success() {
        String token = "token";
        String email = "user@example.com";
        User user = new User();
        user.setEmail(email);
        user.setApproved(false);

        when(jwtUtils.getUserNameFromJwtToken(token)).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        assertTrue(authService.verifyEmailToken(token));

        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testApproveInstructor_Success() {
        Integer instructorId = 1;
        Instructor instructor = new Instructor();
        instructor.setApproved(false);

        when(instructorRepository.findById(instructorId)).thenReturn(Optional.of(instructor));

        authService.approveInstructor(instructorId);

        verify(instructorRepository, times(1)).save(instructor);
        verify(emailService, times(1)).sendApprovalEmail(instructor.getEmail());
    }

    @Test
    void testRejectInstructor_Success() {
        Integer instructorId = 1;
        Instructor instructor = new Instructor();
        instructor.setApproved(false);

        when(instructorRepository.findById(instructorId)).thenReturn(Optional.of(instructor));

        authService.rejectInstructor(instructorId);

        verify(instructorRepository, times(1)).save(instructor);
        verify(emailService, times(1)).sendRejectionEmail(instructor.getEmail());
    }

    @Test
    void testLogout_Success() {
        String token = "token";

        authService.logout(token);

        verify(tokenBlacklistService, times(1)).blacklistToken(token);
    }
}
