package com.group4.cursus.service;


import com.group4.cursus.entity.Enrollment;
import com.group4.cursus.entity.Instructor;
import com.group4.cursus.entity.Student;
import com.group4.cursus.repository.EnrollmentRepository;
import com.group4.cursus.repository.InstructorRepository;
import com.group4.cursus.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudentService {
    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private InstructorRepository instructorRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    public List<Student> findStudentByName(String fullName){
        return studentRepository.findStudentByFullName(fullName);
    }

    public List<Instructor> findInstructorByName(String fullName){
        return instructorRepository.findInstructorByFullName(fullName);
    }


}
