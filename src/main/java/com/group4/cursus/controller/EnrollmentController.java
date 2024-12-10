package com.group4.cursus.controller;

import com.group4.cursus.dto.CourseDTO;
import com.group4.cursus.entity.Course;
import com.group4.cursus.service.EnrollmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {

    @Autowired
    private EnrollmentService enrollmentService;

    @GetMapping("/student/{studentId}/courses")
    public List<CourseDTO> getCoursesByStudentId(@PathVariable Long studentId) {
        return enrollmentService.getCoursesByStudentId(studentId);
    }

}
