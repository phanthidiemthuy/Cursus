package com.group4.cursus.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.group4.cursus.dto.CategoryDTO;
import com.group4.cursus.entity.Category;
import com.group4.cursus.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;


@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @InjectMocks
    private CategoryService categoryService;

    @Mock
    private CategoryRepository categoryRepository;


    @Test
    public void testAddCategory() {
        CategoryDTO categoryDTO = new CategoryDTO("Framework Play", "This is a course basic");
        Category savedCategory = new Category(categoryDTO.getCategoryName(), categoryDTO.getDescription());
        when(categoryRepository.save(any(Category.class))).thenReturn(savedCategory);

        Category addedCategory = categoryService.addCategory(categoryDTO);

        assertEquals(categoryDTO.getCategoryName(), addedCategory.getCategoryName());
        assertEquals(categoryDTO.getDescription(), addedCategory.getDescription());
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    public void testGetCategoryById_ExistingId() {
        Long categoryId = 1L;
        Category expectedCategory = new Category(categoryId, "Framework Play", "This is a course basic");
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(expectedCategory));

        Optional<Category> getCategory = categoryService.getCategoryById(categoryId);

        assertTrue(getCategory.isPresent());
        assertEquals(expectedCategory, getCategory.get());
        verify(categoryRepository).findById(categoryId);
    }

    @Test
    public void testGetCategoryById_NonExistingId() {
        Long categoryId = 1L;
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        Optional<Category> getCategory = categoryService.getCategoryById(categoryId);

        assertFalse(getCategory.isPresent());
        verify(categoryRepository).findById(categoryId);
    }

    @Test
    public void testGetAllCategories() {
        List<Category> expectedCategories = new ArrayList<>();
        expectedCategories.add(new Category(1L, "Framework Play", "This is a course basic for beginner"));
        expectedCategories.add(new Category(2L, "Framework Spring", "This is a course basic for beginner"));
        when(categoryRepository.findAll()).thenReturn(expectedCategories);

        List<Category> categories = categoryService.getAllCategories();

        assertEquals(expectedCategories.size(), categories.size());
        for (int i = 0; i < expectedCategories.size(); i++) {
            assertEquals(expectedCategories.get(i).getCategoryId(), categories.get(i).getCategoryId());
            assertEquals(expectedCategories.get(i).getCategoryName(), categories.get(i).getCategoryName());
            assertEquals(expectedCategories.get(i).getDescription(), categories.get(i).getDescription());
        }
        verify(categoryRepository).findAll();
    }

    @Test
    public void testUpdateCategory_ExistingId() {
        Long categoryId = 1L;
        CategoryDTO updateCategoryDTO = new CategoryDTO("Framework Spring", "This is a course population");
        Category categoryToUpdate = new Category(categoryId, "Framework Spring", "This is a course population and large");
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(categoryToUpdate));
        when(categoryRepository.save(any(Category.class))).thenReturn(categoryToUpdate);
        Category updatedCategory = categoryService.updateCategory(categoryId, updateCategoryDTO);
        assertEquals(categoryId, updatedCategory.getCategoryId());
        assertEquals(updateCategoryDTO.getCategoryName(), updatedCategory.getCategoryName());
        assertEquals(updateCategoryDTO.getDescription(), updatedCategory.getDescription());
        verify(categoryRepository).findById(categoryId);
        verify(categoryRepository).save(categoryToUpdate);
    }

    @Test
    public void testUpdateCategory_NonExistingId() {
        Long categoryId = 1L;
        CategoryDTO updateCategoryDTO = new CategoryDTO("Framework Spring", "This is a course population and large");
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> categoryService.updateCategory(categoryId, updateCategoryDTO));
        verify(categoryRepository).findById(categoryId);
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    public void testDeleteCategoryById_ExistingId() {
        Long categoryId = 1L;
        when(categoryRepository.existsById(categoryId)).thenReturn(true);
        int result = categoryService.deleteCategoryById(categoryId);
        assertEquals(1, result);
        verify(categoryRepository).existsById(categoryId);
        verify(categoryRepository).deleteById(categoryId);
    }

    @Test
    public void testDeleteCategoryById_NonExistingId() {
        Long categoryId = 1L;

        when(categoryRepository.existsById(categoryId)).thenReturn(false);

        int result = categoryService.deleteCategoryById(categoryId);

        assertEquals(0, result);
        verify(categoryRepository).existsById(categoryId);
        verify(categoryRepository, never()).deleteById(categoryId);
    }

    @Test
    public void testGetListCategory() {
        int page = 0;
        int size = 10;
        List<Category> categories = new ArrayList<>();
        categories.add(new Category(1L, "Framework Play", "This is a course basic for beginner"));
        categories.add(new Category(2L, "Framework Spring", "This is a course basic for beginner"));
        Page<Category> categoryPage = new PageImpl<>(categories);
        Pageable pageable = PageRequest.of(page, size);
        when(categoryRepository.findAll(pageable)).thenReturn(categoryPage);
        Page<CategoryDTO> result = categoryService.getListCategory(page, size);
        assertEquals(2, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertEquals("Framework Play", result.getContent().get(0).getCategoryName());
        assertEquals("This is a course basic for beginner", result.getContent().get(0).getDescription());
        assertEquals("Framework Spring", result.getContent().get(1).getCategoryName());
        assertEquals("This is a course basic for beginner", result.getContent().get(1).getDescription());
        verify(categoryRepository).findAll(pageable);
    }

    @Test
    public void testSearchCategoriesByName() {
        String categoryName = "Test";
        List<Category> categories = new ArrayList<>();
        categories.add(new Category(1L, "Framework Play", "This is a course basic for beginner"));
        categories.add(new Category(2L, "Framework Spring", "This is a course basic for beginner"));
        when(categoryRepository.findByCategoryNameContainingIgnoreCase(categoryName)).thenReturn(categories);
        List<CategoryDTO> result = categoryService.searchCategoriesByName(categoryName);
        assertEquals(2, result.size());
        assertEquals("Framework Play", result.get(0).getCategoryName());
        assertEquals("This is a course basic for beginner", result.get(0).getDescription());
        assertEquals("Framework Spring", result.get(1).getCategoryName());
        assertEquals("This is a course basic for beginner", result.get(1).getDescription());
        verify(categoryRepository).findByCategoryNameContainingIgnoreCase(categoryName);
    }
}
