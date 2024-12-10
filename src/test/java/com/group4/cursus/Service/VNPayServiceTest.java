package com.group4.cursus.service;

import com.group4.cursus.config.VNPayConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import jakarta.servlet.http.HttpServletRequest;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class VNPayServiceTest {

    @InjectMocks
    private VNPayService vnpayService;

    @Mock
    private HttpServletRequest request;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateOrder() throws UnsupportedEncodingException {
        BigDecimal total = new BigDecimal("100.00");
        String orderInfor = "Test order";
        String txnRef = "123456";
        String returnUrl = "http://localhost/return";
        when(VNPayConfig.getIpAddress(request)).thenReturn("127.0.0.1");

        String expectedUrlPart = URLEncoder.encode("vnp_OrderInfo", StandardCharsets.US_ASCII.toString()) + "=" + URLEncoder.encode("Test order", StandardCharsets.US_ASCII.toString());

        String orderUrl = vnpayService.createOrder(total, orderInfor, txnRef, request, returnUrl);

        assertTrue(orderUrl.contains(expectedUrlPart));
        assertTrue(orderUrl.contains("vnp_SecureHash="));
    }

    @Test
    public void testGenerateQRCode() throws Exception {
        String text = "http://localhost/test";
        byte[] qrCode = vnpayService.generateQRCode(text);

        assertNotNull(qrCode);
        assertTrue(qrCode.length > 0);
    }

//    @Test
//    public void testValidateReturn() {
//        Map<String, String> fields = new HashMap<>();
//        fields.put("vnp_Amount", "1000000");
//        fields.put("vnp_Command", "pay");
//        fields.put("vnp_TmnCode", "testcode");
//
//        String vnp_SecureHash = "expectedhash";
//        when(VNPayConfig.hashAllFields(fields)).thenReturn("expectedhash");
//
//        boolean isValid = vnpayService.validateReturn(fields, vnp_SecureHash);
//
//        assertTrue(isValid);
//    }
}
