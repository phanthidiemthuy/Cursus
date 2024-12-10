package com.group4.cursus.repository;


import com.group4.cursus.entity.Course;
import com.group4.cursus.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    Collection<Object> findByCourse(Course course);

    List<OrderItem> findByCourseCourseIdIn(List<Long> courseIds);
}
