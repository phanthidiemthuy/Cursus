package com.group4.cursus.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CategoryDTO {

    @NotNull(message = "Category name cannot be null")
    @Size(min = 5, max = 100, message = "Category name must be between 5 and 100 characters")
    @Pattern(regexp = "^[a-zA-Z0-9 ]*$", message = "Category name must contain only letters, numbers, and spaces")
    private String categoryName;

    @NotNull(message = "Category description canznot be null")
    @Size(min = 10, max = 200, message = "Category description must be between 10 and 200 characters")
    private String description;

    public CategoryDTO(String categoryName, String description) {
        this.categoryName = categoryName;
        this.description = description;
    }
}
