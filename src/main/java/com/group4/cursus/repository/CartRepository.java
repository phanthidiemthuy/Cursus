package com.group4.cursus.repository;


import com.group4.cursus.entity.Cart;
import com.group4.cursus.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;

public interface CartRepository extends JpaRepository<Cart, Long> {

    List<Cart> findByStudent(Student student);

    @Query("SELECT COUNT(c) FROM Cart c WHERE c.student.userId = :studentId AND c.course.courseId = :courseId")
    int checkIfCourseInCart(Long studentId, Long courseId);

    Page<Cart> findByStudentUserId(Long userId, Pageable pageable);

    @Query("SELECT SUM(c.price) FROM Cart c WHERE c.student.userId = :userId")
    BigDecimal getTotalBillForUser(Long userId);

    int deleteByStudentUserIdAndCourseCourseId(int userId, Long courseId);

    @Query("SELECT COUNT(c) FROM Cart c WHERE c.student.userId = :userId")
    long countCartByStudentUserId(Long userId);
}