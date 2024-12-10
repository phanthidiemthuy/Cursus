package com.group4.cursus.service;

import com.group4.cursus.dto.CourseDTO;
import com.group4.cursus.entity.Course;
import com.group4.cursus.entity.Enrollment;
import com.group4.cursus.repository.EnrollmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnrollmentServiceTest {

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @InjectMocks
    private EnrollmentService enrollmentService;


    @Test
    void getCoursesByStudentIdTest() {
        // Create
        Long studentId = 1L;
        Course course1 = new Course();
        course1.setCourseTitle("Course 1");
        course1.setDescription("Description 1");
        course1.setRequirements("Requirements 1");
        course1.setCourseLevel("Beginner");
        course1.setRegularPrice(BigDecimal.valueOf(100));
        course1.setStatus("Active");

        Course course2 = new Course();
        course2.setCourseTitle("Course 2");
        course2.setDescription("Description 2");
        course2.setRequirements("Requirements 2");
        course2.setCourseLevel("Intermediate");
        course2.setRegularPrice(BigDecimal.valueOf(200));
        course2.setStatus("Active");

        Enrollment enrollment1 = new Enrollment();
        enrollment1.setCourse(course1);
        Enrollment enrollment2 = new Enrollment();
        enrollment2.setCourse(course2);


        List<Enrollment> enrollments = Arrays.asList(enrollment1, enrollment2);
        when(enrollmentRepository.findCourseByStudentId(studentId)).thenReturn(enrollments);
        List<CourseDTO> courseDTOs = enrollmentService.getCoursesByStudentId(studentId);
        assertEquals(2, courseDTOs.size());
        assertEquals("Course 1", courseDTOs.get(0).getCourseTitle());
        assertEquals("Course 2", courseDTOs.get(1).getCourseTitle());
    }
}
