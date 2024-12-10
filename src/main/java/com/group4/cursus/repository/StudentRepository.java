package com.group4.cursus.repository;


import com.group4.cursus.entity.Course;
import com.group4.cursus.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Integer> {
    Optional<Student> findByEmail(String email);

    @Query("SELECT c FROM Course c WHERE c.subCategory.subcategoryId = :subcategoryId AND c.status = 'APPROVED' AND c.isBlocked = 0")
    List<Course> findCoursesBySubcategoryId(@Param("subcategoryId") Integer subcategoryId);

    @Query("select s from Student s where lower(s.fullName) LIKE lower(CONCAT('%',:fullName, '%'))")
    List<Student> findStudentByFullName(@Param("fullName")String fullName);

}
