package com.group4.cursus.repository;


import com.group4.cursus.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByCourse_CourseId(Long courseId);

    List<Review> findByCourseCourseIdIn(List<Long> courseIds);
}
