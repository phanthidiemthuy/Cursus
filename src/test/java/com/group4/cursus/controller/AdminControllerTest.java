package com.group4.cursus.controller;

import com.google.zxing.WriterException;
import com.group4.cursus.dto.InstructorDTO;
import com.group4.cursus.dto.MyCustomResponse;
import com.group4.cursus.dto.PayoutDTO;
import com.group4.cursus.entity.Course;
import com.group4.cursus.entity.Payout;
import com.group4.cursus.entity.User;
import com.group4.cursus.repository.PayoutRepository;
import com.group4.cursus.service.AuthService;
import com.group4.cursus.service.InstructorService;
import com.group4.cursus.service.UserService;
import com.group4.cursus.service.VNPayService;
import jakarta.servlet.http.HttpServletRequest;
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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class AdminControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private AuthService authService;

    @Mock
    private InstructorService instructorService;

    @Mock
    private VNPayService vnPayService;

    @Mock
    private PayoutRepository payoutRepository;

    @InjectMocks
    private AdminController adminController;

    @Mock
    private HttpServletRequest request;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllApprovedStudents() {
        int page = 0;
        int size = 5;
        Page<User> students = new PageImpl<>(Collections.singletonList(new User()));

        when(userService.getAllApprovedStudents(page, size)).thenReturn(students);

        ResponseEntity<Page<User>> response = adminController.getAllApprovedStudents(page, size);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(students, response.getBody());
    }

    @Test
    public void testGetAllApprovedInstructors() {
        int page = 0;
        int size = 5;
        Page<User> instructors = new PageImpl<>(Collections.singletonList(new User()));
        List<InstructorDTO> instructorDTOs = instructors.getContent().stream().map(instructor -> {
            InstructorDTO dto = new InstructorDTO();
            dto.setUserId((long) instructor.getUserId());
            dto.setFullName(instructor.getFullName());
            dto.setEmail(instructor.getEmail());
            dto.setAddress(instructor.getAddress());
            return dto;
        }).collect(Collectors.toList());

        when(userService.getAllApprovedInstructor(page, size)).thenReturn(instructors);

        ResponseEntity<Page<InstructorDTO>> response = adminController.getAllApprovedInstructors(page, size);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(new PageImpl<>(instructorDTOs, PageRequest.of(page, size), instructors.getTotalElements()), response.getBody());
    }

    @Test
    public void testGetAllInstructorsIsPending() {
        int page = 0;
        int size = 5;
        Page<User> pendingInstructors = new PageImpl<>(Collections.singletonList(new User()));

        when(userService.getAllApprovedInstructorPending(page, size)).thenReturn(pendingInstructors);

        ResponseEntity<Page<User>> response = adminController.getAllInstructorsIsPending(page, size);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(pendingInstructors, response.getBody());
    }

    @Test
    public void testApproveInstructor() {
        int instructorId = 1;

        ResponseEntity<?> response = adminController.approveInstructor(instructorId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Instructor approved successfully.", ((MyCustomResponse) response.getBody()).getMessage());
    }

    @Test
    public void testApproveInstructor_Failure() {
        int instructorId = 1;

        doThrow(new RuntimeException("Error")).when(authService).approveInstructor(instructorId);

        ResponseEntity<?> response = adminController.approveInstructor(instructorId);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Error approving instructor: Error", response.getBody());
    }

    @Test
    public void testRejectInstructor() {
        int instructorId = 1;

        ResponseEntity<?> response = adminController.rejectInstructor(instructorId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Instructor rejected successfully.", ((MyCustomResponse) response.getBody()).getMessage());
    }

    @Test
    public void testRejectInstructor_Failure() {
        int instructorId = 1;

        doThrow(new RuntimeException("Error")).when(authService).rejectInstructor(instructorId);

        ResponseEntity<?> response = adminController.rejectInstructor(instructorId);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Error rejecting instructor: Error", response.getBody());
    }

    @Test
    public void testBlockUser() {
        Long userId = 1L;

        ResponseEntity<String> response = adminController.blockUser(userId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User blocked successfully", response.getBody());
    }

    @Test
    public void testUnblockUser() {
        Long userId = 1L;

        ResponseEntity<String> response = adminController.unblockUser(userId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User unblocked successfully", response.getBody());
    }

    @Test
    public void testGetAllPendingCourses() {
        List<Course> courses = Collections.singletonList(new Course());

        when(instructorService.findAllPendingCourses()).thenReturn(courses);

        ResponseEntity<List<Course>> response = adminController.getAllPendingCourses();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(courses, response.getBody());
    }

    @Test
    public void testApproveCourse() {
        Long courseId = 1L;

        ResponseEntity<?> response = adminController.approveCourse(courseId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Course approved successfully.", ((MyCustomResponse) response.getBody()).getMessage());
    }

    @Test
    public void testApproveCourse_Failure() {
        Long courseId = 1L;

        doThrow(new RuntimeException("Error")).when(instructorService).approveCourse(courseId);

        ResponseEntity<?> response = adminController.approveCourse(courseId);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Error approving course: Error", response.getBody());
    }

    @Test
    public void testRejectCourse() {
        Long courseId = 1L;

        ResponseEntity<?> response = adminController.rejectCourse(courseId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Course rejected successfully.", response.getBody());
    }

    @Test
    public void testRejectCourse_Failure() {
        Long courseId = 1L;

        doThrow(new RuntimeException("Error")).when(instructorService).rejectCourse(courseId);

        ResponseEntity<?> response = adminController.rejectCourse(courseId);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Error rejecting course: Error", response.getBody());
    }

    @Test
    public void testBlockCourse() {
        Long courseId = 1L;

        ResponseEntity<?> response = adminController.blockCourse(courseId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Course blocked successfully.", ((MyCustomResponse) response.getBody()).getMessage());
    }

    @Test
    public void testBlockCourse_Failure() {
        Long courseId = 1L;

        doThrow(new RuntimeException("Error")).when(instructorService).blockCourse(courseId);

        ResponseEntity<?> response = adminController.blockCourse(courseId);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Error blocking course: Error", response.getBody());
    }

    @Test
    public void testUnblockCourse() {
        Long courseId = 1L;

        ResponseEntity<?> response = adminController.unblockCourse(courseId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Course unblocked successfully.", ((MyCustomResponse) response.getBody()).getMessage());
    }

    @Test
    public void testUnblockCourse_Failure() {
        Long courseId = 1L;

        doThrow(new RuntimeException("Error")).when(instructorService).unblockCourse(courseId);

        ResponseEntity<?> response = adminController.unblockCourse(courseId);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Error unblocking course: Error", response.getBody());
    }

    @Test
    public void testGetAllPayoutRequests() {
        int page = 0;
        int size = 5;
        Page<Payout> payouts = new PageImpl<>(Collections.singletonList(new Payout()));
        List<PayoutDTO> payoutDTOs = payouts.getContent().stream().map(payout -> {
            PayoutDTO dto = new PayoutDTO();
            dto.setPayoutId(payout.getPayoutId());
            dto.setAmount(payout.getAmount());
            dto.setPayoutDate(payout.getPayoutDate());
            dto.setStatus(payout.getStatus());
            dto.setInstructorId((long) payout.getInstructor().getUserId());
            dto.setInstructorName(payout.getInstructor().getFullName());
            return dto;
        }).collect(Collectors.toList());

        when(payoutRepository.findAll(any(Pageable.class))).thenReturn(payouts);

        ResponseEntity<Page<PayoutDTO>> response = adminController.getAllPayoutRequests(page, size);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(new PageImpl<>(payoutDTOs, PageRequest.of(page, size), payouts.getTotalElements()), response.getBody());
    }

    @Test
    public void testApprovePayout() throws IOException, WriterException {
        long payoutId = 1L;
        Payout payout = new Payout();
        payout.setAmount(BigDecimal.TEN);
        payout.setPayoutId((int) payoutId);

        when(payoutRepository.findById(payoutId)).thenReturn(Optional.of(payout));
        when(vnPayService.createOrder(any(BigDecimal.class), anyString(), anyString(), any(HttpServletRequest.class), anyString())).thenReturn("http://payment.url");
        when(vnPayService.generateQRCode(anyString())).thenReturn(new byte[0]);

        ResponseEntity<MyCustomResponse> response = adminController.approvePayout(payoutId, request);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("QR Code và Link thanh toán được tạo thành công", response.getBody().getMessage());
    }

    @Test
    public void testApprovePayout_Failure() {
        long payoutId = 1L;

        when(payoutRepository.findById(payoutId)).thenReturn(Optional.empty());

        ResponseEntity<MyCustomResponse> response = adminController.approvePayout(payoutId, request);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Không thể tạo mã QR và link thanh toán", response.getBody().getMessage());
    }

    @Test
    public void testVnpayReturn_Success() {
        Map<String, String> params = new HashMap<>();
        params.put("vnp_SecureHash", "secureHash");
        params.put("vnp_TxnRef", "1");
        params.put("vnp_ResponseCode", "00");
        Payout payout = new Payout();
        payout.setPayoutId(1);

        when(request.getParameterNames()).thenReturn(Collections.enumeration(params.keySet()));
        when(request.getParameter(anyString())).thenAnswer(invocation -> params.get(invocation.getArgument(0)));
        when(payoutRepository.findById(anyLong())).thenReturn(Optional.of(payout));

        ResponseEntity<MyCustomResponse> response = adminController.vnpayReturn(request);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Thanh toán thành công!", response.getBody().getMessage());
    }

    @Test
    public void testVnpayReturn_Failure() {
        Map<String, String> params = new HashMap<>();
        params.put("vnp_SecureHash", "secureHash");
        params.put("vnp_TxnRef", "1");
        params.put("vnp_ResponseCode", "01");
        Payout payout = new Payout();
        payout.setPayoutId(1);

        when(request.getParameterNames()).thenReturn(Collections.enumeration(params.keySet()));
        when(request.getParameter(anyString())).thenAnswer(invocation -> params.get(invocation.getArgument(0)));
        when(payoutRepository.findById(anyLong())).thenReturn(Optional.of(payout));

        ResponseEntity<MyCustomResponse> response = adminController.vnpayReturn(request);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Thanh toán thất bại!", response.getBody().getMessage());
    }

    @Test
    public void testVnpayReturn_PayoutNotFound() {
        Map<String, String> params = new HashMap<>();
        params.put("vnp_SecureHash", "secureHash");
        params.put("vnp_TxnRef", "1");
        params.put("vnp_ResponseCode", "00");

        when(request.getParameterNames()).thenReturn(Collections.enumeration(params.keySet()));
        when(request.getParameter(anyString())).thenAnswer(invocation -> params.get(invocation.getArgument(0)));
        when(payoutRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResponseEntity<MyCustomResponse> response = adminController.vnpayReturn(request);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Không tìm thấy đơn hàng", response.getBody().getMessage());
    }
}
