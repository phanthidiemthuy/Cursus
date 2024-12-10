package com.group4.cursus.dto;

import lombok.Data;

@Data
public class ReviewDTO {
    private Long reviewId;
    private int rating;
    private String contents;
    private Long courseId;
    private Long studentId;
    private String studentName;
}
