package com.group4.cursus.controller;

import com.group4.cursus.dto.CourseDTO;
import com.group4.cursus.service.EnrollmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class EnrollmentControllerTest {

    @Mock
    private EnrollmentService enrollmentService;

    @InjectMocks
    private EnrollmentController enrollmentController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetCoursesByStudentId() {
        Long studentId = 1L;
        List<CourseDTO> courses = Collections.singletonList(new CourseDTO());

        when(enrollmentService.getCoursesByStudentId(studentId)).thenReturn(courses);

        List<CourseDTO> result = enrollmentController.getCoursesByStudentId(studentId);
        assertEquals(courses, result);
        verify(enrollmentService, times(1)).getCoursesByStudentId(studentId);
    }
}
