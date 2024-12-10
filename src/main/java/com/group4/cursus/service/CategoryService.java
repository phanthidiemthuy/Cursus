package com.group4.cursus.service;



import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.group4.cursus.dto.CategoryDTO;
import com.group4.cursus.entity.Category;
import com.group4.cursus.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public Category addCategory(CategoryDTO categoryDTO) {
        Category category = new Category();
        category.setCategoryName(categoryDTO.getCategoryName());
        category.setDescription(categoryDTO.getDescription());
        return categoryRepository.save(category);
    }

    public Optional<Category> getCategoryById(Long categoryId){
        return categoryRepository.findById(categoryId);
    }

    public List<Category> getAllCategories(){
        return categoryRepository.findAll();
    }

    public Category updateCategory(Long id, CategoryDTO categoryDTO) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));

        category.setCategoryName(categoryDTO.getCategoryName());
        category.setDescription(categoryDTO.getDescription());

        return categoryRepository.save(category);
    }


    public int deleteCategoryById(Long categoryId) {
        if(!categoryRepository.existsById(categoryId))
            return 0;
        categoryRepository.deleteById(categoryId);
        return 1;
    }

    public Page<CategoryDTO> getListCategory(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Category> categoryPage = categoryRepository.findAll(pageable);
        return categoryPage.map(category -> new CategoryDTO(
                category.getCategoryName(),
                category.getDescription()));
    }

    public List<CategoryDTO> searchCategoriesByName(String categoryName) {
        List<Category> categories = categoryRepository.findByCategoryNameContainingIgnoreCase(categoryName);
        if (categories == null) {
            throw new IllegalArgumentException("Category not found with name: " + categoryName);
        }
        List<CategoryDTO> categoriesDTO = new ArrayList<>();
        for (Category category : categories) {
            CategoryDTO categoryDTO = new CategoryDTO(category.getCategoryName(), category.getDescription());
            categoriesDTO.add(categoryDTO);
        }
        return categoriesDTO;
    }
}
