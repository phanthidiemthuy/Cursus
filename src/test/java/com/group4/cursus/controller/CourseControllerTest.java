package com.group4.cursus.controller;

import com.group4.cursus.dto.CourseDTO;
import com.group4.cursus.service.CourseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class CourseControllerTest {

    @Mock
    private CourseService courseService;

    @InjectMocks
    private CourseController courseController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetCourseById() throws Exception {
        Long courseId = 1L;
        CourseDTO courseDTO = new CourseDTO();
        when(courseService.getCourseById(courseId)).thenReturn(courseDTO);

        ResponseEntity<CourseDTO> response = courseController.getCourseById(courseId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(courseDTO, response.getBody());
    }

    @Test
    public void testGetCourseByIdException() throws Exception {
        Long courseId = 1L;
        when(courseService.getCourseById(courseId)).thenThrow(new RuntimeException("Error"));

        ResponseEntity<CourseDTO> response = courseController.getCourseById(courseId);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(null, response.getBody());
    }

    @Test
    public void testGetCoursesByCategory() {
        Long categoryId = 1L;
        Page<CourseDTO> coursePage = new PageImpl<>(Collections.singletonList(new CourseDTO()));
        when(courseService.getCoursesByCategory(categoryId, 0, 10)).thenReturn(coursePage);

        Page<CourseDTO> response = courseController.getCoursesByCategory(categoryId, 0, 10);
        assertEquals(coursePage, response);
    }

    @Test
    public void testGetCoursesBySubcategory() {
        Long subcategoryId = 1L;
        Page<CourseDTO> coursePage = new PageImpl<>(Collections.singletonList(new CourseDTO()));
        when(courseService.getCoursesBySubcategory(subcategoryId, 0, 10)).thenReturn(coursePage);

        Page<CourseDTO> response = courseController.getCoursesBySubcategory(subcategoryId, 0, 10);
        assertEquals(coursePage, response);
    }

    @Test
    public void testSearchCourses() {
        String keyword = "test";
        Page<CourseDTO> coursePage = new PageImpl<>(Collections.singletonList(new CourseDTO()));
        when(courseService.searchCourses(keyword, 0, 10, "courseTitle")).thenReturn(coursePage);

        Page<CourseDTO> response = courseController.searchCourses(keyword, 0, 10, "courseTitle");
        assertEquals(coursePage, response);
    }
}
