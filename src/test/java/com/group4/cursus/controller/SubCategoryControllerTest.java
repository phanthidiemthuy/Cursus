package com.group4.cursus.controller;

import com.group4.cursus.dto.MyCustomResponse;
import com.group4.cursus.dto.SubCategoryDTO;
import com.group4.cursus.entity.Course;
import com.group4.cursus.entity.SubCategory;
import com.group4.cursus.service.SubCategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class SubCategoryControllerTest {

    @Mock
    private SubCategoryService subCategoryService;

    @InjectMocks
    private SubCategoryController subCategoryController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testListCategories() {
        Page<SubCategoryDTO> subCategoryDTOS = new PageImpl<>(Collections.singletonList(new SubCategoryDTO()));
        when(subCategoryService.getListSubCatePage(0, 2)).thenReturn(subCategoryDTOS);

        ResponseEntity<Page<SubCategoryDTO>> response = subCategoryController.listCategories(0, 2);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(subCategoryDTOS, response.getBody());
    }

    @Test
    public void testGetCategoryById() {
        List<SubCategoryDTO> subCategoryDTOS = Collections.singletonList(new SubCategoryDTO());
        when(subCategoryService.searchSubCategoryByName(anyString())).thenReturn(subCategoryDTOS);

        ResponseEntity<List<SubCategoryDTO>> response = subCategoryController.getCategoryById("SubCategoryName");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(subCategoryDTOS, response.getBody());
    }

    @Test
    public void testAddSubcategory() {
        SubCategoryDTO subCategoryDTO = new SubCategoryDTO();
        SubCategory subCategory = new SubCategory();
        when(subCategoryService.addSubCategory(subCategoryDTO)).thenReturn(subCategory);

        ResponseEntity<SubCategory> response = subCategoryController.addSubcategory(subCategoryDTO);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(subCategory, response.getBody());
    }

    @Test
    public void testGetAllSubCategories() {
        List<SubCategory> subCategories = Collections.singletonList(new SubCategory());
        when(subCategoryService.getAllSubCategories()).thenReturn(subCategories);

        ResponseEntity<List<SubCategory>> response = subCategoryController.getAllSubCategories();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(subCategories, response.getBody());
    }

    @Test
    public void testGetAllSubCategories_NoContent() {
        when(subCategoryService.getAllSubCategories()).thenReturn(Collections.emptyList());

        ResponseEntity<List<SubCategory>> response = subCategoryController.getAllSubCategories();
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    public void testGetAllSubCategoriesBasic() {
        List<SubCategoryDTO> subCategories = Collections.singletonList(new SubCategoryDTO());
        when(subCategoryService.getAllSubCategoriesBasic()).thenReturn(subCategories);

        ResponseEntity<List<SubCategoryDTO>> response = subCategoryController.getAllSubCategoriesBasic();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(subCategories, response.getBody());
    }

    @Test
    public void testGetAllSubCategoriesBasic_NoContent() {
        when(subCategoryService.getAllSubCategoriesBasic()).thenReturn(Collections.emptyList());

        ResponseEntity<List<SubCategoryDTO>> response = subCategoryController.getAllSubCategoriesBasic();
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    public void testGetSubCategoryById() {
        SubCategory subCategory = new SubCategory();
        when(subCategoryService.getSubCategoryById(anyLong())).thenReturn(Optional.of(subCategory));

        ResponseEntity<SubCategory> response = subCategoryController.getSubCategoryById(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(subCategory, response.getBody());
    }

    @Test
    public void testGetSubCategoryById_NotFound() {
        when(subCategoryService.getSubCategoryById(anyLong())).thenReturn(Optional.empty());

        ResponseEntity<SubCategory> response = subCategoryController.getSubCategoryById(1L);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testDeleteSubCategoryById_Success() {
        when(subCategoryService.deleteSubCategoryById(anyLong())).thenReturn(true);

        ResponseEntity<String> response = subCategoryController.deleteSubCategoryById(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(new MyCustomResponse("Subcategory deleted successfully", true).toString(), response.getBody());
    }

    @Test
    public void testDeleteSubCategoryById_Failure() {
        when(subCategoryService.deleteSubCategoryById(anyLong())).thenReturn(false);

        ResponseEntity<String> response = subCategoryController.deleteSubCategoryById(1L);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(new MyCustomResponse("Error deleting subcategory", false).toString(), response.getBody());
    }

    @Test
    public void testGetCoursesBySubCategoryId() {
        List<Course> courses = Collections.singletonList(new Course());
        when(subCategoryService.findCoursesBySubCategoryId(anyLong())).thenReturn(courses);

        ResponseEntity<List<Course>> response = subCategoryController.getCoursesBySubCategoryId(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(courses, response.getBody());
    }

    @Test
    public void testGetCoursesBySubCategoryId_NoContent() {
        when(subCategoryService.findCoursesBySubCategoryId(anyLong())).thenReturn(Collections.emptyList());

        ResponseEntity<List<Course>> response = subCategoryController.getCoursesBySubCategoryId(1L);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }
}
