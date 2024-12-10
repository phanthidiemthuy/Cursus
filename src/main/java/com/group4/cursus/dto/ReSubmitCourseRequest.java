package com.group4.cursus.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReSubmitCourseRequest {
    private String courseTitle;
    private String shortDescription;
    private String description;
    private String requirements;
    private String courseLevel;
    private String thumbnail;
    private BigDecimal regularPrice;
    private Long subCategoryId;
}
