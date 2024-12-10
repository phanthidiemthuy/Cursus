package com.group4.cursus.controller;

import com.group4.cursus.config.VNPayConfig;
import com.group4.cursus.config.ZaloPayConfig;
import com.group4.cursus.dto.PaymentRestDTO;
import com.group4.cursus.entity.Cart;
import com.group4.cursus.entity.Order;
import com.group4.cursus.entity.Student;
import com.group4.cursus.repository.*;
import com.group4.cursus.service.VNPayService;
import com.group4.cursus.service.ZaloPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private InstructorRepository instructorRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private VNPayService vnPayService;

    @Autowired
    private ZaloPayService zaloPayService;

    @PostMapping("/checkout")
    @Transactional
    public ResponseEntity<?> checkout(@RequestParam String paymentMethod, HttpSession session, HttpServletRequest req) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        Long studentId = (Long) session.getAttribute("studentId");
        if (studentId == null) {
            return ResponseEntity.status(401).body("Student not authenticated");
        }

        Student student = studentRepository.findById(Math.toIntExact(studentId))
                .orElseThrow(() -> new RuntimeException("Student not found"));

        List<Cart> cartItems = cartRepository.findByStudent(student);
        if (cartItems.isEmpty()) {
            return ResponseEntity.badRequest().body("Giỏ hàng trống");
        }

        BigDecimal totalPaid = cartItems.stream()
                .map(Cart::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = new Order();
        order.setPaymentMethod(paymentMethod.equalsIgnoreCase("zalopay") ? "ZaloPay" : "VNPay");
        order.setOrderDate(LocalDate.now());
        order.setTotalPaid(totalPaid);
        order.setStudent(student);
        order.setOrderStatus("PENDING");
        orderRepository.save(order);

        String txnRef = String.valueOf(order.getOrderId());

        String returnUrl;
        String paymentUrl;

        try {
            if ("zalopay".equalsIgnoreCase(paymentMethod)) {
                returnUrl = ZaloPayConfig.REDIRECT_URL;
                Map<String, Object> zaloPayResponse = zaloPayService.createPayment("demo_user", totalPaid.longValue(), order.getOrderId());
                if ((int) zaloPayResponse.get("returncode") == 1) {
                    paymentUrl = (String) zaloPayResponse.get("orderurl");
                } else {
                    throw new RuntimeException("Error creating ZaloPay order: " + zaloPayResponse);
                }
            } else {
                returnUrl = VNPayConfig.vnp_OrderReturnUrl;
                paymentUrl = vnPayService.createOrder(totalPaid, "Thanh toán đơn hàng:" + txnRef, txnRef, req, returnUrl);
            }

            PaymentRestDTO paymentResDTO = new PaymentRestDTO();
            paymentResDTO.setStatus("Ok");
            paymentResDTO.setMessage("Successfully");
            paymentResDTO.setURL(paymentUrl);

            return ResponseEntity.status(HttpStatus.OK).body(paymentResDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating order: " + e.getMessage());
        }
    }
}
