package com.group4.cursus.controller;

import com.group4.cursus.dto.CourseDTO;
import com.group4.cursus.service.CourseService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/student/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @GetMapping(path = "/id/{id}")
    public ResponseEntity<CourseDTO> getCourseById(@PathVariable @NotNull Long id) {
        try {
            CourseDTO courseDTO = courseService.getCourseById(id);
            return ResponseEntity.ok(courseDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping(path = "/cat/{categoryId}")
    @ResponseStatus(value = HttpStatus.OK)
    public Page<CourseDTO> getCoursesByCategory(@PathVariable @NotNull Long categoryId,
                                                @RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "10") int size) {
        return courseService.getCoursesByCategory(categoryId, page, size);
    }

    @GetMapping(path = "/subcat/{subcategoryId}")
    @ResponseStatus(value = HttpStatus.OK)
    public Page<CourseDTO> getCoursesBySubcategory(@PathVariable @NotNull Long subcategoryId,
                                                   @RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "10") int size) {
        return courseService.getCoursesBySubcategory(subcategoryId, page, size);
    }

    @GetMapping(path = "/search")
    @ResponseStatus(value = HttpStatus.OK)
    public Page<CourseDTO> searchCourses(@RequestParam @NotBlank String keyword,
                                         @RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "10") int size,
                                         @RequestParam(defaultValue = "courseTitle") String sortBy) {
        return courseService.searchCourses(keyword, page, size, sortBy);
    }
}
