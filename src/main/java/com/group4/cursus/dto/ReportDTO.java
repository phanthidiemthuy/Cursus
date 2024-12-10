package com.group4.cursus.dto;

import lombok.Data;

@Data
public class ReportDTO {
    private Long reportId;
    private String contents;
    private String images;
    private Long courseId;
    private Long studentId;
    private String studentName;
}
