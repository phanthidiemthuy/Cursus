package com.group4.cursus.entity;

import com.group4.cursus.repository.CategoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

@DataJpaTest
@ActiveProfiles("test")
public class CategoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    public void testAddCategory() {
        // Given
        Category category = new Category("Test Category", "This is a test category");

        // When
        Category savedCategory = categoryRepository.save(category);

        // Then
        Assertions.assertNotNull(savedCategory);
        Assertions.assertNotNull(savedCategory.getCategoryId());
        Assertions.assertEquals("Test Category", savedCategory.getCategoryName());
        Assertions.assertEquals("This is a test category", savedCategory.getDescription());
    }

    @Test
    public void testGetCategoryById() {
        // Given
        Category category = new Category("Test Category", "This is a test category");
        Category savedCategory = categoryRepository.save(category);

        // When
        Optional<Category> foundCategory = categoryRepository.findById(savedCategory.getCategoryId());

        // Then
        Assertions.assertTrue(foundCategory.isPresent());
        Assertions.assertEquals(savedCategory.getCategoryId(), foundCategory.get().getCategoryId());
    }

    @Test
    public void testUpdateCategory() {
        // Given
        Category category = new Category("Test Category", "This is a test category");
        Category savedCategory = categoryRepository.save(category);

        // When
        savedCategory.setCategoryName("Updated Category");
        savedCategory.setDescription("This is an updated description");
        Category updatedCategory = categoryRepository.save(savedCategory);

        // Then
        Assertions.assertEquals("Updated Category", updatedCategory.getCategoryName());
        Assertions.assertEquals("This is an updated description", updatedCategory.getDescription());
    }

    @Test
    public void testDeleteCategory() {
        // Given
        Category category = new Category("Test Category", "This is a test category");
        Category savedCategory = categoryRepository.save(category);

        // When
        categoryRepository.deleteById(savedCategory.getCategoryId());
        Optional<Category> foundCategory = categoryRepository.findById(savedCategory.getCategoryId());

        // Then
        Assertions.assertFalse(foundCategory.isPresent());
    }
}