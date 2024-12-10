package com.group4.cursus.service;

import com.group4.cursus.dto.ReviewDTO;
import com.group4.cursus.entity.Course;
import com.group4.cursus.entity.Enrollment;
import com.group4.cursus.entity.Instructor;
import com.group4.cursus.entity.Review;
import com.group4.cursus.entity.Student;
import com.group4.cursus.repository.CourseRepository;
import com.group4.cursus.repository.EnrollmentRepository;
import com.group4.cursus.repository.ReviewRepository;
import com.group4.cursus.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReviewServiceTest {

    @InjectMocks
    private ReviewService reviewService;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private StudentRepository studentRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetReviewsForCourse_Success() throws Exception {
        Long courseId = 1L;
        String instructorEmail = "instructor@example.com";

        Course course = new Course();
        course.setCourseId(courseId);
        Instructor instructor = new Instructor();
        instructor.setEmail(instructorEmail);
        course.setInstructor(instructor);

        Student student1 = new Student();
        student1.setUserId(1);
        student1.setFullName("Student 1");

        Student student2 = new Student();
        student2.setUserId(2);
        student2.setFullName("Student 2");

        Review review1 = new Review();
        review1.setReviewId(1L);
        review1.setRating(5);
        review1.setContents("Great course!");
        review1.setCourse(course);
        review1.setStudent(student1);

        Review review2 = new Review();
        review2.setReviewId(2L);
        review2.setRating(4);
        review2.setContents("Good course!");
        review2.setCourse(course);
        review2.setStudent(student2);

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(reviewRepository.findByCourse_CourseId(courseId)).thenReturn(Arrays.asList(review1, review2));

        List<ReviewDTO> reviews = reviewService.getReviewsForCourse(courseId, instructorEmail);

        assertEquals(2, reviews.size());
        assertEquals(1L, reviews.get(0).getReviewId());
        assertEquals(5, reviews.get(0).getRating());
        assertEquals("Great course!", reviews.get(0).getContents());
        assertEquals(courseId, reviews.get(0).getCourseId());
        assertEquals(1L, reviews.get(0).getStudentId());
        assertEquals("Student 1", reviews.get(0).getStudentName());

        assertEquals(2L, reviews.get(1).getReviewId());
        assertEquals(4, reviews.get(1).getRating());
        assertEquals("Good course!", reviews.get(1).getContents());
        assertEquals(courseId, reviews.get(1).getCourseId());
        assertEquals(2L, reviews.get(1).getStudentId());
        assertEquals("Student 2", reviews.get(1).getStudentName());
    }

    @Test
    void testGetReviewsForCourse_CourseNotFound() {
        Long courseId = 1L;
        String instructorEmail = "instructor@example.com";

        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () -> {
            reviewService.getReviewsForCourse(courseId, instructorEmail);
        });

        assertEquals("Course not found", exception.getMessage());
    }

    @Test
    void testGetReviewsForCourse_Unauthorized() {
        Long courseId = 1L;
        String instructorEmail = "instructor@example.com";

        Course course = new Course();
        course.setCourseId(courseId);
        Instructor instructor = new Instructor();
        instructor.setEmail("other@example.com");
        course.setInstructor(instructor);

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

        Exception exception = assertThrows(Exception.class, () -> {
            reviewService.getReviewsForCourse(courseId, instructorEmail);
        });

        assertEquals("You are not authorized to view reviews for this course", exception.getMessage());
    }

    @Test
    void testAddReview_Success() throws Exception {
        Long courseId = 1L;
        Long studentId = 1L;
        ReviewDTO reviewDTO = new ReviewDTO();
        reviewDTO.setRating(5);
        reviewDTO.setContents("Great course!");

        Course course = new Course();
        course.setCourseId(courseId);

        Enrollment enrollment = new Enrollment();
        enrollment.setCourse(course);

        Student student = new Student();
        student.setUserId(studentId.intValue());

        when(enrollmentRepository.findByStudentUserIdAndCourse_courseId(studentId, courseId)).thenReturn(enrollment);
        when(studentRepository.findById(studentId.intValue())).thenReturn(Optional.of(student));

        Review review = new Review();
        review.setReviewId(1L);
        review.setRating(reviewDTO.getRating());
        review.setContents(reviewDTO.getContents());
        review.setCourse(course);
        review.setStudent(student);

        when(reviewRepository.save(any(Review.class))).thenReturn(review);

        ReviewDTO createdReview = reviewService.addReview(courseId, studentId, reviewDTO);

        assertNotNull(createdReview);
        assertEquals(1L, createdReview.getReviewId());
        assertEquals(5, createdReview.getRating());
        assertEquals("Great course!", createdReview.getContents());
        assertEquals(courseId, createdReview.getCourseId());

        ArgumentCaptor<Review> reviewCaptor = ArgumentCaptor.forClass(Review.class);
        verify(reviewRepository, times(1)).save(reviewCaptor.capture());
        Review capturedReview = reviewCaptor.getValue();
        assertEquals(5, capturedReview.getRating());
        assertEquals("Great course!", capturedReview.getContents());
        assertEquals(course, capturedReview.getCourse());
        assertEquals(student, capturedReview.getStudent());
    }

    @Test
    void testAddReview_StudentNotEnrolled() {
        Long courseId = 1L;
        Long studentId = 1L;
        ReviewDTO reviewDTO = new ReviewDTO();
        reviewDTO.setRating(5);
        reviewDTO.setContents("Great course!");

        when(enrollmentRepository.findByStudentUserIdAndCourse_courseId(studentId, courseId)).thenReturn(null);

        Exception exception = assertThrows(Exception.class, () -> {
            reviewService.addReview(courseId, studentId, reviewDTO);
        });

        assertEquals("You are not enrolled in this course", exception.getMessage());
    }

    @Test
    void testAddReview_StudentNotFound() {
        Long courseId = 1L;
        Long studentId = 1L;
        ReviewDTO reviewDTO = new ReviewDTO();
        reviewDTO.setRating(5);
        reviewDTO.setContents("Great course!");

        Course course = new Course();
        course.setCourseId(courseId);

        Enrollment enrollment = new Enrollment();
        enrollment.setCourse(course);

        when(enrollmentRepository.findByStudentUserIdAndCourse_courseId(studentId, courseId)).thenReturn(enrollment);
        when(studentRepository.findById(studentId.intValue())).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () -> {
            reviewService.addReview(courseId, studentId, reviewDTO);
        });

        assertEquals("Student not found", exception.getMessage());
    }
}
