package com.group4.cursus.repository;


import com.group4.cursus.entity.Course;
import com.group4.cursus.entity.Enrollment;
import com.group4.cursus.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    boolean existsByCourse(Course course);
    List<Enrollment> findByCourse_CourseId(Long courseId);
    boolean existsByStudentUserIdAndCourseCourseId(Long studentId, Long courseId);
    Enrollment findByStudentUserIdAndCourse_courseId(Long studentId, Long courseId);

    @Query("SELECT c FROM Enrollment c WHERE c.student.userId = :studentId")
    List<Enrollment> findCourseByStudentId(@Param("studentId") Long studentId);
    //Slice<Enrollment> findByStudent_studentId(Long studentId, Pageable pageable);

    //Slice<Enrollment> findByOrder_orderId(Long orderId, Pageable pageable);
}
