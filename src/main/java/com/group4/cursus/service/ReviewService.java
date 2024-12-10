package com.group4.cursus.service;

import com.group4.cursus.dto.ReviewDTO;
import com.group4.cursus.entity.Course;
import com.group4.cursus.entity.Enrollment;
import com.group4.cursus.entity.Review;
import com.group4.cursus.entity.Student;
import com.group4.cursus.repository.CourseRepository;
import com.group4.cursus.repository.EnrollmentRepository;
import com.group4.cursus.repository.ReviewRepository;
import com.group4.cursus.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private StudentRepository studentRepository;

    public List<ReviewDTO> getReviewsForCourse(Long courseId, String instructorEmail) throws Exception {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new Exception("Course not found"));

        if (!course.getInstructor().getEmail().equals(instructorEmail)) {
            throw new Exception("You are not authorized to view reviews for this course");
        }

        List<Review> reviews = reviewRepository.findByCourse_CourseId(courseId);
        return reviews.stream().map(this::convertToDTOViewReview).collect(Collectors.toList());
    }

    private ReviewDTO convertToDTOViewReview(Review review) {
        ReviewDTO reviewDTO = new ReviewDTO();
        reviewDTO.setReviewId(review.getReviewId());
        reviewDTO.setRating(review.getRating());
        reviewDTO.setContents(review.getContents());
        reviewDTO.setCourseId(review.getCourse().getCourseId());
        reviewDTO.setStudentId((long) review.getStudent().getUserId());
        reviewDTO.setStudentName(review.getStudent().getFullName());
        return reviewDTO;
    }

    public ReviewDTO addReview(Long courseId, Long studentId, ReviewDTO reviewDTO) throws Exception {
        Enrollment enrollment = enrollmentRepository.findByStudentUserIdAndCourse_courseId(studentId, courseId);
        if (enrollment == null) {
            throw new Exception("You are not enrolled in this course");
        }

        Student student = studentRepository.findById(Math.toIntExact(studentId))
                .orElseThrow(() -> new Exception("Student not found"));

        Review review = new Review();
        review.setRating(reviewDTO.getRating());
        review.setContents(reviewDTO.getContents());
        review.setCourse(enrollment.getCourse());
        review.setStudent(student);

        review = reviewRepository.save(review);
        return convertToDTO(review);
    }

    private ReviewDTO convertToDTO(Review review) {
        ReviewDTO reviewDTO = new ReviewDTO();
        reviewDTO.setReviewId(review.getReviewId());
        reviewDTO.setRating(review.getRating());
        reviewDTO.setContents(review.getContents());
        reviewDTO.setCourseId(review.getCourse().getCourseId());
        return reviewDTO;
    }
}