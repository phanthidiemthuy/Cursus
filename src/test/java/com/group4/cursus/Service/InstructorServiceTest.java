package com.group4.cursus.service;

import com.group4.cursus.dto.*;
import com.group4.cursus.entity.*;
import com.group4.cursus.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InstructorServiceTest {

    @InjectMocks
    private InstructorService instructorService;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private InstructorRepository instructorRepository;

    @Mock
    private SubCategoryRepository subCategoryRepository;

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateCourse_Success() throws Exception {
        CreateCourseRequest request = new CreateCourseRequest();
        request.setCourseTitle("Test Course");
        request.setDescription("Test Description");
        request.setRequirements("Test Requirements");
        request.setCourseLevel("Beginner");
        request.setThumbnail("test.jpg");
        request.setRegularPrice(new BigDecimal("100.00"));
        request.setSubCategoryId(1L);

        SubCategory subCategory = new SubCategory();
        Instructor instructor = new Instructor();
        instructor.setEmail("instructor@example.com");

        when(subCategoryRepository.findById(1L)).thenReturn(Optional.of(subCategory));
        when(instructorRepository.findByEmail("instructor@example.com")).thenReturn(Optional.of(instructor));
        when(courseRepository.save(any(Course.class))).thenReturn(new Course());

        Course result = instructorService.createCourse(request, "instructor@example.com");

        assertNotNull(result);
        verify(courseRepository, times(1)).save(any(Course.class));
    }

    @Test
    void testUpdateCourse_Success() throws Exception {
        EditCourseRequest request = new EditCourseRequest();
        request.setCourseTitle("Updated Course");
        request.setDescription("Updated Description");
        request.setRequirements("Updated Requirements");
        request.setCourseLevel("Intermediate");
        request.setThumbnail("updated.jpg");
        request.setRegularPrice(new BigDecimal("150.00"));
        request.setSubCategoryId(1L);

        Course course = new Course();
        Instructor instructor = new Instructor();
        instructor.setEmail("instructor@example.com");
        course.setInstructor(instructor);

        SubCategory subCategory = new SubCategory();

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(subCategoryRepository.findById(1L)).thenReturn(Optional.of(subCategory));
        when(courseRepository.save(any(Course.class))).thenReturn(course);

        Course result = instructorService.updateCourse(1L, request, "instructor@example.com");

        assertNotNull(result);
        verify(courseRepository, times(1)).save(course);
    }

    @Test
    void testDeleteCourse_Success() throws Exception {
        Course course = new Course();
        Instructor instructor = new Instructor();
        instructor.setEmail("instructor@example.com");
        course.setInstructor(instructor);

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(enrollmentRepository.existsByCourse(course)).thenReturn(false);

        instructorService.deleteCourse(1L, "instructor@example.com");

        verify(courseRepository, times(1)).delete(course);
    }

    @Test
    void testGetCourseById_Success() throws Exception {
        Course course = new Course();
        course.setCourseId(1L);
        Instructor instructor = new Instructor();
        instructor.setEmail("instructor@example.com");
        SubCategory subCategory = new SubCategory();
        subCategory.setSubcategoryId(1L);
        course.setInstructor(instructor);
        course.setSubCategory(subCategory);

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(reviewRepository.findByCourse_CourseId(1L)).thenReturn(Arrays.asList(createReview(4), createReview(5)));

        CourseDTO result = instructorService.getCourseById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getCourseId());
        assertEquals(4.5, result.getAverageRating());
    }

    @Test
    void testGetCourseAnalytics_Success() throws Exception {
        Course course = new Course();
        course.setCourseId(1L);
        Instructor instructor = new Instructor();
        instructor.setEmail("instructor@example.com");
        SubCategory subCategory = new SubCategory();
        subCategory.setSubcategoryId(1L);
        course.setInstructor(instructor);
        course.setSubCategory(subCategory);

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(reviewRepository.findByCourse_CourseId(1L)).thenReturn(Arrays.asList(createReview(4), createReview(5)));
        when(enrollmentRepository.findByCourse_CourseId(1L)).thenReturn(Arrays.asList(createEnrollment(80.0), createEnrollment(90.0)));

        CourseAnalyticsDTO result = instructorService.getCourseAnalytics(1L, "instructor@example.com");

        assertNotNull(result);
        assertEquals(4.5, result.getAverageRating());
        assertEquals(2, result.getTotalStudentsEnrolled());
        assertEquals(85.0, result.getAverageProgress());
    }

    private Review createReview(int rating) {
        Review review = new Review();
        review.setRating(rating);
        return review;
    }

    private Enrollment createEnrollment(double progress) {
        Enrollment enrollment = new Enrollment();
        enrollment.setProgress((int) progress);
        return enrollment;
    }

    @Test
    void testFindAllPendingCourses_Success() {
        Course course1 = new Course();
        course1.setStatus("PENDING");
        Course course2 = new Course();
        course2.setStatus("PENDING");

        when(courseRepository.findAllByStatus("PENDING")).thenReturn(Arrays.asList(course1, course2));

        List<Course> result = instructorService.findAllPendingCourses();

        assertEquals(2, result.size());
    }

    @Test
    void testApproveCourse_Success() {
        Course course = new Course();
        course.setStatus("PENDING");

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        instructorService.approveCourse(1L);

        assertEquals("APPROVED", course.getStatus());
        verify(courseRepository, times(1)).save(course);
    }

    @Test
    void testRejectCourse_Success() {
        Course course = new Course();
        course.setStatus("PENDING");

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        instructorService.rejectCourse(1L);

        assertEquals("REJECTED", course.getStatus());
        verify(courseRepository, times(1)).save(course);
    }

    @Test
    void testBlockCourse_Success() {
        Course course = new Course();
        course.setIsBlocked(0);

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        instructorService.blockCourse(1L);

        assertEquals(1, course.getIsBlocked());
        verify(courseRepository, times(1)).save(course);
    }

    @Test
    void testUnblockCourse_Success() {
        Course course = new Course();
        course.setIsBlocked(1);

        when(courseRepository.findById(1L)). thenReturn(Optional.of(course));

        instructorService.unblockCourse(1L);

        assertEquals(0, course.getIsBlocked());
        verify(courseRepository, times(1)).save(course);
    }
    @Test
    void testCreateCourse_SubCategoryNotFound() {
        CreateCourseRequest request = new CreateCourseRequest();
        request.setCourseTitle("Test Course");
        request.setDescription("Test Description");
        request.setRequirements("Test Requirements");
        request.setCourseLevel("Beginner");
        request.setThumbnail("test.jpg");
        request.setRegularPrice(new BigDecimal("100.00"));
        request.setSubCategoryId(1L);

        when(subCategoryRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () -> {
            instructorService.createCourse(request, "instructor@example.com");
        });

        assertEquals("SubCategory not found", exception.getMessage());
    }

    @Test
    void testCreateCourse_InstructorNotFound() {
        CreateCourseRequest request = new CreateCourseRequest();
        request.setCourseTitle("Test Course");
        request.setDescription("Test Description");
        request.setRequirements("Test Requirements");
        request.setCourseLevel("Beginner");
        request.setThumbnail("test.jpg");
        request.setRegularPrice(new BigDecimal("100.00"));
        request.setSubCategoryId(1L);

        SubCategory subCategory = new SubCategory();

        when(subCategoryRepository.findById(1L)).thenReturn(Optional.of(subCategory));
        when(instructorRepository.findByEmail("instructor@example.com")).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () -> {
            instructorService.createCourse(request, "instructor@example.com");
        });

        assertEquals("Instructor not found", exception.getMessage());
    }

    @Test
    void testUpdateCourse_CourseNotFound() {
        EditCourseRequest request = new EditCourseRequest();
        request.setCourseTitle("Updated Course");
        request.setDescription("Updated Description");
        request.setRequirements("Updated Requirements");
        request.setCourseLevel("Intermediate");
        request.setThumbnail("updated.jpg");
        request.setRegularPrice(new BigDecimal("150.00"));
        request.setSubCategoryId(1L);

        when(courseRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () -> {
            instructorService.updateCourse(1L, request, "instructor@example.com");
        });

        assertEquals("Course not found", exception.getMessage());
    }

    @Test
    void testUpdateCourse_SubCategoryNotFound() throws Exception {
        EditCourseRequest request = new EditCourseRequest();
        request.setCourseTitle("Updated Course");
        request.setDescription("Updated Description");
        request.setRequirements("Updated Requirements");
        request.setCourseLevel("Intermediate");
        request.setThumbnail("updated.jpg");
        request.setRegularPrice(new BigDecimal("150.00"));
        request.setSubCategoryId(1L);

        Course course = new Course();
        Instructor instructor = new Instructor();
        instructor.setEmail("instructor@example.com");
        course.setInstructor(instructor);

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(subCategoryRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () -> {
            instructorService.updateCourse(1L, request, "instructor@example.com");
        });

        assertEquals("SubCategory not found", exception.getMessage());
    }
    @Test
    void testDeleteCourse_CourseNotFound() {
        when(courseRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () -> {
            instructorService.deleteCourse(1L, "instructor@example.com");
        });

        assertEquals("Course not found", exception.getMessage());
    }

    @Test
    void testDeleteCourse_EnrollmentsExist() throws Exception {
        Course course = new Course();
        Instructor instructor = new Instructor();
        instructor.setEmail("instructor@example.com");
        course.setInstructor(instructor);

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(enrollmentRepository.existsByCourse(course)).thenReturn(true);

        Exception exception = assertThrows(Exception.class, () -> {
            instructorService.deleteCourse(1L, "instructor@example.com");
        });

        assertEquals("Cannot delete the course that has student enrollments", exception.getMessage());
    }

    @Test
    void testGetCourseById_CourseNotFound() {
        when(courseRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () -> {
            instructorService.getCourseById(1L);
        });

        assertEquals("Course not found", exception.getMessage());
    }

    @Test
    void testApproveCourse_CourseNotFound() {
        when(courseRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            instructorService.approveCourse(1L);
        });

        assertEquals("Course not found", exception.getMessage());
    }

    @Test
    void testRejectCourse_CourseNotFound() {
        when(courseRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            instructorService.rejectCourse(1L);
        });

        assertEquals("Course not found", exception.getMessage());
    }

    @Test
    void testBlockCourse_CourseNotFound() {
        when(courseRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            instructorService.blockCourse(1L);
        });

        assertEquals("Course not found", exception.getMessage());
    }

    @Test
    void testUnblockCourse_CourseNotFound() {
        when(courseRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            instructorService.unblockCourse(1L);
        });

        assertEquals("Course not found", exception.getMessage());
    }


}
