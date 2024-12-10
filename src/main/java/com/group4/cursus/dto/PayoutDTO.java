package com.group4.cursus.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class PayoutDTO {
    private int payoutId;
    private BigDecimal amount;
    private LocalDate payoutDate;
    private String status;
    private Long instructorId;
    private String instructorName;
}
