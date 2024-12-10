package com.group4.cursus.repository;

import com.group4.cursus.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ExtendWith(SpringExtension.class)
@DataJpaTest
public class ReviewRepositoryTest {
    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private InstructorRepository instructorRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private SubCategoryRepository subCategoryRepository;

    private Course course1;
    private Course course2;
    private Student student;
    private Review review1;
    private Review review2;
    private Review review3;
    private Instructor instructor;
    private Category category;
    private SubCategory subCategory;

    @BeforeEach
    public void setUp() {
        // Clear the database before each test
        reviewRepository.deleteAll();
        courseRepository.deleteAll();
        studentRepository.deleteAll();
        subCategoryRepository.deleteAll();
        instructorRepository.deleteAll();

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

        // Create and save Instructor
        instructor = new Instructor();
        instructor.setEmail("test@instructor.com");
        instructor.setExperience("Very good");
        instructor.setSalary(new BigDecimal("100.0"));
        instructorRepository.save(instructor);

        // Create and save Courses
        course1 = new Course();
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

        course2 = new Course();
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

        // Create and save Student
        student = new Student();
        student.setFullName("John Doe");
        student.setEmail("john.doe@example.com");
        student.setPassword("password123");
        student.setRegistrationDate(LocalDate.now());
        student.setAddress("123 Main St");
        student.setBlocked(false);
        student.setApproved(true);
        student.setUserType("STUDENT");
        studentRepository.save(student);

        // Create and save Reviews
        review1 = new Review();
        review1.setRating(5);
        review1.setContents("Excellent course!");
        review1.setCourse(course1);
        review1.setStudent(student);
        reviewRepository.save(review1);

        review2 = new Review();
        review2.setRating(4);
        review2.setContents("Very good, but could be improved.");
        review2.setCourse(course1);
        review2.setStudent(student);
        reviewRepository.save(review2);

        review3 = new Review();
        review3.setRating(3);
        review3.setContents("Average course.");
        review3.setCourse(course2);
        review3.setStudent(student);
        reviewRepository.save(review3);
    }

    @Test
    public void testFindByCourse_CourseId() {
        List<Review> reviews = reviewRepository.findByCourse_CourseId(course1.getCourseId());
        assertThat(reviews).hasSize(2);
        assertThat(reviews).extracting("course").containsOnly(course1);

        reviews = reviewRepository.findByCourse_CourseId(course2.getCourseId());
        assertThat(reviews).hasSize(1);
        assertThat(reviews).extracting("course").containsOnly(course2);
    }

    @Test
    public void testFindByCourseCourseIdIn() {
        List<Long> courseIds = Arrays.asList(course1.getCourseId(), course2.getCourseId());
        List<Review> reviews = reviewRepository.findByCourseCourseIdIn(courseIds);

        assertThat(reviews).hasSize(3);

        // Extract course IDs and compare
        List<Long> reviewCourseIds = reviews.stream()
                .map(review -> review.getCourse().getCourseId())
                .distinct()
                .collect(Collectors.toList());

        assertThat(reviewCourseIds).containsExactlyInAnyOrder(course1.getCourseId(), course2.getCourseId());
    }
}