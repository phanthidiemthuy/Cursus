package com.group4.cursus.service;

import com.group4.cursus.dto.ReportDTO;
import com.group4.cursus.entity.Course;
import com.group4.cursus.entity.Enrollment;
import com.group4.cursus.entity.Report;
import com.group4.cursus.entity.Student;
import com.group4.cursus.repository.CourseRepository;
import com.group4.cursus.repository.EnrollmentRepository;
import com.group4.cursus.repository.ReportRepository;
import com.group4.cursus.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReportService {

    @Autowired
    private EnrollmentRepository enrollmentRepository;
    @Autowired
    private ReportRepository reportRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private CourseRepository courseRepository;

    public ReportDTO createReport(Long courseId, Long studentId, ReportDTO reportDTO) throws Exception{

        Enrollment enrollment = enrollmentRepository.findByStudentUserIdAndCourse_courseId(studentId, courseId);

        if (enrollment == null) {
            throw new Exception("You are not enrolled in this course");
        }

        Student student = studentRepository.findById(Math.toIntExact(studentId)).orElseThrow(() -> new Exception("Student not found"));

        Report report = new Report();
        report.setContents(reportDTO.getContents());
        report.setImages(reportDTO.getImages());
        report.setCourse(enrollment.getCourse());
        report.setStudent(student);
        Report reports = reportRepository.save(report);
        return new ReportDTO(){{
            setReportId(reports.getReportId());
            setContents(reports.getContents());
            setImages(reports.getImages());
            setCourseId(reports.getCourse().getCourseId());
        }};
    }

  public List<ReportDTO> getReport(Long courseId) throws Exception {
      Course course = courseRepository.findById(courseId)
              .orElseThrow(() -> new Exception("Course not found"));

        List<Report> reports = reportRepository.findByCourse_CourseId(courseId);
        return reports.stream().map(report -> {
            ReportDTO reportDTO = new ReportDTO();
            reportDTO.setReportId(report.getReportId());
            reportDTO.setContents(report.getContents());
            reportDTO.setImages(report.getImages());
            reportDTO.setCourseId(report.getCourse().getCourseId());
            reportDTO.setStudentId((long) report.getStudent().getUserId());
            reportDTO.setStudentName(report.getStudent().getFullName());
            return reportDTO;
        }).collect(Collectors.toList());
    }
}
