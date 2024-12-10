package com.group4.cursus.controller;

import com.group4.cursus.dto.ReportDTO;
import com.group4.cursus.dto.ReviewDTO;
import com.group4.cursus.entity.Instructor;
import com.group4.cursus.service.ReportService;
import com.group4.cursus.service.ReviewService;
import com.group4.cursus.service.StudentService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class StudentControllerTest {

    @Mock
    private StudentService studentService;

    @Mock
    private ReviewService reviewService;

    @Mock
    private ReportService reportService;

    @Mock
    private HttpSession session;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private StudentController studentController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
    }

    @Test
    public void testFindInstructorByName() {
        String fullName = "John Doe";
        UserDetails userDetails = mock(UserDetails.class);
        List<Instructor> instructors = Collections.singletonList(new Instructor());

        when(studentService.findInstructorByName(fullName)).thenReturn(instructors);

        ResponseEntity<List<Instructor>> response = studentController.findInstructorByName(userDetails, fullName);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(instructors, response.getBody());
    }

    @Test
    public void testFindInstructorByNameUnauthorized() {
        String fullName = "John Doe";

        ResponseEntity<List<Instructor>> response = studentController.findInstructorByName(null, fullName);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    public void testGetReviewsForCourse() throws Exception {
        Long courseId = 1L;
        String instructorEmail = "instructor@example.com";
        List<ReviewDTO> reviews = Collections.singletonList(new ReviewDTO());

        when(authentication.getName()).thenReturn(instructorEmail);
        when(reviewService.getReviewsForCourse(courseId, instructorEmail)).thenReturn(reviews);

        ResponseEntity<?> response = studentController.getReviewsForCourse(courseId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(reviews, response.getBody());
    }

    @Test
    public void testGetReviewsForCourseException() throws Exception {
        Long courseId = 1L;
        String instructorEmail = "instructor@example.com";

        when(authentication.getName()).thenReturn(instructorEmail);
        when(reviewService.getReviewsForCourse(courseId, instructorEmail)).thenThrow(new RuntimeException("Error"));

        ResponseEntity<?> response = studentController.getReviewsForCourse(courseId);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Error: Error", response.getBody());
    }

    @Test
    public void testAddReview() throws Exception {
        Long courseId = 1L;
        Long studentId = 1L;
        ReviewDTO reviewDTO = new ReviewDTO();
        ReviewDTO savedReview = new ReviewDTO();

        when(session.getAttribute("studentId")).thenReturn(studentId);
        when(reviewService.addReview(courseId, studentId, reviewDTO)).thenReturn(savedReview);

        ResponseEntity<?> response = studentController.addReview(courseId, reviewDTO, session);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(savedReview, response.getBody());
    }

    @Test
    public void testAddReviewUnauthorized() {
        Long courseId = 1L;
        ReviewDTO reviewDTO = new ReviewDTO();

        when(session.getAttribute("studentId")).thenReturn(null);

        ResponseEntity<?> response = studentController.addReview(courseId, reviewDTO, session);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("You must be logged in to review a course.", response.getBody());
    }

    @Test
    public void testAddReviewException() throws Exception {
        Long courseId = 1L;
        Long studentId = 1L;
        ReviewDTO reviewDTO = new ReviewDTO();

        when(session.getAttribute("studentId")).thenReturn(studentId);
        when(reviewService.addReview(courseId, studentId, reviewDTO)).thenThrow(new RuntimeException("Error"));

        ResponseEntity<?> response = studentController.addReview(courseId, reviewDTO, session);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Error: Error", response.getBody());
    }

    @Test
    public void testAddReport() throws Exception {
        Long courseId = 1L;
        Long studentId = 1L;
        ReportDTO reportDTO = new ReportDTO();
        ReportDTO savedReport = new ReportDTO();

        when(session.getAttribute("studentId")).thenReturn(studentId);
        when(reportService.createReport(courseId, studentId, reportDTO)).thenReturn(savedReport);

        ResponseEntity<?> response = studentController.addReport(courseId, reportDTO, session);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(savedReport, response.getBody());
    }

    @Test
    public void testAddReportUnauthorized() throws Exception {
        Long courseId = 1L;
        ReportDTO reportDTO = new ReportDTO();

        when(session.getAttribute("studentId")).thenReturn(null);

        ResponseEntity<?> response = studentController.addReport(courseId, reportDTO, session);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("You must be logged in to review a course.", response.getBody());
    }

    @Test
    public void testAddReportException() throws Exception {
        Long courseId = 1L;
        Long studentId = 1L;
        ReportDTO reportDTO = new ReportDTO();

        when(session.getAttribute("studentId")).thenReturn(studentId);
        when(reportService.createReport(courseId, studentId, reportDTO)).thenThrow(new RuntimeException("Error"));

        ResponseEntity<?> response = studentController.addReport(courseId, reportDTO, session);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Error: Error", response.getBody());
    }
}
