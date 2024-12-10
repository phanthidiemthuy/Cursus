package com.group4.cursus.controller;

import com.group4.cursus.config.ZaloPayConfig;
import com.group4.cursus.dto.MyCustomResponse;
import com.group4.cursus.entity.Order;
import com.group4.cursus.repository.*;
import com.group4.cursus.utils.HMACUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import jakarta.servlet.http.HttpServletRequest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class RedirectPaymentControllerTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private InstructorRepository instructorRepository;

    @InjectMocks
    private RedirectPaymentController redirectPaymentController;

    @Mock
    private HttpServletRequest request;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testZaloPayReturn_Success() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("appid", "app123");
        params.put("apptransid", "210101_12345");
        params.put("pmcid", "pmc123");
        params.put("bankcode", "bank123");
        params.put("amount", "1000");
        params.put("discountamount", "0");
        params.put("status", "1");
        params.put("checksum", "validChecksum");

        Order order = new Order();
        order.setOrderStatus("PENDING");

        when(request.getParameterNames()).thenReturn(Collections.enumeration(params.keySet()));
        when(request.getParameter(anyString())).thenAnswer(invocation -> params.get(invocation.getArgument(0)));
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));
        String data = "app123|210101_12345|pmc123|bank123|1000|0|1";
        when(HMACUtil.HMacHexStringEncode(eq(HMACUtil.HMACSHA256), eq(ZaloPayConfig.KEY2), eq(data)))
                .thenReturn("validChecksum");

        ResponseEntity<MyCustomResponse> response = redirectPaymentController.zaloPayReturn(request);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Thanh toán thành công!", response.getBody().getMessage());
    }

    @Test
    public void testZaloPayReturn_Failure_InvalidChecksum() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("appid", "app123");
        params.put("apptransid", "210101_12345");
        params.put("pmcid", "pmc123");
        params.put("bankcode", "bank123");
        params.put("amount", "1000");
        params.put("discountamount", "0");
        params.put("status", "1");
        params.put("checksum", "invalidChecksum");

        when(request.getParameterNames()).thenReturn(Collections.enumeration(params.keySet()));
        when(request.getParameter(anyString())).thenAnswer(invocation -> params.get(invocation.getArgument(0)));
        String data = "app123|210101_12345|pmc123|bank123|1000|0|1";
        when(HMACUtil.HMacHexStringEncode(eq(HMACUtil.HMACSHA256), eq(ZaloPayConfig.KEY2), eq(data)))
                .thenReturn("validChecksum");

        ResponseEntity<MyCustomResponse> response = redirectPaymentController.zaloPayReturn(request);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Checksum không hợp lệ", response.getBody().getMessage());
    }

    @Test
    public void testZaloPayReturn_Failure_InvalidOrderIdFormat() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("appid", "app123");
        params.put("apptransid", "210101_invalid");
        params.put("pmcid", "pmc123");
        params.put("bankcode", "bank123");
        params.put("amount", "1000");
        params.put("discountamount", "0");
        params.put("status", "1");
        params.put("checksum", "validChecksum");

        when(request.getParameterNames()).thenReturn(Collections.enumeration(params.keySet()));
        when(request.getParameter(anyString())).thenAnswer(invocation -> params.get(invocation.getArgument(0)));

        ResponseEntity<MyCustomResponse> response = redirectPaymentController.zaloPayReturn(request);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid order ID in apptransid", response.getBody().getMessage());
    }

    @Test
    public void testZaloPayReturn_Failure_OrderNotFound() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("appid", "app123");
        params.put("apptransid", "210101_12345");
        params.put("pmcid", "pmc123");
        params.put("bankcode", "bank123");
        params.put("amount", "1000");
        params.put("discountamount", "0");
        params.put("status", "1");
        params.put("checksum", "validChecksum");

        when(request.getParameterNames()).thenReturn(Collections.enumeration(params.keySet()));
        when(request.getParameter(anyString())).thenAnswer(invocation -> params.get(invocation.getArgument(0)));
        when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResponseEntity<MyCustomResponse> response = redirectPaymentController.zaloPayReturn(request);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Không tìm thấy đơn hàng", response.getBody().getMessage());
    }

    @Test
    public void testVNPayReturn_Success() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("vnp_SecureHash", "secureHash");
        params.put("vnp_TxnRef", "12345");
        params.put("vnp_ResponseCode", "00");

        Order order = new Order();
        order.setOrderStatus("PENDING");

        when(request.getParameterNames()).thenReturn(Collections.enumeration(params.keySet()));
        when(request.getParameter(anyString())).thenAnswer(invocation -> params.get(invocation.getArgument(0)));
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));

        ResponseEntity<MyCustomResponse> response = redirectPaymentController.vnpayReturn(request);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Thanh toán thành công!", response.getBody().getMessage());
    }

    @Test
    public void testVNPayReturn_Failure() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("vnp_SecureHash", "secureHash");
        params.put("vnp_TxnRef", "12345");
        params.put("vnp_ResponseCode", "01");

        Order order = new Order();
        order.setOrderStatus("PENDING");

        when(request.getParameterNames()).thenReturn(Collections.enumeration(params.keySet()));
        when(request.getParameter(anyString())).thenAnswer(invocation -> params.get(invocation.getArgument(0)));
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));

        ResponseEntity<MyCustomResponse> response = redirectPaymentController.vnpayReturn(request);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Thanh toán thất bại!", response.getBody().getMessage());
    }

    @Test
    public void testVNPayReturn_Failure_OrderNotFound() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("vnp_SecureHash", "secureHash");
        params.put("vnp_TxnRef", "12345");
        params.put("vnp_ResponseCode", "00");

        when(request.getParameterNames()).thenReturn(Collections.enumeration(params.keySet()));
        when(request.getParameter(anyString())).thenAnswer(invocation -> params.get(invocation.getArgument(0)));
        when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResponseEntity<MyCustomResponse> response = redirectPaymentController.vnpayReturn(request);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Không tìm thấy đơn hàng", response.getBody().getMessage());
    }


}
