package com.group4.cursus.service;

import com.group4.cursus.dto.IntroductorDashboardDTO;
import com.group4.cursus.entity.Course;
import com.group4.cursus.entity.OrderItem;
import com.group4.cursus.entity.Review;
import com.group4.cursus.entity.Student;
import com.group4.cursus.repository.CourseRepository;
import com.group4.cursus.repository.OrderItemRepository;
import com.group4.cursus.repository.ReviewRepository;
import com.group4.cursus.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DashboardServiceTest {

    @InjectMocks
    private DashboardService dashboardService;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetDashboardData_Success() throws Exception {
        String instructorEmail = "instructor@example.com";

        Course course1 = new Course();
        course1.setCourseId(1L);
        course1.setRegularPrice(new BigDecimal("100.00"));

        Course course2 = new Course();
        course2.setCourseId(2L);
        course2.setRegularPrice(new BigDecimal("200.00"));

        OrderItem orderItem1 = new OrderItem();
        orderItem1.setUnitPrice(new BigDecimal("100.00"));

        OrderItem orderItem2 = new OrderItem();
        orderItem2.setUnitPrice(new BigDecimal("200.00"));

        Student student1 = new Student();
        student1.setRegistrationDate(LocalDate.now());

        Student student2 = new Student();
        student2.setRegistrationDate(LocalDate.now().minusMonths(1));

        Review review1 = new Review();
        review1.setRating(4);

        Review review2 = new Review();
        review2.setRating(5);

        when(courseRepository.findByInstructor_Email(instructorEmail)).thenReturn(Arrays.asList(course1, course2));
        when(orderItemRepository.findByCourseCourseIdIn(Arrays.asList(1L, 2L))).thenReturn(Arrays.asList(orderItem1, orderItem2));
        when(studentRepository.findAll()).thenReturn(Arrays.asList(student1, student2));
        when(reviewRepository.findByCourseCourseIdIn(Arrays.asList(1L, 2L))).thenReturn(Arrays.asList(review1, review2));

        IntroductorDashboardDTO dashboardDTO = dashboardService.getDashboardData(instructorEmail);

        assertNotNull(dashboardDTO);
        assertEquals(new BigDecimal("300.00"), dashboardDTO.getTotalSales());
        assertEquals(2, dashboardDTO.getTotalEnrollments());
        assertEquals(2, dashboardDTO.getTotalCourses());
        assertEquals(2, dashboardDTO.getTotalStudents());
        assertEquals(2, dashboardDTO.getTotalReviews());
        assertEquals(4.5, dashboardDTO.getAverageRating());
        assertEquals(new BigDecimal("150.00"), dashboardDTO.getAverageCoursePrice());
        assertEquals(1, dashboardDTO.getNewStudentsThisMonth());
    }

    @Test
    void testGetDashboardData_NoCoursesFound() {
        String instructorEmail = "instructor@example.com";

        when(courseRepository.findByInstructor_Email(instructorEmail)).thenReturn(Collections.emptyList());

        Exception exception = assertThrows(Exception.class, () -> {
            dashboardService.getDashboardData(instructorEmail);
        });

        assertEquals("No courses found for the instructor", exception.getMessage());
    }

    @Test
    void testGetDashboardData_NoOrderItems() throws Exception {
        String instructorEmail = "instructor@example.com";

        Course course1 = new Course();
        course1.setCourseId(1L);
        course1.setRegularPrice(new BigDecimal("100.00"));

        when(courseRepository.findByInstructor_Email(instructorEmail)).thenReturn(Collections.singletonList(course1));
        when(orderItemRepository.findByCourseCourseIdIn(Collections.singletonList(1L))).thenReturn(Collections.emptyList());
        when(studentRepository.findAll()).thenReturn(Collections.emptyList());
        when(reviewRepository.findByCourseCourseIdIn(Collections.singletonList(1L))).thenReturn(Collections.emptyList());

        IntroductorDashboardDTO dashboardDTO = dashboardService.getDashboardData(instructorEmail);

        assertNotNull(dashboardDTO);
        assertEquals(BigDecimal.ZERO, dashboardDTO.getTotalSales());
        assertEquals(0, dashboardDTO.getTotalEnrollments());
        assertEquals(1, dashboardDTO.getTotalCourses());
        assertEquals(0, dashboardDTO.getTotalStudents());
        assertEquals(0, dashboardDTO.getTotalReviews());
        assertEquals(0.0, dashboardDTO.getAverageRating());
        assertEquals(new BigDecimal("100.00"), dashboardDTO.getAverageCoursePrice());
        assertEquals(0, dashboardDTO.getNewStudentsThisMonth());
    }
    @Test
    void testGetDashboardData_NoStudentsFound() throws Exception {
        String instructorEmail = "instructor@example.com";

        Course course1 = new Course();
        course1.setCourseId(1L);
        course1.setRegularPrice(new BigDecimal("100.00"));

        when(courseRepository.findByInstructor_Email(instructorEmail)).thenReturn(Collections.singletonList(course1));
        when(orderItemRepository.findByCourseCourseIdIn(Collections.singletonList(1L))).thenReturn(Collections.emptyList());
        when(studentRepository.findAll()).thenReturn(Collections.emptyList());
        when(reviewRepository.findByCourseCourseIdIn(Collections.singletonList(1L))).thenReturn(Collections.emptyList());

        IntroductorDashboardDTO dashboardDTO = dashboardService.getDashboardData(instructorEmail);

        assertNotNull(dashboardDTO);
        assertEquals(BigDecimal.ZERO, dashboardDTO.getTotalSales());
        assertEquals(0, dashboardDTO.getTotalEnrollments());
        assertEquals(1, dashboardDTO.getTotalCourses());
        assertEquals(0, dashboardDTO.getTotalStudents());
        assertEquals(0, dashboardDTO.getTotalReviews());
        assertEquals(0.0, dashboardDTO.getAverageRating());
        assertEquals(new BigDecimal("100.00"), dashboardDTO.getAverageCoursePrice());
        assertEquals(0, dashboardDTO.getNewStudentsThisMonth());
    }

    @Test
    void testGetDashboardData_NoCoursesToCalculateAveragePrice() throws Exception {
        String instructorEmail = "instructor@example.com";

        when(courseRepository.findByInstructor_Email(instructorEmail)).thenReturn(Collections.emptyList());

        Exception exception = assertThrows(Exception.class, () -> {
            dashboardService.getDashboardData(instructorEmail);
        });

        assertEquals("No courses found for the instructor", exception.getMessage());
    }





}

