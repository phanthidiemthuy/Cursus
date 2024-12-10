package com.group4.cursus.service;

import com.group4.cursus.entity.Instructor;
import com.group4.cursus.entity.Payout;
import com.group4.cursus.repository.InstructorRepository;
import com.group4.cursus.repository.PayoutRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PayoutServiceTest {

    @Mock
    private PayoutRepository payoutRepository;

    @Mock
    private InstructorRepository instructorRepository;

    @InjectMocks
    private PayoutService payoutService;

    private Instructor instructor;

    @BeforeEach
    public void setUp() {
        instructor = new Instructor();
        instructor.setUserId(1);
        instructor.setSalary(new BigDecimal("1000.00"));
    }

    @Test
    public void testRequestPayout_Success() throws Exception {
        BigDecimal amount = new BigDecimal("100.00");

        when(instructorRepository.findById(anyInt())).thenReturn(Optional.of(instructor));
        when(payoutRepository.save(any(Payout.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Payout payout = payoutService.requestPayout((long) instructor.getUserId(), amount);

        assertNotNull(payout);
        assertEquals("PENDING", payout.getStatus());
        assertEquals(amount, payout.getAmount());
        assertEquals(instructor, payout.getInstructor());

        verify(instructorRepository, times(1)).save(instructor);
        verify(payoutRepository, times(1)).save(payout);
        assertEquals(new BigDecimal("900.00"), instructor.getSalary());
    }

    @Test
    public void testRequestPayout_InstructorNotFound() {
        when(instructorRepository.findById(anyInt())).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () -> {
            payoutService.requestPayout(1L, new BigDecimal("100.00"));
        });

        assertEquals("Instructor not found", exception.getMessage());
    }

    @Test
    public void testRequestPayout_InsufficientBalance() {
        BigDecimal amount = new BigDecimal("1100.00");

        when(instructorRepository.findById(anyInt())).thenReturn(Optional.of(instructor));

        Exception exception = assertThrows(Exception.class, () -> {
            payoutService.requestPayout((long) instructor.getUserId(), amount);
        });

        assertEquals("Insufficient balance", exception.getMessage());
    }

    @Test
    public void testApprovePayout_Success() throws Exception {
        Payout payout = new Payout();
        payout.setPayoutId(1);
        payout.setStatus("PENDING");
        payout.setAmount(new BigDecimal("100.00"));
        payout.setInstructor(instructor);

        when(payoutRepository.findById(1L)).thenReturn(Optional.of(payout));
        when(payoutRepository.save(any(Payout.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Payout approvedPayout = payoutService.approvePayout(1L);

        assertNotNull(approvedPayout);
        assertEquals("APPROVED", approvedPayout.getStatus());
        assertEquals(LocalDate.now(), approvedPayout.getPayoutDate());

        verify(payoutRepository, times(1)).save(approvedPayout);
    }

    @Test
    public void testApprovePayout_NotFound() {
        when(payoutRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () -> {
            payoutService.approvePayout(1L);
        });

        assertEquals("Payout not found", exception.getMessage());
    }

    @Test
    public void testGetAllPayoutRequests() {
        when(payoutRepository.findAllByStatus("PENDING")).thenReturn(List.of(new Payout(), new Payout()));

        List<Payout> payouts = payoutService.getAllPayoutRequests();

        assertNotNull(payouts);
        assertEquals(2, payouts.size());
        verify(payoutRepository, times(1)).findAllByStatus("PENDING");
    }
}
