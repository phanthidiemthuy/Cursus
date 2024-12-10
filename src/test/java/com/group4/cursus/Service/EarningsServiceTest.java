package com.group4.cursus.service;

import com.group4.cursus.dto.CourseEarningDTO;
import com.group4.cursus.dto.EarningsDTO;
import com.group4.cursus.entity.Course;
import com.group4.cursus.entity.Order;
import com.group4.cursus.entity.OrderItem;
import com.group4.cursus.repository.CourseRepository;
import com.group4.cursus.repository.OrderItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EarningsServiceTest {

    @InjectMocks
    private EarningsService earningsService;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetEarningsByInstructor_Success() throws Exception {
        String instructorEmail = "instructor@example.com";

        Course course1 = new Course();
        course1.setCourseId(1L);
        course1.setCourseTitle("Course 1");

        Course course2 = new Course();
        course2.setCourseId(2L);
        course2.setCourseTitle("Course 2");

        Order order = new Order();
        order.setOrderDate(LocalDate.of(2023, 7, 1));

        OrderItem orderItem1 = new OrderItem();
        orderItem1.setCourse(course1);
        orderItem1.setOrder(order);
        orderItem1.setUnitPrice(new BigDecimal("100.00"));

        OrderItem orderItem2 = new OrderItem();
        orderItem2.setCourse(course2);
        orderItem2.setOrder(order);
        orderItem2.setUnitPrice(new BigDecimal("200.00"));

        when(courseRepository.findByInstructor_Email(instructorEmail)).thenReturn(Arrays.asList(course1, course2));
        when(orderItemRepository.findByCourseCourseIdIn(Arrays.asList(1L, 2L))).thenReturn(Arrays.asList(orderItem1, orderItem2));

        List<EarningsDTO> earningsDTOs = earningsService.getEarningsByInstructor(instructorEmail);

        assertNotNull(earningsDTOs);
        assertEquals(1, earningsDTOs.size()); // This should be 1

        EarningsDTO earningsDTO = earningsDTOs.get(0);
        assertEquals("July 2023", earningsDTO.getMonth());
        assertEquals(new BigDecimal("300.00"), earningsDTO.getTotalEarnings());

        List<CourseEarningDTO> courseEarnings = earningsDTO.getCourseEarnings();
        assertEquals(2, courseEarnings.size());

        CourseEarningDTO courseEarningDTO1 = courseEarnings.stream().filter(e -> e.getCourseId().equals(1L)).findFirst().orElse(null);
        assertNotNull(courseEarningDTO1);
        assertEquals(1L, courseEarningDTO1.getCourseId());
        assertEquals("Course 1", courseEarningDTO1.getCourseTitle());
        assertEquals(1, courseEarningDTO1.getUnitsSold());
        assertEquals(new BigDecimal("100.00"), courseEarningDTO1.getEarnings());

        CourseEarningDTO courseEarningDTO2 = courseEarnings.stream().filter(e -> e.getCourseId().equals(2L)).findFirst().orElse(null);
        assertNotNull(courseEarningDTO2);
        assertEquals(2L, courseEarningDTO2.getCourseId());
        assertEquals("Course 2", courseEarningDTO2.getCourseTitle());
        assertEquals(1, courseEarningDTO2.getUnitsSold());
        assertEquals(new BigDecimal("200.00"), courseEarningDTO2.getEarnings());
    }


    @Test
    void testGetEarningsByInstructor_NoCoursesFound() {
        String instructorEmail = "instructor@example.com";

        when(courseRepository.findByInstructor_Email(instructorEmail)).thenReturn(Collections.emptyList());

        Exception exception = assertThrows(Exception.class, () -> {
            earningsService.getEarningsByInstructor(instructorEmail);
        });

        assertEquals("No courses found for the instructor", exception.getMessage());
    }

    @Test
    void testGetEarningsByInstructor_NoOrderItems() throws Exception {
        String instructorEmail = "instructor@example.com";

        Course course1 = new Course();
        course1.setCourseId(1L);
        course1.setCourseTitle("Course 1");

        when(courseRepository.findByInstructor_Email(instructorEmail)).thenReturn(Collections.singletonList(course1));
        when(orderItemRepository.findByCourseCourseIdIn(Collections.singletonList(1L))).thenReturn(Collections.emptyList());

        List<EarningsDTO> earningsDTOs = earningsService.getEarningsByInstructor(instructorEmail);

        assertNotNull(earningsDTOs);
        assertTrue(earningsDTOs.isEmpty());
    }
}
