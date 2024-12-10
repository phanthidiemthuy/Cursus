package com.group4.cursus.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseDTO {
    private Long courseId;
    private String courseTitle;
    private String description;
    private String requirements;
    private String courseLevel;
    private BigDecimal regularPrice;
    private Long subcategoryId;
    private InstructorDTO instructor;
    private String status;
    private Double averageRating;


}

