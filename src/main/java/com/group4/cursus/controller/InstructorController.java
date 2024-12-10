package com.group4.cursus.controller;

import com.group4.cursus.dto.*;
import com.group4.cursus.entity.Course;
import com.group4.cursus.entity.Instructor;
import com.group4.cursus.entity.Payout;
import com.group4.cursus.repository.InstructorRepository;
import com.group4.cursus.repository.PayoutRepository;
import com.group4.cursus.service.InstructorService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@RestController
@RequestMapping("/api/instructor/")
@PreAuthorize("hasRole('INSTRUCTOR')")
public class InstructorController {

    @Autowired
    private InstructorService instructorService;

    @Autowired
    private PayoutRepository payoutRepository;

    @Autowired
    private InstructorRepository instructorRepository;

    @GetMapping("/courses/{courseId}")
    public ResponseEntity<?> getCourseById(@PathVariable Long courseId) {
        try {
            CourseDTO courseDTO = instructorService.getCourseById(courseId);
            return ResponseEntity.ok(courseDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
    @PostMapping("/courses/create")
    public ResponseEntity<?> createCourse(@RequestBody CreateCourseRequest createCourseRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String instructorEmail = authentication.getName();
        Optional<Instructor> instructor = instructorRepository.findByEmail(instructorEmail);
        if(instructor.isEmpty()){
            return ResponseEntity.badRequest().body(new MyCustomResponse("Instructor not found", false));
        }
        if(!instructor.get().isApproved()){
            return ResponseEntity.badRequest().body(new MyCustomResponse("Instructor not approved", false));
        }
        try {
            Course course = instructorService.createCourse(createCourseRequest, instructorEmail);
            return ResponseEntity.ok(course);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PutMapping("/courses/{courseId}")
    public ResponseEntity<?> updateCourse(@PathVariable Long courseId, @RequestBody EditCourseRequest updateCourseRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String instructorEmail = authentication.getName();
        Optional<Instructor> instructor = instructorRepository.findByEmail(instructorEmail);
        if(instructor.isEmpty()){
            return ResponseEntity.badRequest().body(new MyCustomResponse("Instructor not found", false));
        }
        if(!instructor.get().isApproved()){
            return ResponseEntity.badRequest().body(new MyCustomResponse("Instructor not approved", false));
        }
        try {
            Course course = instructorService.updateCourse(courseId, updateCourseRequest, instructorEmail);
            return ResponseEntity.ok(course);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
    @PutMapping("/courses/{courseId}/resubmit")
    public ResponseEntity<?> resubmitCourse(@PathVariable Long courseId, @RequestBody ReSubmitCourseRequest reSubmitCourseRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String instructorEmail = authentication.getName();
        Optional<Instructor> instructor = instructorRepository.findByEmail(instructorEmail);
        if(instructor.isEmpty()){
            return ResponseEntity.badRequest().body(new MyCustomResponse("Instructor not found", false));
        }
        if(!instructor.get().isApproved()){
            return ResponseEntity.badRequest().body(new MyCustomResponse("Instructor not approved", false));
        }
        try {
            Course course = instructorService.resubmitCourse(courseId, reSubmitCourseRequest, instructorEmail);
            return ResponseEntity.ok(course);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
    @DeleteMapping("/courses/{courseId}")
    public ResponseEntity<?> deleteCourse(@PathVariable Long courseId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String instructorEmail = authentication.getName();
        Optional<Instructor> instructor = instructorRepository.findByEmail(instructorEmail);
        if(instructor.isEmpty()){
            return ResponseEntity.badRequest().body(new MyCustomResponse("Instructor not found", false));
        }
        if(!instructor.get().isApproved()){
            return ResponseEntity.badRequest().body(new MyCustomResponse("Instructor not approved", false));
        }
        try {
            instructorService.deleteCourse(courseId, instructorEmail);
            return ResponseEntity.ok("Course deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/payout")
    public ResponseEntity<MyCustomResponse> createPayoutRequest(@RequestParam BigDecimal amount, HttpSession session) {
        Long instructorId = (Long) session.getAttribute("instructorId");
        Instructor instructor = instructorRepository.findById(Math.toIntExact(instructorId))
                .orElseThrow(() -> new RuntimeException("Instructor not found"));

        // Kiểm tra số dư của instructor
        BigDecimal newSalary = instructor.getSalary().subtract(amount);
        if (newSalary.compareTo(BigDecimal.ZERO) < 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).
                    body(new MyCustomResponse("Số dư không đủ để thực hiện payout", false));
        }

        // Trừ số tiền từ salary của instructor
        instructor.setSalary(newSalary);
        instructorRepository.save(instructor);

        // Tạo payout
        Payout payout = new Payout();
        payout.setAmount(amount);
        payout.setPayoutDate(LocalDate.now());
        payout.setStatus("PENDING");
        payout.setInstructor(instructor);

        payoutRepository.save(payout);

        return ResponseEntity.ok(new MyCustomResponse("Payout request created successfully", true));
    }


}
