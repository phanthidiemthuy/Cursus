package com.group4.cursus.repository;


import com.group4.cursus.entity.Instructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InstructorRepository extends JpaRepository<Instructor, Integer> {
    Optional<Instructor> findByEmail(String email);

    @Query("select i from Instructor i where lower(i.fullName) LIKE lower(CONCAT('%',:fullName, '%'))")
    List<Instructor> findInstructorByFullName(@Param("fullName") String fullName);
}

