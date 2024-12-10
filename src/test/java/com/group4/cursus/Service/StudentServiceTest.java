package com.group4.cursus.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.group4.cursus.entity.Instructor;
import com.group4.cursus.entity.Student;
import com.group4.cursus.repository.InstructorRepository;
import com.group4.cursus.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class StudentServiceTest {
    @Mock
    private StudentRepository studentRepository;
    @Mock
    private InstructorRepository instructorRepository;
    @InjectMocks
    private StudentService studentService;

    private Student mockStudent1;
    private Student mockStudent2;
    private Instructor mockInstructor1;
    private Instructor mockInstructor2;
    private List<Student> mockStudentList;
    private List<Instructor> mockInstructorList;

    @BeforeEach
    public void setUp() {
        mockStudent1 = new Student("John Doe", "john.doe@email.com", "password123", LocalDate.of(2023, 1, 1), "123 Main St", false, true, "student");
        mockStudent2 = new Student("Jane Smith", "jane.smith@email.com", "password456", LocalDate.of(2023, 1, 15), "456 Elm St", false, true, "student");
        mockInstructor1 = new Instructor("Alice Johnson", "alice.johnson@email.com", "password789", LocalDate.of(2022, 12, 1), "789 Oak St", false, true, "instructor");
        mockInstructor2 = new Instructor("Bob Williams", "bob.williams@email.com", "password012", LocalDate.of(2022, 12, 15), "012 Maple St", false, true, "instructor");

        mockStudentList = new ArrayList<>();
        mockStudentList.add(mockStudent1);
        mockStudentList.add(mockStudent2);

        mockInstructorList = new ArrayList<>();
        mockInstructorList.add(mockInstructor1);
        mockInstructorList.add(mockInstructor2);
    }

    @Test
    public void testFindStudentByName() {
        when(studentRepository.findStudentByFullName(mockStudent1.getFullName())).thenReturn(mockStudentList);
        List<Student> students = studentService.findStudentByName(mockStudent1.getFullName());
        assertNotNull(students);
        assertEquals(2, students.size());
        assertTrue(students.contains(mockStudent1));
        assertTrue(students.contains(mockStudent2));
    }

//    @Test
//    public void testFindStudentByName_NotFound() {
//        when(studentRepository.findStudentByFullName("Not Found Name")).thenReturn(new ArrayList<>());
//        List<Student> students = studentService.findStudentByName("Not Found Name");
//        assertNotNull(students);
//        assertTrue(students.isEmpty());
//    }

    @Test
    public void testFindInstructorByName() {
        when(instructorRepository.findInstructorByFullName(mockInstructor1.getFullName())).thenReturn(mockInstructorList);
        List<Instructor> instructors = studentService.findInstructorByName(mockInstructor1.getFullName());
        assertNotNull(instructors);
        assertEquals(2, instructors.size());
        assertTrue(instructors.contains(mockInstructor1));
        assertTrue(instructors.contains(mockInstructor2));
    }

//    @Test
//    public void testFindInstructorByName_NotFound() {
//        when(instructorRepository.findInstructorByFullName("Not Found Name")).thenReturn(new ArrayList<>());
//        List<Instructor> instructors = studentService.findInstructorByName("Not Found Name");
//        assertNotNull(instructors);
//        assertTrue(instructors.isEmpty());
//    }
}
