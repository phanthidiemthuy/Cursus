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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InstructorRepository instructorRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private EmailService emailService;

    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    public void updateStudentProfile(UpdateStudentProfileRequest updateProfileRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        Student student = studentRepository.findByEmail(currentUsername)
                .orElseThrow(() -> new RuntimeException("Student not found with email: " + currentUsername));

        student.setFullName(updateProfileRequest.getFullName());
        student.setAddress(updateProfileRequest.getAddress());

        studentRepository.save(student);
        String token = jwtUtils.generateJwtToken(currentUsername);
        emailService.sendConfirmationEmailUpdateProfile(currentUsername, token);
    }

    public void updateInstructorProfile(UpdateInstructorProfileRequest updateProfileRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        Instructor instructor = instructorRepository.findByEmail(currentUsername)
                .orElseThrow(() -> new RuntimeException("Instructor not found with email: " + currentUsername));

        instructor.setFullName(updateProfileRequest.getFullName());
        instructor.setAddress(updateProfileRequest.getAddress());
        instructor.setExperience(updateProfileRequest.getExperience());
        instructor.setSalary(updateProfileRequest.getSalary());

        instructorRepository.save(instructor);
        String token = jwtUtils.generateJwtToken(currentUsername);
        emailService.sendConfirmationEmailUpdateProfile(currentUsername, token);
    }

    public void registerInstructor(SignUpRequest signUpRequest) {
        if (emailExists(signUpRequest.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already exists.");
        }


        Instructor instructor = new Instructor(
                signUpRequest.getFullName(),
                signUpRequest.getEmail(),
                passwordEncoder.encode(signUpRequest.getPassword()),
                LocalDate.now(),
                signUpRequest.getAddress(),
                false,  // isBlocked
                false,  // isApproved
                "INSTRUCTOR",
                signUpRequest.getExperience(),
                signUpRequest.getSalary()
        );

        userRepository.save(instructor);
        // Send confirmation email
        emailService.sendApplicationUnderReviewEmail(signUpRequest.getEmail());
    }

    public void registerStudent(SignUpStudentRequest signUpRequest) {

        if (emailExists(signUpRequest.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already exists.");
        }

        Student student = new Student(
                signUpRequest.getFullName(),
                signUpRequest.getEmail(),
                passwordEncoder.encode(signUpRequest.getPassword()),
                LocalDate.now(),
                signUpRequest.getAddress(),
                false,  // isBlocked
                false,  // isApproved
                "STUDENT"
        );

        userRepository.save(student);
        // Send confirmation email
        String token = jwtUtils.generateJwtToken(signUpRequest.getEmail()); // Tạo token xác nhận email
        emailService.sendConfirmationEmail(signUpRequest.getEmail(), token);
    }

    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            return new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(), userDetails.getFullName());
        } catch (BadCredentialsException ex) {
            logger.error("Failed login attempt for username: " + loginRequest.getEmail(), ex);
            throw ex;
        }
    }


    public boolean changePassword(ChangePasswordRequest changePasswordRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            throw new RuntimeException("User is not authenticated");
        }
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User Not Found with email: " + email));

        if (!passwordEncoder.matches(changePasswordRequest.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect.");
        }

        if (!changePasswordRequest.getNewPassword().equals(changePasswordRequest.getConfirmPassword())) {
            throw new IllegalArgumentException("New password and confirm password do not match!");
        }

        if (passwordEncoder.matches(changePasswordRequest.getNewPassword(), user.getPassword())) {
            throw new IllegalArgumentException("MSG 22 : Mật khẩu mới không được trùng với mật khẩu hiện tại.");
        }

        user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        userRepository.save(user);
        // Send confirmation email
        emailService.sendSimpleMessage(email, "Password Changed Successfully", "Your password has been changed successfully.");

        return true;
    }

    public boolean forgotPassword(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (!userOptional.isPresent()) {
            return false;
        }

        User user = userOptional.get();
        String token = jwtUtils.generateJwtToken(email); // Generate token
        emailService.sendForgotPasswordEmail(email, token);
        return true;
    }

    public boolean resetPassword(ResetPasswordRequest resetPasswordRequest) {
        String email = jwtUtils.getUserNameFromJwtToken(resetPasswordRequest.getToken());
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User Not Found with email: " + email));

        user.setPassword(passwordEncoder.encode(resetPasswordRequest.getNewPassword()));
        userRepository.save(user);
        return true;
    }

    public boolean verifyEmailToken(String token) {
        String email = jwtUtils.getUserNameFromJwtToken(token);
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User Not Found with email: " + email));

        if (user.isApproved()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Account already activated.");
        }
        user.setApproved(true);
        userRepository.save(user);
        return true;
    }

    public void approveInstructor(Integer instructorId) {

        Instructor instructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> new RuntimeException("Instructor not found with id: " + instructorId));
        if (instructor.isApproved()) {
            throw new IllegalStateException("Instructor already approved.");
        }
        instructor.setApproved(true);
        instructorRepository.save(instructor);
        // Gửi email xác nhận
        emailService.sendApprovalEmail(instructor.getEmail());
    }

    public void rejectInstructor(Integer instructorId) {

        Instructor instructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> new RuntimeException("Instructor not found with id: " + instructorId));
        if (instructor.isApproved()) {
            throw new IllegalStateException("Instructor already approved.");
        }
        instructor.setApproved(false);

        instructorRepository.save(instructor);
        // Gửi email xác nhận
        emailService.sendRejectionEmail(instructor.getEmail());
    }

    public void logout(String token) {
        tokenBlacklistService.blacklistToken(token);
    }

}
