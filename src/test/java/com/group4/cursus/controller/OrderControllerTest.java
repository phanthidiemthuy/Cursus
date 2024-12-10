package com.group4.cursus.controller;

import com.group4.cursus.dto.PaymentRestDTO;
import com.group4.cursus.entity.Cart;
import com.group4.cursus.entity.Order;
import com.group4.cursus.entity.Student;
import com.group4.cursus.repository.*;
import com.group4.cursus.service.VNPayService;
import com.group4.cursus.service.ZaloPayService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class OrderControllerTest {

    @InjectMocks
    private OrderController orderController;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private VNPayService vnPayService;

    @Mock
    private ZaloPayService zaloPayService;

    @Mock
    private MockHttpServletRequest request;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCheckout_withZaloPay() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("studentId", 1L);

        Student student = new Student();
        student.setUserId(1);
        when(studentRepository.findById(1)).thenReturn(Optional.of(student));

        Cart cartItem = new Cart();
        cartItem.setPrice(new BigDecimal("100.00"));
        when(cartRepository.findByStudent(any(Student.class))).thenReturn(Collections.singletonList(cartItem));

        Order order = new Order();
        order.setOrderId(1L);
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Map<String, Object> zaloPayResponse = new HashMap<>();
        zaloPayResponse.put("returncode", 1);
        zaloPayResponse.put("orderurl", "http://zalopay.vn");
        when(zaloPayService.createPayment(any(), any(), any())).thenReturn(zaloPayResponse);

        ResponseEntity<?> response = orderController.checkout("zalopay", session, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        PaymentRestDTO body = (PaymentRestDTO) response.getBody();
        assertEquals("Ok", body.getStatus());
        assertEquals("Successfully", body.getMessage());
        assertEquals("http://zalopay.vn", body.getURL());
    }

    @Test
    public void testCheckout_withVNPay() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("studentId", 1L);

        Student student = new Student();
        student.setUserId(1);
        when(studentRepository.findById(1)).thenReturn(Optional.of(student));

        Cart cartItem = new Cart();
        cartItem.setPrice(new BigDecimal("100.00"));
        when(cartRepository.findByStudent(any(Student.class))).thenReturn(Collections.singletonList(cartItem));

        Order order = new Order();
        order.setOrderId(1L);
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        when(vnPayService.createOrder(any(), any(), any(), any(), any())).thenReturn("http://vnpay.vn");

        ResponseEntity<?> response = orderController.checkout("vnpay", session, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        PaymentRestDTO body = (PaymentRestDTO) response.getBody();
        assertEquals("Ok", body.getStatus());
        assertEquals("Successfully", body.getMessage());
        assertEquals("http://vnpay.vn", body.getURL());
    }

    @Test
    public void testCheckout_noStudentIdInSession() throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        MockHttpSession session = new MockHttpSession();

        ResponseEntity<?> response = orderController.checkout("zalopay", session, request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Student not authenticated", response.getBody());
    }

    @Test
    public void testCheckout_emptyCart() throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("studentId", 1L);

        Student student = new Student();
        student.setUserId(1);
        when(studentRepository.findById(1)).thenReturn(Optional.of(student));

        when(cartRepository.findByStudent(any(Student.class))).thenReturn(Collections.emptyList());

        ResponseEntity<?> response = orderController.checkout("zalopay", session, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Giỏ hàng trống", response.getBody());
    }
}
