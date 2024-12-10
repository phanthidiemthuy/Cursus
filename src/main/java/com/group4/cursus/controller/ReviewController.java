package com.group4.cursus.controller;

import com.group4.cursus.dto.CourseAnalyticsDTO;
import com.group4.cursus.dto.ReviewDTO;
import com.group4.cursus.service.CourseService;
import com.group4.cursus.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private CourseService courseService;

    @GetMapping("/{courseId}/reviews")
    public ResponseEntity<?> getReviewsByCourseId(@PathVariable Long courseId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String instructorEmail = authentication.getName();
        try {
            List<ReviewDTO> reviews = reviewService.getReviewsForCourse(courseId, instructorEmail);
            return ResponseEntity.ok(reviews);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/{courseId}/analytics")
    public ResponseEntity<?> getCourseAnalytics(@PathVariable Long courseId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String instructorEmail = authentication.getName();
        try {
            CourseAnalyticsDTO courseAnalyticsDTO = courseService.getCourseAnalytics(courseId, instructorEmail);
            return ResponseEntity.ok(courseAnalyticsDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}
