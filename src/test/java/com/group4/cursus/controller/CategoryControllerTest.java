package com.group4.cursus.controller;

import com.group4.cursus.dto.CategoryDTO;
import com.group4.cursus.entity.Category;
import com.group4.cursus.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
public class CategoryControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    private CategoryDTO mockCategoryDTO() {
        return new CategoryDTO("test", "description");
    }

    private Category mockCategory() {
        return new Category(1L, "test", "description");
    }

    @Test
    @WithMockUser(username = "adminnehihi", roles = {"SUPER_ADMIN"})
    public void testListCategories() throws Exception {
        Page<CategoryDTO> categoryPage = new PageImpl<>(Collections.singletonList(mockCategoryDTO()));
        Mockito.when(categoryService.getListCategory(Mockito.anyInt(), Mockito.anyInt())).thenReturn(categoryPage);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/admin/category/pageable")
                        .param("page", "0")
                        .param("size", "2"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].categoryName").value("test"))
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "adminnehihi", roles = {"SUPER_ADMIN"})
    public void testSearchCategory() throws Exception {
        List<CategoryDTO> categories = Collections.singletonList(mockCategoryDTO());
        Mockito.when(categoryService.searchCategoriesByName(Mockito.anyString())).thenReturn(categories);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/admin/category/searchByName/test"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].categoryName").value("test"))
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "adminnehihi", roles = {"SUPER_ADMIN"})
    public void testAddCategory() throws Exception {
        Category category = mockCategory();
        Mockito.when(categoryService.addCategory(Mockito.any(CategoryDTO.class))).thenReturn(category);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/admin/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"categoryName\":\"testtt\",\"description\":\"description\"}"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.categoryId").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.categoryName").value("test"))
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "adminnehihi", roles = {"SUPER_ADMIN"})
    public void testGetAllCategories() throws Exception {
        List<Category> categories = Collections.singletonList(mockCategory());
        Mockito.when(categoryService.getAllCategories()).thenReturn(categories);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/admin/category"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].categoryId").value(1L))
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "adminnehihi", roles = {"SUPER_ADMIN"})
    public void testGetCategoryById() throws Exception {
        Optional<Category> category = Optional.of(mockCategory());
        Mockito.when(categoryService.getCategoryById(Mockito.anyLong())).thenReturn(category);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/admin/category/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.categoryId").value(1L))
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "adminnehihi", roles = {"SUPER_ADMIN"})
    public void testUpdateCategory() throws Exception {
        Category updatedCategory = mockCategory();
        Mockito.when(categoryService.updateCategory(Mockito.anyLong(), Mockito.any(CategoryDTO.class))).thenReturn(updatedCategory);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/admin/category/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"categoryName\":\"testtt\",\"description\":\"description\"}"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.categoryId").value(1L))
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "adminnehihi", roles = {"SUPER_ADMIN"})
    public void testDeleteCategoryById() throws Exception {
        Mockito.when(categoryService.deleteCategoryById(Mockito.anyLong())).thenReturn(1);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/admin/category/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("{\"message\":\"Category deleted successfully\",\"success\":true}"))
                .andDo(print());

        Mockito.when(categoryService.deleteCategoryById(Mockito.anyLong())).thenReturn(0);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/admin/category/1"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json("{\"message\":\"Category not found\",\"success\":false}"))
                .andDo(print());

        Mockito.doThrow(new RuntimeException("Some error")).when(categoryService).deleteCategoryById(Mockito.anyLong());

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/admin/category/1"))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().json("{\"message\":\"Error: Some error\",\"success\":false}"))
                .andDo(print());
    }
}
