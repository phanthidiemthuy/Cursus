package com.group4.cursus.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class EarningsDTO {
    private String month;
    private BigDecimal totalEarnings;
    private List<CourseEarningDTO> courseEarnings;
}

