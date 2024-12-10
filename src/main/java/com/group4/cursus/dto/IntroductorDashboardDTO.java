package com.group4.cursus.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class IntroductorDashboardDTO {
    private BigDecimal totalSales;
    private int totalEnrollments;
    private int totalCourses;
    private int totalStudents;
    private int totalReviews;
    private double averageRating;
    private int totalCompletedCourses; // Number of courses marked as completed
    private BigDecimal averageCoursePrice; // Average price of the courses
    private int newStudentsThisMonth; // Number of new students enrolled this month
    // Add other fields as necessary
}
