package com.group4.cursus.service;

import com.group4.cursus.dto.CourseEarningDTO;
import  com.group4.cursus.dto.EarningsDTO;
import  com.group4.cursus.entity.Course;
import  com.group4.cursus.entity.OrderItem;
import  com.group4.cursus.repository.CourseRepository;
import  com.group4.cursus.repository.OrderItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EarningsService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    public List<EarningsDTO> getEarningsByInstructor(String instructorEmail) throws Exception {
        List<Course> courses = courseRepository.findByInstructor_Email(instructorEmail);
        if (courses.isEmpty()) {
            throw new Exception("No courses found for the instructor");
        }

        List<Long> courseIds = courses.stream().map(Course::getCourseId).collect(Collectors.toList());
        List<OrderItem> orderItems = orderItemRepository.findByCourseCourseIdIn(courseIds);

        Map<YearMonth, List<OrderItem>> groupedByMonth = orderItems.stream()
                .collect(Collectors.groupingBy(orderItem -> YearMonth.from(orderItem.getOrder().getOrderDate())));

        return groupedByMonth.entrySet().stream().map(entry -> {
            YearMonth month = entry.getKey();
            List<OrderItem> itemsInMonth = entry.getValue();

            BigDecimal totalEarnings = itemsInMonth.stream()
                    .map(OrderItem::getUnitPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            List<CourseEarningDTO> courseEarnings = itemsInMonth.stream()
                    .collect(Collectors.groupingBy(OrderItem::getCourse))
                    .entrySet().stream()
                    .map(courseEntry -> {
                        Course course = courseEntry.getKey();
                        List<OrderItem> courseOrderItems = courseEntry.getValue();

                        int unitsSold = courseOrderItems.size();
                        BigDecimal earnings = courseOrderItems.stream()
                                .map(OrderItem::getUnitPrice)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                        CourseEarningDTO courseEarningDTO = new CourseEarningDTO();
                        courseEarningDTO.setCourseId(course.getCourseId());
                        courseEarningDTO.setCourseTitle(course.getCourseTitle());
                        courseEarningDTO.setUnitsSold(unitsSold);
                        courseEarningDTO.setEarnings(earnings);

                        return courseEarningDTO;
                    }).collect(Collectors.toList());

            EarningsDTO earningDTO = new EarningsDTO();
            earningDTO.setMonth(month.format(DateTimeFormatter.ofPattern("MMMM yyyy")));
            earningDTO.setTotalEarnings(totalEarnings);
            earningDTO.setCourseEarnings(courseEarnings);

            return earningDTO;
        }).collect(Collectors.toList());
    }
}