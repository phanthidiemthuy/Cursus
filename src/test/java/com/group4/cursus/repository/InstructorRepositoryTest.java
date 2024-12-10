package com.group4.cursus.repository;

import com.group4.cursus.entity.Instructor;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ExtendWith(SpringExtension.class)
@DataJpaTest
public class InstructorRepositoryTest {
    @Autowired
    private InstructorRepository instructorRepository;

    private Instructor instructor;

    @BeforeEach
    public void setUp() {
        // Xóa tất cả các dữ liệu trước khi chạy mỗi bài kiểm tra
        instructorRepository.deleteAll();

        // Tạo và lưu một Instructor
        instructor = new Instructor();
        instructor.setFullName("John Doe");
        instructor.setEmail("john.doe@example.com");
        instructor.setPassword("password123");
        instructor.setRegistrationDate(LocalDate.now());
        instructor.setAddress("123 Main St");
        instructor.setBlocked(false);
        instructor.setApproved(true);
        instructor.setUserType("INSTRUCTOR");
        instructor.setExperience("5 years");
        instructor.setSalary(new BigDecimal("5000.00"));
        instructorRepository.save(instructor);
    }

    @Test
    public void testFindByEmail() {
        Optional<Instructor> foundInstructor = instructorRepository.findByEmail("john.doe@example.com");

        assertThat(foundInstructor).isPresent();
        assertThat(foundInstructor.get().getEmail()).isEqualTo("john.doe@example.com");
    }

    @Test
    public void testFindInstructorByFullName() {
        List<Instructor> foundInstructors = instructorRepository.findInstructorByFullName("John Doe");

        assertThat(foundInstructors).hasSize(1);
        assertThat(foundInstructors.get(0).getFullName()).isEqualTo("John Doe");

        // Tìm kiếm với một tên không tồn tại
        List<Instructor> emptyList = instructorRepository.findInstructorByFullName("Jane Smith");
        assertThat(emptyList).isEmpty();
    }

    @Test
    public void testFindInstructorByPartialFullName() {
        List<Instructor> foundInstructors = instructorRepository.findInstructorByFullName("John");

        assertThat(foundInstructors).hasSize(1);
        assertThat(foundInstructors.get(0).getFullName()).isEqualTo("John Doe");
    }
}