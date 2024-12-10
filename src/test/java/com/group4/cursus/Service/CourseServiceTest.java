package com.group4.cursus.service;

import com.group4.cursus.dto.*;
import com.group4.cursus.entity.*;
import com.group4.cursus.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private CourseService courseService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetCourseById_Success() throws Exception {
        Course course = createCourse();
        when(courseRepository.findById(anyLong())).thenReturn(Optional.of(course));

        CourseDTO courseDTO = courseService.getCourseById(1L);

        assertNotNull(courseDTO);
        assertEquals(course.getCourseId(), courseDTO.getCourseId());
    }

    @Test
    public void testGetCoursesByCategory() {
        Course course = createCourse();
        Page<Course> coursesPage = new PageImpl<>(Collections.singletonList(course));
        when(courseRepository.findByCategoryId(anyLong(), any(Pageable.class))).thenReturn(coursesPage);

        Page<CourseDTO> result = courseService.getCoursesByCategory(1L, 0, 2);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    public void testGetCoursesBySubcategory() {
        Course course = createCourse();
        Page<Course> coursesPage = new PageImpl<>(Collections.singletonList(course));
        when(courseRepository.findBySubcategoryId(anyLong(), any(Pageable.class))).thenReturn(coursesPage);

        Page<CourseDTO> result = courseService.getCoursesBySubcategory(1L, 0, 2);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    public void testSearchCourses() {
        Course course = createCourse();
        Page<Course> coursesPage = new PageImpl<>(Collections.singletonList(course));
        when(courseRepository.searchByKeyword(anyString(), any(Pageable.class))).thenReturn(coursesPage);

        Page<CourseDTO> result = courseService.searchCourses("keyword", 0, 2, "courseTitle");

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    public void testGetCourseAnalytics_Success() throws Exception {
        Course course = createCourse();
        when(courseRepository.findById(anyLong())).thenReturn(Optional.of(course));
        when(reviewRepository.findByCourse_CourseId(anyLong())).thenReturn(Collections.emptyList());
        when(enrollmentRepository.findByCourse_CourseId(anyLong())).thenReturn(Collections.emptyList());

        CourseAnalyticsDTO result = courseService.getCourseAnalytics(1L, "instructor@example.com");

        assertNotNull(result);
        assertEquals(course.getCourseId(), result.getCourseId());
    }

    private Course createCourse() {
        Instructor instructor = new Instructor();
        instructor.setUserId(1);
        instructor.setFullName("John Doe");
        instructor.setEmail("instructor@example.com");
        instructor.setAddress("123 Main St");
        instructor.setSalary(BigDecimal.valueOf(1000));
        instructor.setApproved(true);

        SubCategory subCategory = new SubCategory();
        subCategory.setSubcategoryId(1L);
        subCategory.setSubcategoryName("Java");

        Course course = new Course();
        course.setCourseId(1L);
        course.setCourseTitle("Java Basics");
        course.setDescription("Learn Java from scratch");
        course.setRequirements("Basic programming knowledge");
        course.setCourseLevel("Beginner");
        course.setRegularPrice(BigDecimal.valueOf(199.99));
        course.setSubCategory(subCategory);
        course.setInstructor(instructor);
        course.setStatus("Active");

        return course;
    }
}
