package com.group4.cursus.repository;

import com.group4.cursus.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ExtendWith(SpringExtension.class)
@DataJpaTest
public class OrderItemRepositoryTest {

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private OrderRepository orderRepository;

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
    private Order order;
    private OrderItem orderItem1;
    private OrderItem orderItem2;

    @BeforeEach
    public void setUp() {
        // Clear the database before each test
        courseRepository.deleteAll();
        subCategoryRepository.deleteAll();
        instructorRepository.deleteAll();
        categoryRepository.deleteAll();
        studentRepository.deleteAll();
        orderRepository.deleteAll();
        orderItemRepository.deleteAll();

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

        // Create and save an Order
        order = new Order();
        order.setStudent(student);
        order.setPaymentMethod("Banking");
        order.setOrderDate(LocalDate.now());
        order.setTotalPaid(BigDecimal.valueOf(300));
        orderRepository.save(order);

        // Create and save OrderItems
        orderItem1 = new OrderItem();
        orderItem1.setOrder(order);
        orderItem1.setCourse(course1);
        orderItem1.setAddingDate(LocalDate.now()); // Ensure addingDate is not null
        orderItem1.setUnitPrice(new BigDecimal("100.00")); // Set unitPrice instead of price
        orderItemRepository.save(orderItem1);

        orderItem2 = new OrderItem();
        orderItem2.setOrder(order);
        orderItem2.setCourse(course2);
        orderItem2.setAddingDate(LocalDate.now()); // Ensure addingDate is not null
        orderItem2.setUnitPrice(new BigDecimal("200.00")); // Set unitPrice instead of price
        orderItemRepository.save(orderItem2);
    }

    @Test
    public void testFindByCourse() {
        Collection<Object> orderItems = orderItemRepository.findByCourse(course1);
        assertThat(orderItems).hasSize(1);

        OrderItem orderItem = (OrderItem) orderItems.iterator().next();
        assertThat(orderItem.getCourse()).isEqualTo(course1);
    }

    @Test
    public void testFindByCourseCourseIdIn() {
        List<Long> courseIds = Arrays.asList(course1.getCourseId(), course2.getCourseId());
        List<OrderItem> orderItems = orderItemRepository.findByCourseCourseIdIn(courseIds);

        assertThat(orderItems).hasSize(2);
        assertThat(orderItems).extracting("course").containsExactlyInAnyOrder(course1, course2);
    }
}