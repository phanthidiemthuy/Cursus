package com.group4.cursus.service;

import com.group4.cursus.dto.CourseDTO;
import com.group4.cursus.dto.InstructorDTO;
import com.group4.cursus.entity.Course;
import com.group4.cursus.entity.Enrollment;
import com.group4.cursus.repository.EnrollmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EnrollmentService {
    @Autowired
    private EnrollmentRepository enrollmentRepository;

    public List<CourseDTO> getCoursesByStudentId(Long studentId) {
        List<Enrollment> enrollments = enrollmentRepository.findCourseByStudentId(studentId);
        return enrollments.stream()
                .map(enrollment -> convertToDTO(enrollment.getCourse()))
                .collect(Collectors.toList());
    }
    private CourseDTO convertToDTO(Course course) {
        CourseDTO courseDTO = new CourseDTO();
        courseDTO.setCourseTitle(course.getCourseTitle());
        courseDTO.setDescription(course.getDescription());
        courseDTO.setRequirements(course.getRequirements());
        courseDTO.setCourseLevel(course.getCourseLevel());
        courseDTO.setRegularPrice(course.getRegularPrice());
        courseDTO.setStatus(course.getStatus());
        return courseDTO;
    }


}
