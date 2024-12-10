package com.group4.cursus.repository;


import com.group4.cursus.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("SELECT c FROM Category c WHERE lower(c.categoryName) LIKE lower(concat('%', :categoryName, '%'))")
    List<Category> findByCategoryNameContainingIgnoreCase(String categoryName);

    boolean existsByCategoryName(String uniqueCategory);
}
