package com.group4.cursus.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


import com.group4.cursus.dto.SubCategoryDTO;
import com.group4.cursus.entity.Category;
import com.group4.cursus.entity.Course;
import com.group4.cursus.entity.SubCategory;
import com.group4.cursus.repository.CategoryRepository;
import com.group4.cursus.repository.SubCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class SubCategoryService {
    @Autowired
    private SubCategoryRepository subCategoryRepository;

    @Autowired
    private CategoryRepository categoryRepository;


    public Page<SubCategoryDTO> getListSubCatePage(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<SubCategory> subCategoryPage = subCategoryRepository.findAll(pageable);
        return subCategoryPage.map(subCategory -> new SubCategoryDTO(
                subCategory.getSubcategoryName(),
                subCategory.getDescription(),
                subCategory.getCategory()));
    }

    public List<SubCategoryDTO> searchSubCategoryByName(String courseName) {
        List<SubCategory> subCategories = subCategoryRepository.findBySubCateNameContainingIgnoreCase(courseName);
        if (subCategories == null) {
            throw new IllegalArgumentException("Subcategory not found with name: " + courseName);
        }
        List<SubCategoryDTO> subCategoryDTOS = new ArrayList<>();
        for (SubCategory subCategory : subCategories) {
            SubCategoryDTO subCategoryDTO = new SubCategoryDTO(subCategory.getSubcategoryName(), subCategory.getDescription(), subCategory.getCategory());
            subCategoryDTOS.add(subCategoryDTO);
        }
        return subCategoryDTOS;
    }

    public SubCategory addSubCategory(SubCategoryDTO subCategoryDTO) {
        Category category = categoryRepository.findById(subCategoryDTO.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found with ID: " + subCategoryDTO.getCategoryId()));
        SubCategory subCategory = new SubCategory(subCategoryDTO.getSubcategoryName(), subCategoryDTO.getDescription(), category);
        return subCategoryRepository.save(subCategory);
    }

    public Optional<SubCategory> getSubCategoryById(Long subCategoryId) {
        return subCategoryRepository.findById(subCategoryId);
    }


    public List<SubCategory> getAllSubCategories(){
        return subCategoryRepository.findAll();
    }


 /*   public SubCategory updateSubCategory(SubCategoryDTO subCategoryDTO) {
        if(!subCategoryRepository.existsById(subCategoryDTO.getsubcategoryId()))
            return null;
        SubCategory subcategory = new SubCategory(subCategoryDTO.getSubcategoryId(), subCategoryDTO.getSubcategoryName(), subCategoryDTO.getDescription(), null);
        return subCategoryRepository.save(subcategory);
    }*/

    public boolean deleteSubCategoryById(Long subCategoryId) {
        if (!subCategoryRepository.existsById(subCategoryId)) {
            return false;
        }
        subCategoryRepository.deleteById(subCategoryId);
        return true;
    }
    public List<Course> findCoursesBySubCategoryId(Long subCategoryId) {
        return subCategoryRepository.findById(subCategoryId)
                .map(SubCategory::getCourses)
                .orElse(Collections.emptyList()); // Return an empty list if the subcategory is not found
    }

    public List<SubCategoryDTO> getAllSubCategoriesBasic() {
        return subCategoryRepository.findAll().stream()
                .map(subCategory -> new SubCategoryDTO(subCategory.getSubcategoryName(), subCategory.getDescription(), subCategory.getCategory().getCategoryId()))
                .collect(Collectors.toList());
    }
}
