package com.beta.schoolpayment.service;

import com.beta.schoolpayment.dto.request.StudentRequest;
import com.beta.schoolpayment.dto.response.StudentResponse;
import com.beta.schoolpayment.exception.DataNotFoundException;
import com.beta.schoolpayment.exception.ValidationException;
import com.beta.schoolpayment.model.Classes;
import com.beta.schoolpayment.model.Student;
import com.beta.schoolpayment.repository.ClassesRepository;
import com.beta.schoolpayment.repository.StudentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class StudentServiceTest {

    @InjectMocks
    private StudentService studentService;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private ClassesRepository classesRepository;

    private Student student;
    private StudentRequest studentRequest;
    private Classes classes;
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        classes = new Classes();
        classes.setClassesId(1L);
        classes.setClassesName("class");
        studentRequest = new StudentRequest();
        studentRequest.setNis(1234L);
        studentRequest.setName("John Doe");
        studentRequest.setBirthdate(LocalDate.of(2000, 1, 1));
        studentRequest.setAddress("123 Main St");
        studentRequest.setPhoneNumber("123-456-7890");
        studentRequest.setClassId(1L);

        student = new Student();
        student.setNis(studentRequest.getNis());
        student.setName(studentRequest.getName());
        student.setBirthdate(studentRequest.getBirthdate());
        student.setAddress(studentRequest.getAddress());
        student.setPhoneNumber(studentRequest.getPhoneNumber());
        student.setClasses(classes);
    }

    @Test
    public void testCreateStudent_Success() {
        // Given
        when(studentRepository.existsByNis(studentRequest.getNis())).thenReturn(false);
        when(classesRepository.findById(studentRequest.getClassId())).thenReturn(Optional.of(classes));
        when(studentRepository.save(any(Student.class))).thenReturn(student);

        // When
        StudentResponse response = studentService.createStudent(studentRequest);

        // Then
        assertNotNull(response);
        assertEquals("John Doe", response.getName());
        assertEquals(1234L, response.getNis());
        verify(studentRepository, times(1)).save(any(Student.class));
    }

    @Test
    public void testCreateStudent_Fail_NisAlreadyExists() {
        // Given
        when(studentRepository.existsByNis(studentRequest.getNis())).thenReturn(true);

        // When & Then (expect ValidationException)
        ValidationException exception = assertThrows(ValidationException.class, () ->
                studentService.createStudent(studentRequest)
        );
        assertEquals("NIS 1234 is already registered.", exception.getMessage());

        verify(studentRepository, never()).save(any(Student.class)); // Ensure save() is never called
    }

    @Test
    public void testCreateStudent_Fail_ClassNotFound() {
        // Given
        when(studentRepository.existsByNis(studentRequest.getNis())).thenReturn(false);
        when(classesRepository.findById(studentRequest.getClassId())).thenReturn(Optional.empty());

        // When & Then (expect DataNotFoundException)
        DataNotFoundException exception = assertThrows(DataNotFoundException.class, () ->
                studentService.createStudent(studentRequest)
        );
        assertEquals("Class not found", exception.getMessage());

        verify(studentRepository, never()).save(any(Student.class)); // Ensure save() is never called
    }

    @Test
    public void testGetAllStudents_success() {
        int page = 0, size = 5;
        Pageable pageable = PageRequest.of(page, size);
        List<Student> studentList = List.of(student);
        Page<Student> studentPage = new PageImpl<>(studentList, pageable, studentList.size());

        when(studentRepository.findAll(pageable)).thenReturn(studentPage);

        Page<StudentResponse> result = studentService.getAllStudents(page, size);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals("John Doe", result.getContent().getFirst().getName());

        // Verify repository method was called
        verify(studentRepository, times(1)).findAll(pageable);
    }
    @Test
    public void testDeleteStudent_Success() {
        // Given
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        studentService.deleteStudent(1L);

        assertNotNull(student.getDeletedAt());
        verify(studentRepository, times(1)).save(student);
    }

    // ❌ Test for Soft Delete (Student Not Found)
    @Test
    public void testDeleteStudent_ThrowsException_WhenStudentNotFound() {
        // Given
        when(studentRepository.findById(1L)).thenReturn(Optional.empty());

        // Then
        assertThrows(DataNotFoundException.class, () -> studentService.deleteStudent(1L));
        verify(studentRepository, never()).save(any());
    }

    // ✅ Test for Hard Delete
    @Test
    public void testHardDeleteStudent_Success() {
        // Given
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        // When
        studentService.hardDeleteStudent(1L);

        // Then
        verify(studentRepository, times(1)).deleteById(1L);
    }

    // ❌ Test for Hard Delete (Student Not Found)
    @Test
    public void testHardDeleteStudent_ThrowsException_WhenStudentNotFound() {
        // Given
        when(studentRepository.findById(1L)).thenReturn(Optional.empty());

        // Then
        assertThrows(DataNotFoundException.class, () -> studentService.hardDeleteStudent(1L));
        verify(studentRepository, never()).deleteById(any());
    }

    // ✅ Test for Update Student
    @Test
    public void testUpdateStudent_Success() {
        // Given
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(classesRepository.findById(1L)).thenReturn(Optional.of(classes));
        when(studentRepository.save(any(Student.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        StudentResponse response = studentService.updateStudent(1L, studentRequest);

        // Then
        assertNotNull(response);
        assertEquals("John Doe", student.getName());
        assertEquals(LocalDate.of(2000, 1, 1), student.getBirthdate());
        assertEquals("123 Main St", student.getAddress());
        assertEquals("123-456-7890", student.getPhoneNumber());
        assertEquals(1L, student.getClasses().getClassesId());

        verify(studentRepository, times(1)).save(student);
    }

    // ❌ Test for Update Student (Student Not Found)
    @Test
    public void testUpdateStudent_ThrowsException_WhenStudentNotFound() {
        // Given
        when(studentRepository.findById(1L)).thenReturn(Optional.empty());

        // Then
        assertThrows(DataNotFoundException.class, () -> studentService.updateStudent(1L, studentRequest));
        verify(studentRepository, never()).save(any());
    }

    // ❌ Test for Update Student (Class Not Found)
    @Test
    public void testUpdateStudent_ThrowsException_WhenClassNotFound() {
        // Given
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(classesRepository.findById(1L)).thenReturn(Optional.empty());

        // Then
        assertThrows(DataNotFoundException.class, () -> studentService.updateStudent(101L, studentRequest));
        verify(studentRepository, never()).save(any());
    }
}
