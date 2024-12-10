package com.group4.cursus.repository;

import com.group4.cursus.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ExtendWith(SpringExtension.class)
@DataJpaTest
public class CartRepositoryTest {
    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private SubCategoryRepository subCategoryRepository;

    @Autowired
    private InstructorRepository instructorRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private StudentRepository studentRepository;

    private Category category;
    private SubCategory subCategory;
    private Instructor instructor;
    private Student student;
    private Course course1;
    private Course course2;

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

        // Create and save Students
        student = new Student();
        student.setFullName("John");
        studentRepository.save(student);

        // Add some test data
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
    }
    @Test
    public void testFindByStudent() {
        Cart cart = new Cart();
        cart.setStudent(student);
        cart.setCourse(course1);
        cart.setAddingDate(LocalDate.now());
        cart.setDescription("Test description");
        cart.setPrice(new BigDecimal("100.00"));
        cartRepository.save(cart);

        List<Cart> carts = cartRepository.findByStudent(student);
        assertThat(carts).hasSize(1);
        assertThat(carts.get(0).getCourse()).isEqualTo(course1);
    }

    @Test
    public void testCheckIfCourseInCart() {
        Cart cart = new Cart();
        cart.setStudent(student);
        cart.setCourse(course1);
        cart.setAddingDate(LocalDate.now());
        cart.setDescription("Test description");
        cart.setPrice(new BigDecimal("100.00"));
        cartRepository.save(cart);

        int count = cartRepository.checkIfCourseInCart((long) student.getUserId(), course1.getCourseId());
        assertThat(count).isEqualTo(1);
    }

    @Test
    public void testFindByStudentUserId() {
        Cart cart = new Cart();
        cart.setStudent(student);
        cart.setCourse(course1);
        cart.setAddingDate(LocalDate.now());
        cart.setDescription("Test description");
        cart.setPrice(new BigDecimal("100.00"));
        cartRepository.save(cart);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Cart> page = cartRepository.findByStudentUserId((long) student.getUserId(), pageable);
        assertThat(page.getTotalElements()).isEqualTo(1);
    }

    @Test
    public void testGetTotalBillForUser() {
        Cart cart = new Cart();
        cart.setStudent(student);
        cart.setCourse(course1);
        cart.setAddingDate(LocalDate.now());
        cart.setDescription("Test description");
        cart.setPrice(new BigDecimal("100.00"));
        cartRepository.save(cart);

        BigDecimal totalBill = cartRepository.getTotalBillForUser((long) student.getUserId());
        assertThat(totalBill).isEqualTo(new BigDecimal("100.00"));
    }

    @Test
    public void testDeleteByStudentUserIdAndCourseCourseId() {
        Cart cart = new Cart();
        cart.setStudent(student);
        cart.setCourse(course1);
        cart.setAddingDate(LocalDate.now());
        cart.setDescription("Test description");
        cart.setPrice(new BigDecimal("100.00"));
        cartRepository.save(cart);

        int deletedCount = cartRepository.deleteByStudentUserIdAndCourseCourseId(student.getUserId(), course1.getCourseId());
        assertThat(deletedCount).isEqualTo(1);
    }

    @Test
    public void testCountCartByStudentUserId() {
        Cart cart = new Cart();
        cart.setStudent(student);
        cart.setCourse(course1);
        cart.setAddingDate(LocalDate.now());
        cart.setDescription("Test description");
        cart.setPrice(new BigDecimal("100.00"));
        cartRepository.save(cart);

        long count = cartRepository.countCartByStudentUserId((long) student.getUserId());
        assertThat(count).isEqualTo(1);
    }
}
