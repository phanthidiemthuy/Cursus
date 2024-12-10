package com.group4.cursus.controller;

import com.group4.cursus.config.ZaloPayConfig;
import com.group4.cursus.dto.MyCustomResponse;
import com.group4.cursus.entity.*;
import com.group4.cursus.repository.*;
import com.group4.cursus.utils.HMACUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("order/payment_return")
public class RedirectPaymentController {

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

    @GetMapping("/zalopay")
    @Transactional
    public ResponseEntity<MyCustomResponse> zaloPayReturn(HttpServletRequest req) {
        Map<String, String> fields = new HashMap<>();
        for (Enumeration<String> params = req.getParameterNames(); params.hasMoreElements();) {
            String fieldName = params.nextElement();
            String fieldValue = req.getParameter(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                fields.put(fieldName, fieldValue);
            }
        }

        String appid = req.getParameter("appid");
        String apptransid = req.getParameter("apptransid");
        String pmcid = req.getParameter("pmcid");
        String bankcode = req.getParameter("bankcode");
        String amount = req.getParameter("amount");
        String discountamount = req.getParameter("discountamount");
        String status = req.getParameter("status");
        String checksum = req.getParameter("checksum");

        // Assuming apptransid format is yyMMdd_orderId
        String[] parts = apptransid.split("_");
        if (parts.length != 2) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MyCustomResponse("Invalid apptransid format", false));
        }

        Long orderId;
        try {
            orderId = Long.valueOf(parts[1]);
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MyCustomResponse("Invalid order ID in apptransid", false));
        }

        // 1. Lấy thông tin đơn hàng từ database
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MyCustomResponse("Không tìm thấy đơn hàng", false));
        }

        // 2. Kiểm tra trạng thái thanh toán
        String data = appid + "|" + apptransid + "|" + pmcid + "|" + bankcode + "|" + amount + "|" + discountamount + "|" + status;
        String calculatedChecksum = HMACUtil.HMacHexStringEncode(HMACUtil.HMACSHA256, ZaloPayConfig.KEY2, data);

        if (calculatedChecksum.equals(checksum)) {
            if ("1".equals(status)) {
                // Thanh toán thành công
                order.setOrderStatus("COMPLETED");
                updateOrderAndEnrollments(order);
                orderRepository.save(order);
                return ResponseEntity.ok(new MyCustomResponse("Thanh toán thành công!", true));
            } else {
                // Thanh toán thất bại
                order.setOrderStatus("FAILED");
                orderRepository.save(order);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MyCustomResponse("Thanh toán thất bại!", false));
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MyCustomResponse("Checksum không hợp lệ", false));
        }
    }

    @GetMapping("/vnpay")
    @Transactional
    public ResponseEntity<MyCustomResponse> vnpayReturn(HttpServletRequest req) {
        Map<String, String> fields = new HashMap<>();
        for (Enumeration<String> params = req.getParameterNames(); params.hasMoreElements();) {
            String fieldName = params.nextElement();
            String fieldValue = req.getParameter(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                fields.put(fieldName, fieldValue);
            }
        }
        String vnp_SecureHash = req.getParameter("vnp_SecureHash");

        String vnp_TxnRef = req.getParameter("vnp_TxnRef");
        String responseCode = req.getParameter("vnp_ResponseCode");

        Long orderId = Long.valueOf(vnp_TxnRef);
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MyCustomResponse("Không tìm thấy đơn hàng", false));
        }

        if ("00".equals(responseCode)) {
            order.setOrderStatus("COMPLETED");
            updateOrderAndEnrollments(order);
            orderRepository.save(order);
            return ResponseEntity.ok(new MyCustomResponse("Thanh toán thành công!", true));
        } else {
            order.setOrderStatus("FAILED");
            orderRepository.save(order);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MyCustomResponse("Thanh toán thất bại!", false));
        }
    }

    private void updateOrderAndEnrollments(Order order) {
        List<Cart> cartItems = cartRepository.findByStudent(order.getStudent());
        List<OrderItem> orderItems = new ArrayList<>();
        List<Enrollment> enrollments = new ArrayList<>();

        for (Cart cart : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setAddingDate(cart.getAddingDate());
            orderItem.setUnitPrice(cart.getPrice());
            orderItem.setOrder(order);
            orderItem.setCourse(cart.getCourse());
            orderItems.add(orderItem);

            Enrollment enrollment = new Enrollment();
            enrollment.setEnrollmentDate(LocalDate.now());
            enrollment.setProgress(0);
            enrollment.setStudent(order.getStudent());
            enrollment.setCourse(cart.getCourse());
            enrollments.add(enrollment);

            Instructor instructor = cart.getCourse().getInstructor();
            BigDecimal instructorShare = cart.getPrice().multiply(new BigDecimal("0.70"));
            instructor.setSalary(instructor.getSalary().add(instructorShare));
            instructorRepository.save(instructor);
        }

        orderItemRepository.saveAll(orderItems);
        enrollmentRepository.saveAll(enrollments);
        cartRepository.deleteAll(cartItems);
    }
}

