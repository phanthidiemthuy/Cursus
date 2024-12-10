package com.group4.cursus.controller;

import com.group4.cursus.dto.*;
import com.group4.cursus.entity.Course;
import com.group4.cursus.entity.Payout;
import com.group4.cursus.entity.User;
import com.group4.cursus.repository.PayoutRepository;
import com.group4.cursus.service.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import java.util.Base64;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

    @Autowired
    private InstructorService instructorService;

    @Autowired
    private VNPayService vnPayService;

    @Autowired
    private PayoutRepository payoutRepository;

    @Autowired
    private ReportService reportService;

    @GetMapping("/report/{courseId}")
    public ResponseEntity<?> getReportByCourse(@PathVariable Long courseId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //String instructorEmail = authentication.getName();
        try {
            List<ReportDTO> report = reportService.getReport(courseId);
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/students")
    public ResponseEntity<Page<User>> getAllApprovedStudents(
            @RequestParam(required = false,defaultValue = "0") Integer page,
            @RequestParam(required = false,defaultValue = "5") Integer size) {
        if (page == null || size == null) {
            return ResponseEntity.badRequest().build();
        }
        Page<User> students = userService.getAllApprovedStudents(page, size);
        return ResponseEntity.ok(students);
    }

    @GetMapping("/instructors")
    public ResponseEntity<Page<InstructorDTO>> getAllApprovedInstructors(
            @RequestParam(required = false,defaultValue = "0") Integer page,
            @RequestParam(required = false,defaultValue = "5") Integer size) {
        if (page == null || size == null) {
            return ResponseEntity.badRequest().build();
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<User> instructors = userService.getAllApprovedInstructor(page, size);
        List<InstructorDTO> instructorDTOs = instructors.getContent().stream().map(instructor -> {
            InstructorDTO dto = new InstructorDTO();
            dto.setUserId((long) instructor.getUserId());
            dto.setFullName(instructor.getFullName());
            dto.setEmail(instructor.getEmail());
            dto.setAddress(instructor.getAddress());
            return dto;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(new PageImpl<>(instructorDTOs, pageable, instructors.getTotalElements()));
    }

    @GetMapping("/instructors/pending")
    public ResponseEntity<Page<User>> getAllInstructorsIsPending(
            @RequestParam(required = false,defaultValue = "0") Integer page,
            @RequestParam(required = false,defaultValue = "5") Integer size) {
        if (page == null || size == null) {
            return ResponseEntity.badRequest().build();
        }
        Page<User> students = userService.getAllApprovedInstructorPending(page, size);
        return ResponseEntity.ok(students);
    }


    @PostMapping("/instructors/{instructorId}/approve")
    public ResponseEntity<?> approveInstructor(@PathVariable Integer instructorId) {
        try {
            authService.approveInstructor(instructorId);
            return ResponseEntity.ok(new MyCustomResponse("Instructor approved successfully.",true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error approving instructor: " + e.getMessage());
        }
    }

    @PostMapping("/instructors/{instructorId}/reject")
    public ResponseEntity<?> rejectInstructor(@PathVariable Integer instructorId) {
        try {
            authService.rejectInstructor(instructorId);
            return ResponseEntity.ok(new MyCustomResponse("Instructor rejected successfully.",true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error rejecting instructor: " + e.getMessage());
        }
    }
    // Block a user
    @PostMapping("/{id}/block")
    public ResponseEntity<String> blockUser(@PathVariable Long id) {
        userService.blockUser(id);
        return ResponseEntity.ok("User blocked successfully");
    }

    // Unblock a user
    @PostMapping("/{id}/unblock")
    public ResponseEntity<String> unblockUser(@PathVariable Long id) {
        userService.unblockUser(id);
        return ResponseEntity.ok("User unblocked successfully");
    }


    // New endpoint to get all pending courses
    @GetMapping("/courses/pending")
    public ResponseEntity<List<Course>> getAllPendingCourses() {
        List<Course> courses = instructorService.findAllPendingCourses();
        return ResponseEntity.ok(courses);
    }

    // Endpoint to approve a course by ID
    @PostMapping("/courses/{courseId}/approve")
    public ResponseEntity<?> approveCourse(@PathVariable Long courseId) {
        try {
            instructorService.approveCourse(courseId);
            return ResponseEntity.ok(new MyCustomResponse("Course approved successfully.",true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error approving course: " + e.getMessage());
        }
    }

    // Endpoint to reject a course by ID
    @PostMapping("/courses/{courseId}/reject")
    public ResponseEntity<?> rejectCourse(@PathVariable Long courseId) {
        try {
            instructorService.rejectCourse(courseId);
            return ResponseEntity.ok("Course rejected successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error rejecting course: " + e.getMessage());
        }
    }

    // Endpoint to block a course by ID
    @PostMapping("/courses/{courseId}/block")
    public ResponseEntity<?> blockCourse(@PathVariable Long courseId) {
        try {
            instructorService.blockCourse(courseId);
            return ResponseEntity.ok(new MyCustomResponse("Course blocked successfully.",true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error blocking course: " + e.getMessage());
        }
    }

    // Endpoint to unblock a course by ID
    @PostMapping("/courses/{courseId}/unblock")
    public ResponseEntity<?> unblockCourse(@PathVariable Long courseId) {
        try {
            instructorService.unblockCourse(courseId);
            return ResponseEntity.ok(new MyCustomResponse("Course unblocked successfully.",true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error unblocking course: " + e.getMessage());
        }
    }

    //payout

    // Endpoint để xem danh sách payout
    @GetMapping("/payouts")
    public ResponseEntity<Page<PayoutDTO>> getAllPayoutRequests(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "5") Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Payout> payouts = payoutRepository.findAll(pageable);
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
        return ResponseEntity.ok(new PageImpl<>(payoutDTOs, pageable, payouts.getTotalElements()));
    }



    // Endpoint để phê duyệt payout và tạo mã QR
    @PostMapping("/payouts/{payoutId}/approve")
    public ResponseEntity<MyCustomResponse> approvePayout(@PathVariable long payoutId, HttpServletRequest request) {
        try {
            Payout payout = payoutRepository.findById(payoutId).orElseThrow(() -> new RuntimeException("Payout not found"));
            String txnRef = String.valueOf(payoutId);
            String returnUrl = "http://localhost:8080/api/admin/vnpay_return";
            String paymentUrl = vnPayService.createOrder(payout.getAmount(), "Payout to instructor", txnRef, request, returnUrl);
            byte[] qrCode = vnPayService.generateQRCode(paymentUrl);

            String qrCodeBase64 = Base64.getEncoder().encodeToString(qrCode);
            payout.setStatus("APPROVED");
            payoutRepository.save(payout);

            Map<String, String> responseData = new HashMap<>();
            responseData.put("paymentUrl", paymentUrl);
            responseData.put("qrCode", qrCodeBase64);

            return ResponseEntity.ok(new MyCustomResponse("QR Code và Link thanh toán được tạo thành công", true, responseData));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new MyCustomResponse("Không thể tạo mã QR và link thanh toán", false));
        }
    }


    @GetMapping("/vnpay_return")
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

        // Tìm kiếm payout bằng payoutId (txnRef)
        Long payoutId = Long.valueOf(vnp_TxnRef);
        Payout payout = payoutRepository.findById(payoutId).orElse(null);
        if (payout == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MyCustomResponse("Không tìm thấy đơn hàng", false));
        }

        if ("00".equals(responseCode)) {
            payout.setStatus("COMPLETED");
            payoutRepository.save(payout);
            return ResponseEntity.ok(new MyCustomResponse("Thanh toán thành công!", true));
        } else {
            payout.setStatus("FAILED");
            payoutRepository.save(payout);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MyCustomResponse("Thanh toán thất bại!", false));
        }
    }




}
