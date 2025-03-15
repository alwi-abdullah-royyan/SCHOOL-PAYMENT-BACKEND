package com.beta.schoolpayment.repository;

import com.beta.schoolpayment.model.Classes;
import com.beta.schoolpayment.model.SchoolYear;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ClassesRepositoryTest {

    @Mock
    private ClassesRepository classesRepository;

    private Classes classes;
    private SchoolYear schoolYear;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        schoolYear = new SchoolYear();
        schoolYear.setId(1L);
        schoolYear.setSchoolYear("2023-2024");

        classes = new Classes();
        classes.setClassesId(1L);
        classes.setClassesName("Mathematics");
        classes.setSchoolYear(schoolYear);
    }

    @Test
    void testFindByClassesId_Success() {
        when(classesRepository.findByClassesId(1L)).thenReturn(List.of(classes));

        List<Classes> result = classesRepository.findByClassesId(1L);

        assertFalse(result.isEmpty());
        assertEquals(1L, result.get(0).getClassesId());
        assertEquals("Mathematics", result.get(0).getClassesName());
    }

    @Test
    void testFindBySchoolYear_Success() {
        when(classesRepository.findBySchoolYear(schoolYear)).thenReturn(List.of(classes));

        List<Classes> result = classesRepository.findBySchoolYear(schoolYear);

        assertFalse(result.isEmpty());
        assertEquals(schoolYear, result.get(0).getSchoolYear());
    }

    @Test
    void testFindByClassesNameContainingIgnoringCase_Success() {
        when(classesRepository.findByClassesNameContainingIgnoringCase("math")).thenReturn(List.of(classes));

        List<Classes> result = classesRepository.findByClassesNameContainingIgnoringCase("math");

        assertFalse(result.isEmpty());
        assertTrue(result.get(0).getClassesName().toLowerCase().contains("math"));
    }
}
