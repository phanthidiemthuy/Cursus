package com.group4.cursus.controller;


import com.group4.cursus.dto.MyCustomResponse;
import com.group4.cursus.dto.SubCategoryDTO;
import com.group4.cursus.entity.Course;
import com.group4.cursus.entity.SubCategory;
import com.group4.cursus.service.SubCategoryService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;

@RestController
@RequestMapping("/api/admin/subcategory")
public class SubCategoryController {
    @Autowired
    private SubCategoryService subCategoryService;

    @GetMapping("/pageable")
    public ResponseEntity<Page<SubCategoryDTO>> listCategories(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "2") int size){
        Page<SubCategoryDTO> subCategoryDTOS = subCategoryService.getListSubCatePage(page, size);
        return ResponseEntity.ok(subCategoryDTOS);
    }

    @GetMapping("/searchByName/{subCateName}")
    public ResponseEntity<List<SubCategoryDTO>> getCategoryById(@PathVariable String subCateName) {
        List<SubCategoryDTO> subCategoryDTOS = subCategoryService.searchSubCategoryByName(subCateName);
        return ResponseEntity.ok(subCategoryDTOS);
    }

    @PostMapping
    public ResponseEntity<SubCategory> addSubcategory(@Validated @RequestBody SubCategoryDTO subCategoryDTO) {
        SubCategory subCategory = subCategoryService.addSubCategory(subCategoryDTO);
        return ResponseEntity.ok(subCategory);
    }

    @GetMapping
    public ResponseEntity<List<SubCategory>> getAllSubCategories() {
        List<SubCategory> subCategories = subCategoryService.getAllSubCategories();
        if (subCategories.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(subCategories);
    }

    @GetMapping("/basic")
    public ResponseEntity<List<SubCategoryDTO>> getAllSubCategoriesBasic() {
        List<SubCategoryDTO> subCategories = subCategoryService.getAllSubCategoriesBasic();
        if (subCategories.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(subCategories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubCategory> getSubCategoryById(@PathVariable Long id) {
        return subCategoryService.getSubCategoryById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSubCategoryById(@PathVariable Long id) {
        return subCategoryService.deleteSubCategoryById(id) ?
                ResponseEntity.ok(new MyCustomResponse("Subcategory deleted successfully",true).toString())
                :
                ResponseEntity.badRequest().body(new MyCustomResponse("Error deleting subcategory",false).toString());
    }

    @GetMapping("/{subcategoryId}/courses")
    public ResponseEntity<List<Course>> getCoursesBySubCategoryId(@PathVariable Long subcategoryId) {
        List<Course> courses = subCategoryService.findCoursesBySubCategoryId(subcategoryId);
        if (courses.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(courses);
    }

}
