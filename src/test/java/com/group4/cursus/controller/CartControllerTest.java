package com.group4.cursus.controller;

import com.group4.cursus.dto.CartItemDTO;
import com.group4.cursus.dto.MyCustomResponse;
import com.group4.cursus.entity.Cart;
import com.group4.cursus.entity.Course;
import com.group4.cursus.entity.Student;
import com.group4.cursus.repository.CartRepository;
import com.group4.cursus.repository.CourseRepository;
import com.group4.cursus.repository.EnrollmentRepository;
import com.group4.cursus.repository.StudentRepository;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

public class CartControllerTest {

    @InjectMocks
    private CartController cartController;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private HttpSession session;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAddToCart() {
        Long userId = 1L;
        Long courseId = 1L;
        when(session.getAttribute("studentId")).thenReturn(userId);

        Student student = new Student();
        student.setUserId(userId.intValue());
        when(studentRepository.findById(any(Integer.class))).thenReturn(Optional.of(student));

        Course course = new Course();
        course.setCourseId(courseId);
        course.setDescription("Course Description");
        course.setRegularPrice(BigDecimal.valueOf(100));
        when(courseRepository.findById(any(Long.class))).thenReturn(Optional.of(course));

        when(cartRepository.checkIfCourseInCart(anyLong(), anyLong())).thenReturn(0);
        when(enrollmentRepository.existsByStudentUserIdAndCourseCourseId(anyLong(), anyLong())).thenReturn(false);

        ResponseEntity<MyCustomResponse> response = cartController.addToCart(courseId, session);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Khóa học đã được thêm vào giỏ hàng", response.getBody().getMessage());
    }

    @Test
    public void testCheckUserCartItem() {
        Long userId = 1L;
        Long courseId = 1L;
        when(session.getAttribute("studentId")).thenReturn(userId);

        when(cartRepository.checkIfCourseInCart(anyLong(), anyLong())).thenReturn(1);

        Map<String, Boolean> response = cartController.checkUserCartItem(courseId, session);

        assertEquals(true, response.get("inCart"));
    }

    @Test
    public void testGetAllMyCartItems() {
        Long userId = 1L;
        when(session.getAttribute("studentId")).thenReturn(userId);

        Cart cart = new Cart();
        cart.setCourse(new Course());
        Page<Cart> cartPage = new PageImpl<>(Arrays.asList(cart));

        when(cartRepository.findByStudentUserId(anyLong(), any(Pageable.class))).thenReturn(cartPage);

        Page<CartItemDTO> response = cartController.getAllMyCartItems(0, session);

        assertEquals(1, response.getTotalElements());
    }

    @Test
    public void testGetMyCartBill() {
        Long userId = 1L;
        when(session.getAttribute("studentId")).thenReturn(userId);

        BigDecimal totalPrice = BigDecimal.valueOf(200);
        when(cartRepository.getTotalBillForUser(anyLong())).thenReturn(totalPrice);

        Map<String, BigDecimal> response = cartController.getMyCartBill(session);

        assertEquals(totalPrice, response.get("totalPrice"));
    }

    @Test
    public void testCountMyCartItems() {
        Long userId = 1L;
        when(session.getAttribute("studentId")).thenReturn(userId);

        long cartCount = 5;
        when(cartRepository.countCartByStudentUserId(anyLong())).thenReturn(cartCount);

        Map<String, Long> response = cartController.countMyCartItems(session);

        assertEquals(cartCount, response.get("cartCount"));
    }

    @Test
    public void testRemoveCartByCourseId() {
        Long userId = 1L;
        Long courseId = 1L;
        when(session.getAttribute("studentId")).thenReturn(userId);

        when(cartRepository.deleteByStudentUserIdAndCourseCourseId(any(Integer.class), any(Long.class))).thenReturn(1);

        ResponseEntity<MyCustomResponse> response = cartController.removeCartByCourseId(courseId, session);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Removed from Cart, course " + courseId, response.getBody().getMessage());
    }
}
