package com.group4.cursus.service;

import com.group4.cursus.config.ZaloPayConfig;
import com.group4.cursus.utils.HMACUtil;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ZaloPayServiceTest {

    @InjectMocks
    private ZaloPayService zaloPayService;

    @Mock
    private CloseableHttpClient httpClient;

    @Mock
    private CloseableHttpResponse httpResponse;

    @BeforeEach
    public void setUp() {
        // Không cần thiết lập httpClient bằng ReflectionTestUtils
    }

    @Test
    public void testCreatePayment_Success() throws Exception {
        String appuser = "testuser";
        Long amount = 10000L;
        Long order_id = 123456789L;

        String jsonResponse = "{\"returnmessage\":\"Success\",\"orderurl\":\"http://order.url\",\"returncode\":1,\"zptranstoken\":\"abc123\"}";

        when(httpClient.execute(any(HttpPost.class))).thenReturn(httpResponse);
        when(httpResponse.getEntity().getContent()).thenReturn(new ByteArrayInputStream(jsonResponse.getBytes()));

        Map<String, Object> result = zaloPayService.createPayment(appuser, amount, order_id);

        assertNotNull(result);
        assertEquals("Success", result.get("returnmessage"));
        assertEquals("http://order.url", result.get("orderurl"));
        assertEquals(1, result.get("returncode"));
        assertEquals("abc123", result.get("zptranstoken"));
        assertNotNull(result.get("apptransid"));
    }

    @Test
    public void testGetCurrentTimeString() {
        String format = "yyMMdd";
        String currentTimeString = ReflectionTestUtils.invokeMethod(zaloPayService, "getCurrentTimeString", format);
        assertNotNull(currentTimeString);
        assertEquals(new SimpleDateFormat(format).format(new GregorianCalendar(TimeZone.getTimeZone("GMT+7")).getTime()), currentTimeString);
    }

}
