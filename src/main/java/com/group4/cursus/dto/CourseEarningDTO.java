package com.group4.cursus.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CourseEarningDTO {
    private Long courseId;
    private String courseTitle;
    private int unitsSold;
    private BigDecimal earnings;
}