package com.group4.cursus.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CourseAnalyticsDTO {
    private Long courseId;
    private String courseTitle;
    private String shortDescription;
    private String description;
    private String requirements;
    private String courseLevel;
    private BigDecimal regularPrice;
    private Long subcategoryId;
    private InstructorDTO instructor;
    private String status;
    private Double averageRating;
    private Long totalStudentsEnrolled;
    private Double averageProgress;
}
