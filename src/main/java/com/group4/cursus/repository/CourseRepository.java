package com.group4.cursus.repository;

import com.group4.cursus.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findAllByStatus(String status);
    List<Course> findByInstructor_Email(String instructorEmail);
    @Query("SELECT c FROM Course c JOIN Cart ca ON ca.course.courseId = c.courseId WHERE ca.student.userId = :studentId")
    Page<Course> getCartListByUser(Long studentId, Pageable pageable);

    @Query("SELECT c FROM Course c WHERE c.subCategory.category.categoryId = :categoryId AND c.status = 'APPROVED' AND c.isBlocked = 0")
    Page<Course> findByCategoryId(Long categoryId, Pageable pageable);

    @Query("SELECT c FROM Course c WHERE c.subCategory.subcategoryId = :subcategoryId AND c.status = 'APPROVED' AND c.isBlocked = 0")
    Page<Course> findBySubcategoryId(Long subcategoryId, Pageable pageable);

    @Query("SELECT c FROM Course c WHERE c.courseTitle LIKE %:keyword% AND c.status = 'APPROVED' AND c.isBlocked = 0")
    Page<Course> searchByKeyword(String keyword, Pageable pageable);
}
