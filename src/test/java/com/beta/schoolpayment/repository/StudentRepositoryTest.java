package com.beta.schoolpayment.repository;

import com.beta.schoolpayment.model.Classes;
import com.beta.schoolpayment.model.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

public class StudentRepositoryTest {
    @Mock
    private StudentRepository studentRepository;
    private Student student;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        student = new Student();
        student.setId(1L);
        student.setClasses(new Classes());
        student.setNis(1234L);
        student.setName("John Doe");
        student.setBirthdate(LocalDate.of(2000, 1, 1));
        student.setAddress("123 Main St");
        student.setPhoneNumber("123-456-7890");
    }

    @Test
    public void testExistByNis_success(){
        when(studentRepository.existsByNis(student.getNis())).thenReturn(true);

        boolean isExist = this.studentRepository.existsByNis(1234L);
        assertTrue(isExist);
    }
    @Test
    public void testExistByNis_failure() {
        when(studentRepository.existsByNis(9999L)).thenReturn(false);

        boolean isExist = this.studentRepository.existsByNis(9999L);

        assertFalse(isExist);
    }
}
