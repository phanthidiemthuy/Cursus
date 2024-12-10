package com.group4.cursus.controller;

import java.util.List;
import java.util.Optional;
import com.group4.cursus.dto.CategoryDTO;
import com.group4.cursus.dto.MyCustomResponse;
import com.group4.cursus.entity.Category;
import com.group4.cursus.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/admin/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;


    @GetMapping("/pageable")
    public ResponseEntity<Page<CategoryDTO>> listCategories(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "2") int size){
        Page<CategoryDTO> categories = categoryService.getListCategory(page, size);
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/searchByName/{categoryName}")
    public ResponseEntity<List<CategoryDTO>> searchCategory(@PathVariable String categoryName) {
        List<CategoryDTO> categories = categoryService.searchCategoriesByName(categoryName);
        return ResponseEntity.ok(categories);
    }

    @PostMapping()
    public ResponseEntity<Category> addCategory(@Validated @RequestBody CategoryDTO categoryDTO) {
        Category category = categoryService.addCategory(categoryDTO);
        if(category==null)
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        return ResponseEntity.ok().body(category);
    }

    @GetMapping()
    public ResponseEntity<List<Category>> getAllCategories(){
        return ResponseEntity.ok().body(categoryService.getAllCategories());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<Category>> getCategoryById(@PathVariable Long id){
        return ResponseEntity.ok().body(categoryService.getCategoryById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable Long id, @Validated @RequestBody CategoryDTO categoryDTO) {
        Category updatedCategory = categoryService.updateCategory(id, categoryDTO);
        return ResponseEntity.ok(updatedCategory);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCategoryById(@PathVariable Long id) {
        try {
            int result = categoryService.deleteCategoryById(id);
            if(result == 1) {
                return ResponseEntity.ok(new MyCustomResponse("Category deleted successfully", true).toString());
            } else {
                return ResponseEntity.badRequest().body(MyCustomResponse.fail("Category not found").toString());
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(MyCustomResponse.fail("Error: " + e.getMessage()).toString());
        }
    }

}

