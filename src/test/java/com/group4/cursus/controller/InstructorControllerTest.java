package com.group4.cursus.controller;

import com.group4.cursus.dto.*;
import com.group4.cursus.entity.Course;
import com.group4.cursus.entity.Instructor;
import com.group4.cursus.entity.Payout;
import com.group4.cursus.repository.InstructorRepository;
import com.group4.cursus.repository.PayoutRepository;
import com.group4.cursus.service.InstructorService;
import jakarta.servlet.http.HttpSession;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class InstructorControllerTest {

    @Mock
    private InstructorService instructorService;

    @Mock
    private PayoutRepository payoutRepository;

    @Mock
    private InstructorRepository instructorRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private InstructorController instructorController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
    }

    @Test
    public void testGetCourseById() throws Exception {
        Long courseId = 1L;
        CourseDTO courseDTO = new CourseDTO();
        when(instructorService.getCourseById(courseId)).thenReturn(courseDTO);

        ResponseEntity<?> response = instructorController.getCourseById(courseId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(courseDTO, response.getBody());
    }

    @Test
    public void testCreateCourse() throws Exception {
        CreateCourseRequest createCourseRequest = new CreateCourseRequest();
        String instructorEmail = "test@example.com";
        Instructor instructor = new Instructor();
        instructor.setApproved(true);

        when(authentication.getName()).thenReturn(instructorEmail);
        when(instructorRepository.findByEmail(instructorEmail)).thenReturn(Optional.of(instructor));
        Course course = new Course();
        when(instructorService.createCourse(any(CreateCourseRequest.class), eq(instructorEmail))).thenReturn(course);

        ResponseEntity<?> response = instructorController.createCourse(createCourseRequest);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(course, response.getBody());
    }

    @Test
    public void testCreateCourseInstructorNotFound() {
        CreateCourseRequest createCourseRequest = new CreateCourseRequest();
        String instructorEmail = "test@example.com";

        when(authentication.getName()).thenReturn(instructorEmail);
        when(instructorRepository.findByEmail(instructorEmail)).thenReturn(Optional.empty());

        ResponseEntity<?> response = instructorController.createCourse(createCourseRequest);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody() instanceof MyCustomResponse);
        assertEquals("Instructor not found", ((MyCustomResponse) response.getBody()).getMessage());
    }

    @Test
    public void testUpdateCourse() throws Exception {
        Long courseId = 1L;
        EditCourseRequest updateCourseRequest = new EditCourseRequest();
        String instructorEmail = "test@example.com";
        Instructor instructor = new Instructor();
        instructor.setApproved(true);

        when(authentication.getName()).thenReturn(instructorEmail);
        when(instructorRepository.findByEmail(instructorEmail)).thenReturn(Optional.of(instructor));
        Course course = new Course();
        when(instructorService.updateCourse(eq(courseId), any(EditCourseRequest.class), eq(instructorEmail))).thenReturn(course);

        ResponseEntity<?> response = instructorController.updateCourse(courseId, updateCourseRequest);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(course, response.getBody());
    }

    @Test
    public void testDeleteCourse() throws Exception {
        Long courseId = 1L;
        String instructorEmail = "test@example.com";
        Instructor instructor = new Instructor();
        instructor.setApproved(true);

        when(authentication.getName()).thenReturn(instructorEmail);
        when(instructorRepository.findByEmail(instructorEmail)).thenReturn(Optional.of(instructor));

        doNothing().when(instructorService).deleteCourse(courseId, instructorEmail);

        ResponseEntity<?> response = instructorController.deleteCourse(courseId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Course deleted successfully", response.getBody());
    }

    @Test
    public void testCreatePayoutRequest() {
        HttpSession session = mock(HttpSession.class);
        BigDecimal amount = new BigDecimal("100");
        Long instructorId = 1L;
        Instructor instructor = new Instructor();
        instructor.setSalary(new BigDecimal("200"));

        when(session.getAttribute("instructorId")).thenReturn(instructorId);
        when(instructorRepository.findById(instructorId.intValue())).thenReturn(Optional.of(instructor));

        ResponseEntity<MyCustomResponse> response = instructorController.createPayoutRequest(amount, session);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Payout request created successfully", response.getBody().getMessage());
        assertTrue(response.getBody().isSuccess());
    }

    @Test
    public void testCreatePayoutRequestInsufficientBalance() {
        HttpSession session = mock(HttpSession.class);
        BigDecimal amount = new BigDecimal("300");
        Long instructorId = 1L;
        Instructor instructor = new Instructor();
        instructor.setSalary(new BigDecimal("200"));

        when(session.getAttribute("instructorId")).thenReturn(instructorId);
        when(instructorRepository.findById(instructorId.intValue())).thenReturn(Optional.of(instructor));

        ResponseEntity<MyCustomResponse> response = instructorController.createPayoutRequest(amount, session);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Số dư không đủ để thực hiện payout", response.getBody().getMessage());
        assertTrue(!response.getBody().isSuccess());
    }
}
