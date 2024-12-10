package com.group4.cursus.repository;

import com.group4.cursus.entity.Category;
import com.group4.cursus.entity.Course;
import com.group4.cursus.entity.Instructor;
import com.group4.cursus.entity.SubCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ExtendWith(SpringExtension.class)
@DataJpaTest
public class SubCategoryRepositoryTest {
    @Autowired
    private SubCategoryRepository subCategoryRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private InstructorRepository instructorRepository;

    private Category category;
    private SubCategory subCategory;
    private Instructor instructor;
    private Course course;

    @BeforeEach
    public void setUp() {
        // Clear the database before each test
//        subCategoryRepository.deleteAll();
//        categoryRepository.deleteAll();
//        courseRepository.deleteAll();

        // Create and save a Category
        category = new Category();
        category.setCategoryName("Test Category");
        category.setDescription("Description for Test Category");
        categoryRepository.save(category);

        // Create and save an Instructor
        instructor = new Instructor();
        instructor.setEmail("test@instructor.com");
        instructor.setExperience("Very good");
        instructor.setSalary(new BigDecimal("100.0"));
        instructorRepository.save(instructor);

        // Create and save a SubCategory
        subCategory = new SubCategory();
        subCategory.setCategory(category);
        subCategory.setSubcategoryName("Test SubCategory");
        subCategory.setDescription("Description for Test SubCategory");
        subCategoryRepository.save(subCategory);

        // Add some test data
        course = new Course();
        course.setCourseTitle("Course 1");
        course.setDescription("Description 1");
        course.setRequirements("Requirements 1");
        course.setCourseLevel("Beginner");
        course.setThumbnail("thumbnail1.jpg");
        course.setRegularPrice(BigDecimal.valueOf(100));
        course.setStatus("APPROVED");
        course.setIsBlocked(0);
        course.setSubCategory(subCategory);
        course.setInstructor(instructor);
        courseRepository.save(course);

        subCategory.setCourses(Collections.singletonList(course));

        // Log the subcategoryId to ensure it's being saved correctly
        System.out.println("Saved SubCategory ID: " + subCategory.getSubcategoryId());
    }

    @Test
    public void testFindByIdWithCourses() {
        System.out.println("Testing findByIdWithCourses with ID: " + subCategory.getSubcategoryId());
        Optional<SubCategory> foundSubCategory = subCategoryRepository.findByIdWithCourses(subCategory.getSubcategoryId());

        assertThat(foundSubCategory).isPresent();
        assertThat(foundSubCategory.get().getSubcategoryName()).isEqualTo(subCategory.getSubcategoryName());
        assertThat(foundSubCategory.get().getCourses()).isNotEmpty();  // Assuming no courses are added
    }

    @Test
    public void testFindBySubCateNameContainingIgnoreCase() {
        List<SubCategory> subCategories = subCategoryRepository.findBySubCateNameContainingIgnoreCase("test subcategory");
        assertThat(subCategories).hasSize(1);
        assertThat(subCategories.get(0).getSubcategoryName()).isEqualTo("Test SubCategory");

        subCategories = subCategoryRepository.findBySubCateNameContainingIgnoreCase("nonexistent");
        assertThat(subCategories).isEmpty();
    }
}