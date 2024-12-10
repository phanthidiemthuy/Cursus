package com.group4.cursus.dto;


import com.group4.cursus.entity.Category;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SubCategoryDTO {

    @NotNull(message = "SubCategory name cannot be null")
    @Size(min = 5, max = 100, message = "SubCategory name must be between 5 and 100 characters")
    @Pattern(regexp = "^[a-zA-Z ]*$", message = "SubCategory name must contain only letters, numbers and spaces")
    private String subcategoryName;

    @NotNull(message = "SubCategory description cannot be null")
    @Size(min = 10, max = 200, message = "SubCategory description must be between 10 and 200 characters")
    private String description;

    @NotNull(message = "Category Id cannot be null")
    private Long categoryId;

    public SubCategoryDTO() {}

    public SubCategoryDTO(String subcategoryName, String description, Long categoryId) {
        this.subcategoryName = subcategoryName;
        this.description = description;
        this.categoryId = categoryId;
    }
    public SubCategoryDTO(String subcategoryName, String description, Category category) {
        this.subcategoryName = subcategoryName;
        this.description = description;
        this.categoryId = category.getCategoryId();
    }
}

