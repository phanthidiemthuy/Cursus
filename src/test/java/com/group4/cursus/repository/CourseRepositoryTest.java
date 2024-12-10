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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ExtendWith(SpringExtension.class)
@DataJpaTest
public class CourseRepositoryTest {
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private SubCategoryRepository subCategoryRepository; // Assuming you have this repository
    @Autowired
    private InstructorRepository instructorRepository; // Assuming you have this repository
    @Autowired
    private CategoryRepository categoryRepository;

    private SubCategory subCategory;
    private Instructor instructor;
    private Category category;

    @BeforeEach
    public void setUp() {
        // Clear the database before each test
        courseRepository.deleteAll();
        subCategoryRepository.deleteAll();
        instructorRepository.deleteAll();
        categoryRepository.deleteAll();

        // Create and save a Category
        category = new Category();
        category.setCategoryName("Test Category");
        category.setDescription("Description Test Category");
        categoryRepository.save(category);

        // Create and save a SubCategory
        subCategory = new SubCategory();
        subCategory.setSubcategoryName("Test SubCategory");
        subCategory.setDescription("Description Test SubCategory");
        subCategory.setCategory(category);
        subCategoryRepository.save(subCategory);

        // Create and save an Instructor
        instructor = new Instructor();
        instructor.setEmail("test@instructor.com");
        instructor.setExperience("Very good");
        instructor.setSalary(new BigDecimal("100.0"));
        instructorRepository.save(instructor);

        // Add some test data
        Course course1 = new Course();
        course1.setCourseTitle("Course 1");
        course1.setDescription("Description 1");
        course1.setRequirements("Requirements 1");
        course1.setCourseLevel("Beginner");
        course1.setThumbnail("thumbnail1.jpg");
        course1.setRegularPrice(BigDecimal.valueOf(100));
        course1.setStatus("APPROVED");
        course1.setIsBlocked(0);
        course1.setSubCategory(subCategory);
        course1.setInstructor(instructor);
        courseRepository.save(course1);

        Course course2 = new Course();
        course2.setCourseTitle("Course 2");
        course2.setDescription("Description 2");
        course2.setRequirements("Requirements 2");
        course2.setCourseLevel("Intermediate");
        course2.setThumbnail("thumbnail2.jpg");
        course2.setRegularPrice(BigDecimal.valueOf(200));
        course2.setStatus("PENDING");
        course2.setIsBlocked(1);
        course2.setSubCategory(subCategory);
        course2.setInstructor(instructor);
        courseRepository.save(course2);
    }

    @Test
    public void testFindAllByStatus() {
        List<Course> courses = courseRepository.findAllByStatus("APPROVED");
        assertThat(courses).hasSize(1);

        courses = courseRepository.findAllByStatus("PENDING");
        assertThat(courses).hasSize(1);

        courses = courseRepository.findAllByStatus("REJECTED");
        assertThat(courses).isEmpty();
    }

    @Test
    public void testFindByInstructor_Email() {
        List<Course> courses = courseRepository.findByInstructor_Email("test@instructor.com");
        assertThat(courses).hasSize(2);

        courses = courseRepository.findByInstructor_Email("nonexistent@instructor.com");
        assertThat(courses).isEmpty();
    }

    @Test
    public void testGetCartListByUser() {
        // Assuming you have set up Cart and Student entities and relationships
        // Add test logic here
    }

    @Test
    public void testFindByCategoryId() {
        Page<Course> courses = courseRepository.findByCategoryId(category.getCategoryId(), PageRequest.of(0, 10));
        assertThat(courses).hasSize(1);
    }

    @Test
    public void testFindBySubcategoryId() {
        Page<Course> courses = courseRepository.findBySubcategoryId(subCategory.getSubcategoryId(), PageRequest.of(0, 10));
        assertThat(courses).hasSize(1);
    }

    @Test
    public void testSearchByKeyword() {
        Page<Course> courses = courseRepository.searchByKeyword("Course", PageRequest.of(0, 10));
        assertThat(courses).hasSize(1);

        courses = courseRepository.searchByKeyword("Nonexistent", PageRequest.of(0, 10));
        assertThat(courses).isEmpty();
    }
}
