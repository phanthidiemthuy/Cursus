package com.group4.cursus.entity;


import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "payout")
@Data
public class Payout {
    @Id
    @Column(name = "payout_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int payoutId;

    @Column(name = "amout", nullable = false)
    private BigDecimal amount;

    @Column(name = "payout_date", nullable = false)
    private LocalDate payoutDate;

    @Column(name = "status", nullable = false)
    private String status;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "instructor_id", nullable = false)
    private Instructor instructor;
}
