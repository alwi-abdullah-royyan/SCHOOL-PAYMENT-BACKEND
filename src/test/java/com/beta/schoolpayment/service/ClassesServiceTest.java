package com.beta.schoolpayment.service;

import com.beta.schoolpayment.dto.request.ClassesRequest;
import com.beta.schoolpayment.dto.response.ClassesResponse;
import com.beta.schoolpayment.model.Classes;
import com.beta.schoolpayment.model.SchoolYear;
import com.beta.schoolpayment.repository.ClassesRepository;
import com.beta.schoolpayment.repository.SchoolYearRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ClassesServiceTest {

    @Mock
    private ClassesRepository classesRepository;

    @Mock
    private SchoolYearRepository schoolYearRepository;

    @InjectMocks
    private ClassesService classesService;

    private Classes classes;
    private SchoolYear schoolYear;
    private ClassesRequest classesRequest;

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

        classesRequest = new ClassesRequest();
        classesRequest.setClassesName("Mathematics");
        classesRequest.setSchoolYearId(1L);
    }

    @Test
    void testFindAll_Success() {
        when(classesRepository.findAll()).thenReturn(List.of(classes));

        List<ClassesResponse> result = classesService.findAll();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertThat(result.get(0).getClassesName()).isEqualTo("Mathematics");

        verify(classesRepository, times(1)).findAll();
    }

    @Test
    void testFindAll_ThrowsException() {
        when(classesRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        Exception exception = assertThrows(RuntimeException.class, () -> classesService.findAll());

        assertEquals("Failed to get all classes", exception.getMessage());
    }

    @Test
    void testCreateClasses_Success() {
        when(schoolYearRepository.findById(classesRequest.getSchoolYearId())).thenReturn(Optional.of(schoolYear));
        when(classesRepository.save(any(Classes.class))).thenReturn(classes);

        ClassesResponse response = classesService.createClasses(classesRequest);

        assertNotNull(response);
        assertEquals("Mathematics", response.getClassesName());
        assertThat(response.getSchoolYearId()).isEqualTo(1L);

        verify(schoolYearRepository, times(1)).findById(1L);
        verify(classesRepository, times(1)).save(any(Classes.class));
    }

    @Test
    void testCreateClasses_Fail_ExceptionThrown() {
        when(schoolYearRepository.findById(classesRequest.getSchoolYearId())).thenReturn(Optional.of(schoolYear));
        when(classesRepository.save(any(Classes.class))).thenThrow(new RuntimeException("Database error"));

        Exception exception = assertThrows(RuntimeException.class, () -> classesService.createClasses(classesRequest));

        assertEquals("Failed to create classes", exception.getMessage());
    }

    @Test
    void testUpdateClasses_Success() {
        when(classesRepository.findByClassesId(1L)).thenReturn(List.of(classes));
        when(schoolYearRepository.findById(1L)).thenReturn(Optional.of(schoolYear));
        when(classesRepository.save(any(Classes.class))).thenReturn(classes);

        ClassesResponse response = classesService.updateClasses(1L, classesRequest);

        assertNotNull(response);
        assertEquals("Mathematics", response.getClassesName());
        assertThat(response.getSchoolYearId()).isEqualTo(1L);

        verify(classesRepository, times(1)).findByClassesId(1L);
        verify(schoolYearRepository, times(1)).findById(1L);
        verify(classesRepository, times(1)).save(any(Classes.class));
    }

    @Test
    void testUpdateClasses_Fail_ExceptionThrown() {
        when(classesRepository.findByClassesId(1L)).thenReturn(List.of(classes));
        when(schoolYearRepository.findById(1L)).thenReturn(Optional.of(schoolYear));
        when(classesRepository.save(any(Classes.class))).thenThrow(new RuntimeException("Database error"));

        Exception exception = assertThrows(RuntimeException.class, () -> classesService.updateClasses(1L, classesRequest));

        assertEquals("Failed to update classes", exception.getMessage());
    }

    @Test
    void testDeleteClasses_Success() {
        Long classId = 1L;
        when(classesRepository.existsById(classId)).thenReturn(true);
        doNothing().when(classesRepository).deleteById(classId);

        assertDoesNotThrow(() -> classesService.deleteClasses(classId));

        verify(classesRepository, times(1)).existsById(classId);
        verify(classesRepository, times(1)).deleteById(classId);
    }

    @Test
    void testDeleteClasses_Fail_ExceptionThrown() {
        Long classId = 1L;
        when(classesRepository.existsById(classId)).thenReturn(true);
        doThrow(new RuntimeException("Database error")).when(classesRepository).deleteById(classId);

        Exception exception = assertThrows(RuntimeException.class, () -> classesService.deleteClasses(classId));

        assertEquals("Failed to delete classes", exception.getMessage());

        verify(classesRepository, times(1)).existsById(classId);
        verify(classesRepository, times(1)).deleteById(classId);
    }

    @Test
    void testSearchClasses_Success() {
        String searchKeyword = "Math";

        when(classesRepository.findByClassesNameContainingIgnoringCase(searchKeyword))
                .thenReturn(List.of(classes));

        List<ClassesResponse> result = classesService.searchClasses(searchKeyword);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Mathematics", result.get(0).getClassesName());

        verify(classesRepository, times(1)).findByClassesNameContainingIgnoringCase(searchKeyword);
    }

    @Test
    void testSearchClasses_EmptyResult() {
        String searchKeyword = "Physics";

        when(classesRepository.findByClassesNameContainingIgnoringCase(searchKeyword))
                .thenReturn(List.of());

        List<ClassesResponse> result = classesService.searchClasses(searchKeyword);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(classesRepository, times(1)).findByClassesNameContainingIgnoringCase(searchKeyword);
    }

    @Test
    void testSearchClasses_Fail_ExceptionThrown() {
        String searchKeyword = "History";

        when(classesRepository.findByClassesNameContainingIgnoringCase(searchKeyword))
                .thenThrow(new RuntimeException("Database error"));

        Exception exception = assertThrows(RuntimeException.class, () -> classesService.searchClasses(searchKeyword));

        assertEquals("Failed to search classes", exception.getMessage());

        verify(classesRepository, times(1)).findByClassesNameContainingIgnoringCase(searchKeyword);
    }
}
