package com.group4.cursus.service;

import com.group4.cursus.dto.IntroductorDashboardDTO;
import com.group4.cursus.entity.Course;
import com.group4.cursus.entity.OrderItem;
import com.group4.cursus.entity.Review;
import com.group4.cursus.entity.Student;
import com.group4.cursus.repository.CourseRepository;
import com.group4.cursus.repository.OrderItemRepository;
import com.group4.cursus.repository.ReviewRepository;
import com.group4.cursus.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    public IntroductorDashboardDTO getDashboardData(String instructorEmail) throws Exception {
        List<Course> courses = courseRepository.findByInstructor_Email(instructorEmail);
        if (courses.isEmpty()) {
            throw new Exception("No courses found for the instructor");
        }

        List<Long> courseIds = courses.stream().map(Course::getCourseId).collect(Collectors.toList());
        List<OrderItem> orderItems = orderItemRepository.findByCourseCourseIdIn(courseIds);
        List<Student> students = studentRepository.findAll();
        List<Review> reviews = reviewRepository.findByCourseCourseIdIn(courseIds);

        BigDecimal totalSales = orderItems.stream()
                .map(OrderItem::getUnitPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalEnrollments = orderItems.size();
        int totalCourses = courses.size();
        int totalStudents = students.size();
        int totalReviews = reviews.size();
        double averageRating = reviews.stream().mapToInt(Review::getRating).average().orElse(0.0);
        BigDecimal averageCoursePrice = courses.stream()
                .map(Course::getRegularPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(totalCourses), BigDecimal.ROUND_HALF_UP);
        int newStudentsThisMonth = (int) students.stream()
                .filter(student -> YearMonth.from(student.getRegistrationDate()).equals(YearMonth.now()))
                .count(); // Assuming Student has an enrollmentDate field



        IntroductorDashboardDTO dashboardDTO = new IntroductorDashboardDTO();
        dashboardDTO.setTotalSales(totalSales);
        dashboardDTO.setTotalEnrollments(totalEnrollments);
        dashboardDTO.setTotalCourses(totalCourses);
        dashboardDTO.setTotalStudents(totalStudents);
        dashboardDTO.setTotalReviews(totalReviews);
        dashboardDTO.setAverageRating(averageRating);
        dashboardDTO.setAverageCoursePrice(averageCoursePrice);
        dashboardDTO.setNewStudentsThisMonth(newStudentsThisMonth);


        return dashboardDTO;
    }
}
