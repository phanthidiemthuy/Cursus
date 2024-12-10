package com.group4.cursus.service;


import com.group4.cursus.dto.CourseAnalyticsDTO;
import com.group4.cursus.dto.CourseDTO;
import com.group4.cursus.dto.InstructorDTO;
import com.group4.cursus.entity.Course;
import com.group4.cursus.entity.Enrollment;
import com.group4.cursus.entity.Review;
import com.group4.cursus.repository.CourseRepository;
import com.group4.cursus.repository.EnrollmentRepository;
import com.group4.cursus.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;
    @Autowired
    private ReviewRepository reviewRepository;

    public CourseDTO getCourseById(Long courseId) throws Exception {
        Optional<Course> courseOptional = courseRepository.findById(courseId);
        if (courseOptional.isEmpty()) {
            throw new Exception("Course not found");
        }
        Course course = courseOptional.get();
        return convertToDTO(course);
    }

    public Page<CourseDTO> getCoursesByCategory(Long categoryId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("courseTitle").descending());
        Page<Course> coursesPage = courseRepository.findByCategoryId(categoryId, pageable);
        return coursesPage.map(this::convertToDTO);
    }

    public Page<CourseDTO> getCoursesBySubcategory(Long subcategoryId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("courseTitle").descending());
        Page<Course> coursesPage = courseRepository.findBySubcategoryId(subcategoryId, pageable);
        return coursesPage.map(this::convertToDTO);
    }

    public Page<CourseDTO> searchCourses(String keyword, int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());
        Page<Course> coursesPage = courseRepository.searchByKeyword(keyword, pageable);
        return coursesPage.map(this::convertToDTO);
    }

    private CourseDTO convertToDTO(Course course) {
        CourseDTO courseDTO = new CourseDTO();
        courseDTO.setCourseId(course.getCourseId());
        courseDTO.setCourseTitle(course.getCourseTitle());
        courseDTO.setDescription(course.getDescription());
        courseDTO.setRequirements(course.getRequirements());
        courseDTO.setCourseLevel(course.getCourseLevel());
        courseDTO.setRegularPrice(course.getRegularPrice());
        courseDTO.setSubcategoryId(course.getSubCategory().getSubcategoryId());
        courseDTO.setStatus(course.getStatus());
        courseDTO.setAverageRating(0.0);

        InstructorDTO instructorDTO = new InstructorDTO();
        instructorDTO.setUserId((long) course.getInstructor().getUserId());
        instructorDTO.setFullName(course.getInstructor().getFullName());
        instructorDTO.setEmail(course.getInstructor().getEmail());
        instructorDTO.setAddress(course.getInstructor().getAddress());
        courseDTO.setInstructor(instructorDTO);
        return courseDTO;
    }

    public CourseAnalyticsDTO getCourseAnalytics(Long courseId, String instructorEmail) throws Exception {
        Optional<Course> courseOptional = courseRepository.findById(courseId);
        if (courseOptional.isEmpty()) {
            throw new Exception("Course not found");
        }
        Course course = courseOptional.get();

        if (!course.getInstructor().getEmail().equals(instructorEmail)) {
            throw new Exception("You are not authorized to view analytics for this course");
        }

        List<Review> reviews = reviewRepository.findByCourse_CourseId(courseId);
        double averageRating = reviews.stream()
                .collect(Collectors.averagingDouble(Review::getRating));

        List<Enrollment> enrollments = enrollmentRepository.findByCourse_CourseId(courseId);
        long totalStudentsEnrolled = enrollments.size();
        double averageProgress = enrollments.stream()
                .collect(Collectors.averagingDouble(Enrollment::getProgress));

        return convertToAnalyticsDTO(course, averageRating, totalStudentsEnrolled, averageProgress);
    }
    private CourseAnalyticsDTO convertToAnalyticsDTO(Course course, double averageRating, long totalStudentsEnrolled, double averageProgress) {
        CourseAnalyticsDTO courseAnalyticsDTO = new CourseAnalyticsDTO();
        courseAnalyticsDTO.setCourseId(course.getCourseId());
        courseAnalyticsDTO.setCourseTitle(course.getCourseTitle());
        courseAnalyticsDTO.setDescription(course.getDescription());
        courseAnalyticsDTO.setRequirements(course.getRequirements());
        courseAnalyticsDTO.setCourseLevel(course.getCourseLevel());
        courseAnalyticsDTO.setRegularPrice(course.getRegularPrice());
        courseAnalyticsDTO.setSubcategoryId(course.getSubCategory().getSubcategoryId());
        courseAnalyticsDTO.setStatus(course.getStatus());
        courseAnalyticsDTO.setAverageRating(averageRating);
        courseAnalyticsDTO.setTotalStudentsEnrolled(totalStudentsEnrolled);
        courseAnalyticsDTO.setAverageProgress(averageProgress);

        InstructorDTO instructorDTO = new InstructorDTO();
        instructorDTO.setFullName(course.getInstructor().getFullName());
        instructorDTO.setEmail(course.getInstructor().getEmail());
        courseAnalyticsDTO.setInstructor(instructorDTO);

        return courseAnalyticsDTO;
    }
}
