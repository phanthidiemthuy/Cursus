package com.group4.cursus.controller;

import com.group4.cursus.dto.ReportDTO;
import com.group4.cursus.dto.ReviewDTO;
import com.group4.cursus.entity.Instructor;
import com.group4.cursus.service.ReportService;
import com.group4.cursus.service.ReviewService;
import com.group4.cursus.service.StudentService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/student")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ReportService reportService;
    @GetMapping("/search-instructor")
    public ResponseEntity<List<Instructor>> findInstructorByName(@AuthenticationPrincipal UserDetails userDetails,
                                                                 @RequestParam("fullName") String fullName){
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(studentService.findInstructorByName(fullName));
    }

    @GetMapping("/{courseId}/reviews")
    public ResponseEntity<?> getReviewsForCourse(@PathVariable Long courseId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String instructorEmail = authentication.getName();
        try {
            List<ReviewDTO> reviews = reviewService.getReviewsForCourse(courseId, instructorEmail);
            return ResponseEntity.ok(reviews);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/{courseId}/reviews")
    public ResponseEntity<?> addReview(@PathVariable Long courseId, @RequestBody ReviewDTO reviewDTO, HttpSession session) {
        Long studentId = (Long) session.getAttribute("studentId");
        if (studentId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You must be logged in to review a course.");
        }

        try {
            ReviewDTO savedReview = reviewService.addReview(courseId, studentId, reviewDTO);
            return ResponseEntity.ok(savedReview);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/report/{courseId}")
    public ResponseEntity<?> addReport(@PathVariable Long courseId, @RequestBody ReportDTO reportDTO, HttpSession session) throws Exception {
        Long studentId = (Long) session.getAttribute("studentId");
        if (studentId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You must be logged in to review a course.");
        }
        try {
            ReportDTO reportDTO1 = reportService.createReport(courseId, studentId, reportDTO);
            return ResponseEntity.ok(reportDTO1);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}
