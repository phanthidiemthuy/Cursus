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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ExtendWith(SpringExtension.class)
@DataJpaTest
public class ReportRepositoryTest {
    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private SubCategoryRepository subCategoryRepository;

    @Autowired
    private InstructorRepository instructorRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    private Course course;
    private Student student;
    private Report report1;
    private Report report2;
    private Category category;
    private SubCategory subCategory;
    private Enrollment enrollment;
    private Instructor instructor;

    @BeforeEach
    public void setUp() {
        // Clear the database before each test
        reportRepository.deleteAll();
        enrollmentRepository.deleteAll();
        studentRepository.deleteAll();
        courseRepository.deleteAll();
        categoryRepository.deleteAll();
        subCategoryRepository.deleteAll();
        instructorRepository.deleteAll();

        // Create and save entities
        category = new Category();
        category.setCategoryName("Test Category");
        category.setDescription("Description Test Category");
        categoryRepository.save(category);

        subCategory = new SubCategory();
        subCategory.setSubcategoryName("Test SubCategory");
        subCategory.setDescription("Description Test SubCategory");
        subCategory.setCategory(category);
        subCategoryRepository.save(subCategory);

        instructor = new Instructor();
        instructor.setEmail("test@instructor.com");
        instructor.setExperience("Very good");
        instructor.setSalary(new BigDecimal("100.0"));
        instructorRepository.save(instructor);

        course = new Course();
        course.setCourseTitle("Course Title");
        course.setCourseLevel("Beginner");
        course.setDescription("Course Description");
        course.setThumbnail("thumbnail.jpg");
        course.setRequirements("Requirement");
        course.setRegularPrice(BigDecimal.valueOf(100));
        course.setStatus("APPROVED");
        course.setIsBlocked(0);
        course.setInstructor(instructor);
        course.setSubCategory(subCategory);
        courseRepository.save(course);

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

        enrollment = new Enrollment();
        enrollment.setCourse(course);
        enrollment.setStudent(student);
        enrollment.setEnrollmentDate(LocalDate.now());
        enrollment.setProgress(0);
        enrollmentRepository.save(enrollment);

        report1 = new Report();
        report1.setContents("Report content 1");
        report1.setImages("image1.jpg");
        report1.setCourse(course);
        report1.setStudent(student);
        reportRepository.save(report1);

        report2 = new Report();
        report2.setContents("Report content 2");
        report2.setImages("image2.jpg");
        report2.setCourse(course);
        report2.setStudent(student);
        reportRepository.save(report2);
    }

    @Test
    public void testFindReportByCourse() {
        List<Report> reports = reportRepository.findReportByCourse(course.getCourseId());

        assertThat(reports).hasSize(2);
        assertThat(reports).extracting("course").containsOnly(course);
        assertThat(reports).extracting("contents").containsExactlyInAnyOrder("Report content 1", "Report content 2");
    }
}
