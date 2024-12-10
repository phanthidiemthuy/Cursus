package com.group4.cursus.repository;

import com.group4.cursus.entity.Category;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ExtendWith(SpringExtension.class)
@DataJpaTest
public class CategoryRepositoryTest {
    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    public void testFindByCategoryNameContainingIgnoreCase() {
        // Given
        Category category = new Category("Test Category", "This is a test category");
        categoryRepository.save(category);

        // When
        List<Category> foundCategories = categoryRepository.findByCategoryNameContainingIgnoreCase("test");

        // Then
        Assertions.assertEquals(1, foundCategories.size());
        Assertions.assertEquals("Test Category", foundCategories.get(0).getCategoryName());
    }

    @Test
    public void testExistsByCategoryName() {
        // Given
        Category category = new Category("Unique Category", "This is a unique category");
        categoryRepository.save(category);

        // When
        boolean exists = categoryRepository.existsByCategoryName("Unique Category");

        // Then
        Assertions.assertTrue(exists);
    }
}
