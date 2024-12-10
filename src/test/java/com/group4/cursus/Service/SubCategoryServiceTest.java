package com.group4.cursus.service;

import com.group4.cursus.dto.CategoryDTO;
import com.group4.cursus.dto.SubCategoryDTO;
import com.group4.cursus.entity.Category;
import com.group4.cursus.entity.Course;
import com.group4.cursus.entity.SubCategory;
import com.group4.cursus.repository.CategoryRepository;
import com.group4.cursus.repository.SubCategoryRepository;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SubCategoryServiceTest {
    @MockBean
    private SubCategoryRepository subCategoryRepository;

    @MockBean
    private CategoryRepository categoryRepository;

    @Autowired
    private SubCategoryService subCategoryService;

    private SubCategoryDTO mockSubCategoryDTO() {
        return new SubCategoryDTO("Subcategory Test", "Description Test", 1L);
    }

    private SubCategory mockSubCategory(Category category) {
        return new SubCategory("Subcategory Test", "Description Test", category);
    }

    private Category mockCategory() {
        return new Category(1L, "Category Test", "Description Test");
    }

    @Test
    public void testSearchSubCategoryByName_WhenNotExists_ThrowException() {
        String name = "test";
        Mockito.when(subCategoryRepository.findBySubCateNameContainingIgnoreCase(name)).thenReturn(Collections.emptyList());

        assertThrows(IllegalArgumentException.class, () -> {
            subCategoryService.searchSubCategoryByName(name);
        });

        Mockito.verify(subCategoryRepository, Mockito.times(1)).findBySubCateNameContainingIgnoreCase(name);
    }

    @Test
    public void testGetAllSubCategory_ReturnsList() {
        List<SubCategory> listSubCategory = Arrays.asList(mockSubCategory(mockCategory()));

        Mockito.when(subCategoryRepository.findAll()).thenReturn(listSubCategory);
        List<SubCategory> result = subCategoryService.getAllSubCategories();

        Mockito.verify(subCategoryRepository, Mockito.times(1)).findAll();
        Assertions.assertEquals(listSubCategory.size(), result.size());
    }

    @Test
    public void testSearchSubCategoryByName_WhenExists_ReturnsList() {
        String name = "Subcategory Test";
        SubCategory subCategory = mockSubCategory(mockCategory());
        List<SubCategory> subCategoryList = Collections.singletonList(subCategory);

        Mockito.when(subCategoryRepository.findBySubCateNameContainingIgnoreCase(name)).thenReturn(subCategoryList);

        List<SubCategoryDTO> result = subCategoryService.searchSubCategoryByName(name);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(subCategory.getSubcategoryName(), result.get(0).getSubcategoryName());
    }

    @Test
    public void testGetSubCategoryById_WhenExists_ReturnsSubCategory() {
        Long id = 1L;
        SubCategory subCategory = mockSubCategory(mockCategory());

        Mockito.when(subCategoryRepository.findById(id)).thenReturn(Optional.of(subCategory));

        Optional<SubCategory> result = subCategoryService.getSubCategoryById(id);

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(subCategory.getSubcategoryName(), result.get().getSubcategoryName());
    }

    @Test
    public void testGetSubCategoryById_WhenNotExists_ReturnsEmptyOptional() {
        Long id = 1L;

        Mockito.when(subCategoryRepository.findById(id)).thenReturn(Optional.empty());

        Optional<SubCategory> result = subCategoryService.getSubCategoryById(id);

        Assertions.assertFalse(result.isPresent());
    }

    @Test
    public void testDeleteSubCategoryById_WhenExists_ReturnsTrue() {
        Long id = 1L;

        Mockito.when(subCategoryRepository.existsById(id)).thenReturn(true);

        boolean result = subCategoryService.deleteSubCategoryById(id);

        Assertions.assertTrue(result);

        Mockito.verify(subCategoryRepository, Mockito.times(1)).existsById(id);
        Mockito.verify(subCategoryRepository, Mockito.times(1)).deleteById(id);
    }

    @Test
    public void testDeleteSubCategoryById_WhenNotExists_ReturnsFalse() {
        Long id = 1L;

        Mockito.when(subCategoryRepository.existsById(id)).thenReturn(false);

        boolean result = subCategoryService.deleteSubCategoryById(id);

        Assertions.assertFalse(result);

        Mockito.verify(subCategoryRepository, Mockito.times(1)).existsById(id);
        Mockito.verify(subCategoryRepository, Mockito.never()).deleteById(id);
    }

    @Test
    public void testFindCoursesBySubCategoryId_WhenExists_ReturnsCourses() {
        Long id = 1L;
        SubCategory subCategory = mockSubCategory(mockCategory());
        List<Course> courses = Collections.singletonList(new Course());

        subCategory.setCourses(courses);

        Mockito.when(subCategoryRepository.findById(id)).thenReturn(Optional.of(subCategory));

        List<Course> result = subCategoryService.findCoursesBySubCategoryId(id);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(courses.size(), result.size());
    }

    @Test
    public void testFindCoursesBySubCategoryId_WhenNotExists_ReturnsEmptyList() {
        Long id = 1L;

        Mockito.when(subCategoryRepository.findById(id)).thenReturn(Optional.empty());

        List<Course> result = subCategoryService.findCoursesBySubCategoryId(id);

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    public void testGetAllSubCategoriesBasic() {
        SubCategory subCategory = mockSubCategory(mockCategory());
        List<SubCategory> subCategories = Collections.singletonList(subCategory);

        Mockito.when(subCategoryRepository.findAll()).thenReturn(subCategories);

        List<SubCategoryDTO> result = subCategoryService.getAllSubCategoriesBasic();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(subCategory.getSubcategoryName(), result.get(0).getSubcategoryName());
    }

    @Test
    public void testAddSubCategory_WhenSuccess_ReturnsSubCategory() {
        SubCategoryDTO subCategoryDTO = mockSubCategoryDTO();
        Category category = mockCategory();
        SubCategory subCategory = mockSubCategory(category);

        Mockito.when(categoryRepository.findById(subCategoryDTO.getCategoryId())).thenReturn(Optional.of(category));
        Mockito.when(subCategoryRepository.save(Mockito.any(SubCategory.class))).thenReturn(subCategory);

        SubCategory result = subCategoryService.addSubCategory(subCategoryDTO);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(subCategory.getSubcategoryName(), result.getSubcategoryName());
        Assertions.assertEquals(subCategory.getDescription(), result.getDescription());

        Mockito.verify(categoryRepository, Mockito.times(1)).findById(subCategoryDTO.getCategoryId());
        Mockito.verify(subCategoryRepository, Mockito.times(1)).save(Mockito.any(SubCategory.class));
    }

    @Test
    public void testGetListSubCategoryWithPage_ReturnPage() {
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);
        SubCategory subCategory = mockSubCategory(mockCategory());
        Page<SubCategory> subCategoryPage = new PageImpl<>(Collections.singletonList(subCategory));

        Mockito.when(subCategoryRepository.findAll(pageable)).thenReturn(subCategoryPage);

        Page<SubCategoryDTO> result = subCategoryService.getListSubCatePage(page, size);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getTotalElements());
        Assertions.assertEquals(subCategory.getSubcategoryName(), result.getContent().get(0).getSubcategoryName());
    }
}
