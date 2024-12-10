package com.group4.cursus.repository;


import com.group4.cursus.entity.Payout;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PayoutRepository extends JpaRepository<Payout, Long>{
    List<Payout> findAllByStatus(String status);
    List<Payout> findAllByInstructor_UserId(Long instructorId);
}
