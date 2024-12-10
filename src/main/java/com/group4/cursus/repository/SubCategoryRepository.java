package com.group4.cursus.repository;

import com.group4.cursus.entity.SubCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubCategoryRepository extends JpaRepository<SubCategory, Long> {
    @Query("SELECT s FROM SubCategory s JOIN FETCH s.courses WHERE s.subcategoryId = :id")
    Optional<SubCategory> findByIdWithCourses(@Param("id") Long id);// Change to Long

    @Query("SELECT c FROM SubCategory c WHERE lower(c.subcategoryName) LIKE lower(concat('%', :subcategoryName, '%'))")
    List<SubCategory> findBySubCateNameContainingIgnoreCase(String subcategoryName);
}
