package com.group4.cursus.repository;

import com.group4.cursus.entity.*;
import com.group4.cursus.entity.Course;
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
public class EnrollmentRepositoryTest {

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private SubCategoryRepository subCategoryRepository;

    @Autowired
    private InstructorRepository instructorRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Category category;
    private SubCategory subCategory;
    private Instructor instructor;
    private Course course1;
    private Course course2;
    private Student student;
    private Enrollment enrollment;

    @BeforeEach
    public void setUp() {
        // Clear the database before each test
        courseRepository.deleteAll();
        subCategoryRepository.deleteAll();
        instructorRepository.deleteAll();
        categoryRepository.deleteAll();
        enrollmentRepository.deleteAll();
        studentRepository.deleteAll();

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

        // Create and save Students
        student = new Student();
        student.setFullName("BOB");
        studentRepository.save(student);

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

        // Create and save an Enrollment
        enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course1);
        enrollment.setEnrollmentDate(LocalDate.now());
        enrollmentRepository.save(enrollment);
    }

    @Test
    public void testExistsByCourse() {
        boolean exists = enrollmentRepository.existsByCourse(course1);
        assertThat(exists).isTrue();

        Course newCourse = new Course();
        newCourse.setCourseTitle("Course 2");
        newCourse.setDescription("Description for Course 3"); // Ensure this field is not null
        newCourse.setRequirements("Requirements 2");
        newCourse.setCourseLevel("Noob");
        newCourse.setThumbnail("thumbnail3.jpg");
        newCourse.setRegularPrice(BigDecimal.valueOf(300));
        newCourse.setStatus("PENDING");
        newCourse.setIsBlocked(0);
        newCourse.setSubCategory(subCategory);
        newCourse.setInstructor(instructor);
        courseRepository.save(newCourse);

        exists = enrollmentRepository.existsByCourse(newCourse);
        assertThat(exists).isFalse();
    }

    @Test
    public void testFindByCourse_CourseId() {
        List<Enrollment> enrollments = enrollmentRepository.findByCourse_CourseId(course1.getCourseId());
        assertThat(enrollments).hasSize(1);
        assertThat(enrollments.get(0).getCourse()).isEqualTo(course1);
    }

    @Test
    public void testExistsByStudentUserIdAndCourseCourseId() {
        boolean exists = enrollmentRepository.existsByStudentUserIdAndCourseCourseId((long) student.getUserId(), course1.getCourseId());
        assertThat(exists).isTrue();

        exists = enrollmentRepository.existsByStudentUserIdAndCourseCourseId((long) student.getUserId(), course2.getCourseId());
        assertThat(exists).isFalse();
    }

    @Test
    public void testFindByStudentUserIdAndCourse_courseId() {
        Enrollment foundEnrollment = enrollmentRepository.findByStudentUserIdAndCourse_courseId((long) student.getUserId(), course1.getCourseId());
        assertThat(foundEnrollment).isNotNull();
        assertThat(foundEnrollment.getCourse()).isEqualTo(course1);

        Enrollment nonExistentEnrollment = enrollmentRepository.findByStudentUserIdAndCourse_courseId((long) student.getUserId(), course2.getCourseId());
        assertThat(nonExistentEnrollment).isNull();
    }

    @Test
    public void testFindCourseByStudentId() {
        List<Enrollment> enrollments = enrollmentRepository.findCourseByStudentId((long) student.getUserId());
        assertThat(enrollments).hasSize(1);
        assertThat(enrollments.get(0).getCourse()).isEqualTo(course1);
    }
}
