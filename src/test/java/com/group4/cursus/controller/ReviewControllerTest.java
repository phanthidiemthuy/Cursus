package com.group4.cursus.controller;

import com.group4.cursus.dto.CourseAnalyticsDTO;
import com.group4.cursus.dto.ReviewDTO;
import com.group4.cursus.service.CourseService;
import com.group4.cursus.service.ReviewService;
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

public class ReviewControllerTest {

    @Mock
    private ReviewService reviewService;

    @Mock
    private CourseService courseService;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private ReviewController reviewController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
    }

    @Test
    public void testGetReviewsByCourseId() throws Exception {
        Long courseId = 1L;
        String instructorEmail = "instructor@example.com";
        List<ReviewDTO> reviews = Collections.singletonList(new ReviewDTO());

        when(authentication.getName()).thenReturn(instructorEmail);
        when(reviewService.getReviewsForCourse(courseId, instructorEmail)).thenReturn(reviews);

        ResponseEntity<?> response = reviewController.getReviewsByCourseId(courseId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(reviews, response.getBody());
    }

    @Test
    public void testGetReviewsByCourseIdException() throws Exception {
        Long courseId = 1L;
        String instructorEmail = "instructor@example.com";

        when(authentication.getName()).thenReturn(instructorEmail);
        when(reviewService.getReviewsForCourse(courseId, instructorEmail)).thenThrow(new RuntimeException("Error"));

        ResponseEntity<?> response = reviewController.getReviewsByCourseId(courseId);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Error: Error", response.getBody());
    }

    @Test
    public void testGetCourseAnalytics() throws Exception {
        Long courseId = 1L;
        String instructorEmail = "instructor@example.com";
        CourseAnalyticsDTO courseAnalyticsDTO = new CourseAnalyticsDTO();

        when(authentication.getName()).thenReturn(instructorEmail);
        when(courseService.getCourseAnalytics(courseId, instructorEmail)).thenReturn(courseAnalyticsDTO);

        ResponseEntity<?> response = reviewController.getCourseAnalytics(courseId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(courseAnalyticsDTO, response.getBody());
    }

    @Test
    public void testGetCourseAnalyticsException() throws Exception {
        Long courseId = 1L;
        String instructorEmail = "instructor@example.com";

        when(authentication.getName()).thenReturn(instructorEmail);
        when(courseService.getCourseAnalytics(courseId, instructorEmail)).thenThrow(new RuntimeException("Error"));

        ResponseEntity<?> response = reviewController.getCourseAnalytics(courseId);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Error: Error", response.getBody());
    }
}
