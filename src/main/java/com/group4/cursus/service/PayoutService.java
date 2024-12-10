package com.group4.cursus.service;


import com.group4.cursus.entity.Instructor;
import com.group4.cursus.entity.Payout;
import com.group4.cursus.repository.InstructorRepository;
import com.group4.cursus.repository.PayoutRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class PayoutService {

    @Autowired
    private PayoutRepository payoutRepository;

    @Autowired
    private InstructorRepository instructorRepository;

    /**
     * Request a payout for the given instructor and amount.
     *
     * @param instructorId The ID of the instructor requesting the payout.
     * @param amount The amount of the payout.
     * @return The created Payout object.
     * @throws Exception If the instructor is not found or the balance is insufficient.
     */
    public Payout requestPayout(Long instructorId, BigDecimal amount) throws Exception {
        Instructor instructor = instructorRepository.findById(Math.toIntExact(instructorId))
                .orElseThrow(() -> new Exception("Instructor not found"));

        if (instructor.getSalary().compareTo(amount) < 0) {
            throw new Exception("Insufficient balance");
        }

        Payout payout = new Payout();
        payout.setAmount(amount);
        payout.setPayoutDate(LocalDate.now());
        payout.setStatus("PENDING");
        payout.setInstructor(instructor);

        instructor.setSalary(instructor.getSalary().subtract(amount));
        instructorRepository.save(instructor);

        return payoutRepository.save(payout);
    }

    public List<Payout> getAllPayoutRequests() {
        return payoutRepository.findAllByStatus("PENDING");
    }

    /**
     * Approve a payout request by its ID.
     *
     * @param payoutId The ID of the payout to approve.
     * @return The updated Payout object.
     * @throws Exception If the payout is not found.
     */
    public Payout approvePayout(Long payoutId) throws Exception {
        Payout payout = payoutRepository.findById(payoutId)
                .orElseThrow(() -> new Exception("Payout not found"));

        payout.setStatus("APPROVED");
        payout.setPayoutDate(LocalDate.now());
        return payoutRepository.save(payout);
    }


}

