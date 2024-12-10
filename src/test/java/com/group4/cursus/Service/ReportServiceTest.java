package com.group4.cursus.service;

import com.group4.cursus.dto.ReportDTO;
import com.group4.cursus.entity.Course;
import com.group4.cursus.entity.Enrollment;
import com.group4.cursus.entity.Report;
import com.group4.cursus.entity.Student;
import com.group4.cursus.repository.EnrollmentRepository;
import com.group4.cursus.repository.ReportRepository;
import com.group4.cursus.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReportServiceTest {

    @InjectMocks
    private ReportService reportService;

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private StudentRepository studentRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateReport_Success() throws Exception {
        Long courseId = 1L;
        Long studentId = 1L;
        ReportDTO reportDTO = new ReportDTO();
        reportDTO.setContents("Test Report");
        reportDTO.setImages("image.png");

        Course course = new Course();
        course.setCourseId(courseId);
        Enrollment enrollment = new Enrollment();
        enrollment.setCourse(course);

        Student student = new Student();
        student.setUserId(studentId.intValue());

        when(enrollmentRepository.findByStudentUserIdAndCourse_courseId(studentId, courseId)).thenReturn(enrollment);
        when(studentRepository.findById(Math.toIntExact(studentId))).thenReturn(Optional.of(student));

        Report report = new Report();
        report.setReportId(1L);
        report.setContents(reportDTO.getContents());
        report.setImages(reportDTO.getImages());
        report.setCourse(course);
        report.setStudent(student);

        when(reportRepository.save(any(Report.class))).thenReturn(report);

        ReportDTO createdReport = reportService.createReport(courseId, studentId, reportDTO);

        assertNotNull(createdReport);
        assertEquals(1L, createdReport.getReportId());
        assertEquals("Test Report", createdReport.getContents());
        assertEquals("image.png", createdReport.getImages());
        assertEquals(courseId, createdReport.getCourseId());

        ArgumentCaptor<Report> reportCaptor = ArgumentCaptor.forClass(Report.class);
        verify(reportRepository, times(1)).save(reportCaptor.capture());
        Report capturedReport = reportCaptor.getValue();
        assertEquals("Test Report", capturedReport.getContents());
        assertEquals("image.png", capturedReport.getImages());
        assertEquals(course, capturedReport.getCourse());
        assertEquals(student, capturedReport.getStudent());
    }

    @Test
    void testCreateReport_StudentNotEnrolled() {
        Long courseId = 1L;
        Long studentId = 1L;
        ReportDTO reportDTO = new ReportDTO();
        reportDTO.setContents("Test Report");
        reportDTO.setImages("image.png");

        when(enrollmentRepository.findByStudentUserIdAndCourse_courseId(studentId, courseId)).thenReturn(null);

        Exception exception = assertThrows(Exception.class, () -> {
            reportService.createReport(courseId, studentId, reportDTO);
        });

        assertEquals("You are not enrolled in this course", exception.getMessage());
    }

    @Test
    void testCreateReport_StudentNotFound() {
        Long courseId = 1L;
        Long studentId = 1L;
        ReportDTO reportDTO = new ReportDTO();
        reportDTO.setContents("Test Report");
        reportDTO.setImages("image.png");

        Course course = new Course();
        course.setCourseId(courseId);
        Enrollment enrollment = new Enrollment();
        enrollment.setCourse(course);

        when(enrollmentRepository.findByStudentUserIdAndCourse_courseId(studentId, courseId)).thenReturn(enrollment);
        when(studentRepository.findById(Math.toIntExact(studentId))).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () -> {
            reportService.createReport(courseId, studentId, reportDTO);
        });

        assertEquals("Student not found", exception.getMessage());
    }
}
