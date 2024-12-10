package com.group4.cursus.service;

import com.group4.cursus.dto.*;
import com.group4.cursus.entity.*;
import com.group4.cursus.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class InstructorService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private InstructorRepository instructorRepository;

    @Autowired
    private SubCategoryRepository subCategoryRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private ReviewRepository reviewRepository;


    public Course createCourse(CreateCourseRequest createCourseRequest, String instructorEmail) throws Exception {
        SubCategory subCategory = subCategoryRepository.findById(createCourseRequest.getSubCategoryId())
                .orElseThrow(() -> new Exception("SubCategory not found"));

        Instructor instructor = instructorRepository.findByEmail(instructorEmail)
                .orElseThrow(() -> new Exception("Instructor not found"));

        Course course = new Course();
        course.setCourseTitle(createCourseRequest.getCourseTitle());
        course.setDescription(createCourseRequest.getDescription());
        course.setRequirements(createCourseRequest.getRequirements());
        course.setCourseLevel(createCourseRequest.getCourseLevel());
        course.setThumbnail(createCourseRequest.getThumbnail());
        course.setRegularPrice(createCourseRequest.getRegularPrice());
        course.setStatus("PENDING"); // Initial status
        course.setIsBlocked(0);
        course.setSubCategory(subCategory);
        course.setInstructor(instructor);

        return courseRepository.save(course);
    }

    public Course updateCourse(Long courseId, EditCourseRequest updateCourseRequest, String instructorEmail) throws Exception {
        Optional<Course> courseOptional = courseRepository.findById(courseId);
        if (courseOptional.isEmpty()) {
            throw new Exception("Course not found");
        }
        Course course = courseOptional.get();

        // Verify if the logged-in instructor is the one who owns the course
        if (!course.getInstructor().getEmail().equals(instructorEmail)) {
            throw new Exception("You are not authorized to edit this course");
        }

        SubCategory subCategory = subCategoryRepository.findById(updateCourseRequest.getSubCategoryId())
                .orElseThrow(() -> new Exception("SubCategory not found"));

        course.setCourseTitle(updateCourseRequest.getCourseTitle());
        course.setDescription(updateCourseRequest.getDescription());
        course.setRequirements(updateCourseRequest.getRequirements());
        course.setCourseLevel(updateCourseRequest.getCourseLevel());
        course.setThumbnail(updateCourseRequest.getThumbnail());
        course.setRegularPrice(updateCourseRequest.getRegularPrice());
        course.setSubCategory(subCategory);

        return courseRepository.save(course);
    }

    public Course resubmitCourse(Long courseId, ReSubmitCourseRequest reSubmitCourseRequest, String instructorEmail) throws Exception {
        Optional<Course> courseOptional = courseRepository.findById(courseId);
        if (courseOptional.isEmpty()) {
            throw new Exception("Course not found");
        }
        Course course = courseOptional.get();

        // Verify if the logged-in instructor is the one who owns the course
        if (!course.getInstructor().getEmail().equals(instructorEmail)) {
            throw new Exception("You are not authorized to edit this course");
        }

        SubCategory subCategory = subCategoryRepository.findById(reSubmitCourseRequest.getSubCategoryId())
                .orElseThrow(() -> new Exception("SubCategory not found"));

        course.setCourseTitle(reSubmitCourseRequest.getCourseTitle());
        course.setDescription(reSubmitCourseRequest.getDescription());
        course.setRequirements(reSubmitCourseRequest.getRequirements());
        course.setCourseLevel(reSubmitCourseRequest.getCourseLevel());
        course.setThumbnail(reSubmitCourseRequest.getThumbnail());
        course.setRegularPrice(reSubmitCourseRequest.getRegularPrice());
        course.setSubCategory(subCategory);
        course.setStatus("RESUBMITTED"); // Update status to resubmitted

        return courseRepository.save(course);
    }

    public void deleteCourse(Long courseId, String instructorEmail) throws Exception {
        Optional<Course> courseOptional = courseRepository.findById(courseId);
        if (courseOptional.isEmpty()) {
            throw new Exception("Course not found");
        }
        Course course = courseOptional.get();

        // Verify if the logged-in instructor is the one who owns the course
        if (!course.getInstructor().getEmail().equals(instructorEmail)) {
            throw new Exception("You are not authorized to delete this course");
        }

        // Check if the course has student enrollments
        if (enrollmentRepository.existsByCourse(course)) {
            throw new Exception("Cannot delete the course that has student enrollments");
        }

        courseRepository.delete(course);
    }

    public CourseDTO getCourseById(Long courseId) throws Exception {
        Optional<Course> courseOptional = courseRepository.findById(courseId);
        if (courseOptional.isEmpty()) {
            throw new Exception("Course not found");
        }
        Course course = courseOptional.get();

        List<Review> reviews = reviewRepository.findByCourse_CourseId(courseId);
        double averageRating = reviews.stream()
                .collect(Collectors.averagingDouble(Review::getRating));

        CourseDTO courseDTO = convertToDTO(course, averageRating);
        return courseDTO;
    }

    private CourseDTO convertToDTO(Course course, double averageRating) {
        CourseDTO courseDTO = new CourseDTO();
        courseDTO.setCourseId(course.getCourseId());
        courseDTO.setCourseTitle(course.getCourseTitle());
        courseDTO.setDescription(course.getDescription());
        courseDTO.setRequirements(course.getRequirements());
        courseDTO.setCourseLevel(course.getCourseLevel());
        courseDTO.setRegularPrice(course.getRegularPrice());
        courseDTO.setSubcategoryId(course.getSubCategory().getSubcategoryId());
        courseDTO.setStatus(course.getStatus());
        courseDTO.setAverageRating(averageRating);

        InstructorDTO instructorDTO = new InstructorDTO();
        instructorDTO.setFullName(course.getInstructor().getFullName());
        instructorDTO.setEmail(course.getInstructor().getEmail());
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

    public List<Course> findAllPendingCourses() {
        return courseRepository.findAllByStatus("PENDING");
    }

    public void approveCourse(Long courseId) {
        Course course = courseRepository.findById(courseId).
                orElseThrow(() -> new RuntimeException("Course not found"));
        course.setStatus("APPROVED");
        courseRepository.save(course);
    }

    public void rejectCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        course.setStatus("REJECTED");
        courseRepository.save(course);
    }

    public void blockCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        course.setIsBlocked(1);
        courseRepository.save(course);
    }

    public void unblockCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        course.setIsBlocked(0);
        courseRepository.save(course);
    }
}

