package com.beta.schoolpayment.service;

import com.beta.schoolpayment.dto.request.SchoolYearRequest;
import com.beta.schoolpayment.dto.response.SchoolYearResponse;
import com.beta.schoolpayment.model.SchoolYear;
import com.beta.schoolpayment.repository.SchoolYearRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SchoolYearServiceTest {
    @InjectMocks
    private SchoolYearService schoolYearService;

    @Mock
    private SchoolYearRepository schoolYearRepository;

    private SchoolYearRequest schoolYearRequest;
    private SchoolYear schoolYear1;
    private SchoolYear schoolYear2;
    private SchoolYear schoolYear;
    private SchoolYear existingSchoolYear;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        schoolYear1 = new SchoolYear();
        schoolYear1.setId(1L);
        schoolYear1.setSchoolYear("2024-2025");
        schoolYear1.setStartDate(LocalDate.of(2024, 9, 1));
        schoolYear1.setEndDate(LocalDate.of(2025, 6, 30));

        schoolYear2 = new SchoolYear();
        schoolYear2.setId(2L);
        schoolYear2.setSchoolYear("2025-2026");
        schoolYear2.setStartDate(LocalDate.of(2025, 9, 1));
        schoolYear2.setEndDate(LocalDate.of(2026, 6, 30));

        schoolYearRequest = new SchoolYearRequest();
        schoolYearRequest.setSchoolYear("2024-2025");
        schoolYearRequest.setStartDate(LocalDate.of(2024, 9, 1));
        schoolYearRequest.setEndDate(LocalDate.of(2025, 6, 30));

        schoolYear = new SchoolYear();
        schoolYear.setId(1L);
        schoolYear.setSchoolYear("2024-2025");
        schoolYear.setStartDate(LocalDate.of(2024, 9, 1));
        schoolYear.setEndDate(LocalDate.of(2025, 6, 30));

        existingSchoolYear = new SchoolYear();
        existingSchoolYear.setId(1L);
        existingSchoolYear.setSchoolYear("2024-2025");
        existingSchoolYear.setStartDate(LocalDate.of(2024, 9, 1));
        existingSchoolYear.setEndDate(LocalDate.of(2025, 6, 30));
    }

    @Test
    void testFindAll_Success() {
        when(schoolYearRepository.findAll()).thenReturn(List.of(schoolYear1, schoolYear2));

        List<SchoolYearResponse> responses = schoolYearService.findAll();

        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertEquals("2024-2025", responses.get(0).getSchoolYear());
        assertEquals("2025-2026", responses.get(1).getSchoolYear());

        verify(schoolYearRepository, times(1)).findAll();
    }

    @Test
    void testFindAll_EmptyList() {
        when(schoolYearRepository.findAll()).thenReturn(List.of());

        List<SchoolYearResponse> responses = schoolYearService.findAll();

        assertNotNull(responses);
        assertTrue(responses.isEmpty());

        verify(schoolYearRepository, times(1)).findAll();
    }

    @Test
    void testFindAll_ExceptionHandling() {
        when(schoolYearRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> schoolYearService.findAll());

        assertEquals("Failed to get all classes", exception.getMessage());

        verify(schoolYearRepository, times(1)).findAll();
    }

    @Test
    void testCreateSchoolYear_Success() {
        when(schoolYearRepository.save(any(SchoolYear.class))).thenReturn(schoolYear);

        SchoolYearResponse response = schoolYearService.createSchoolYear(schoolYearRequest);

        assertNotNull(response);
        assertEquals("2024-2025", response.getSchoolYear());
        assertEquals(LocalDate.of(2024, 9, 1), response.getStartDate());
        assertEquals(LocalDate.of(2025, 6, 30), response.getEndDate());

        verify(schoolYearRepository, times(1)).save(any(SchoolYear.class));
    }

    @Test
    void testCreateSchoolYear_ExceptionHandling() {
        when(schoolYearRepository.save(any(SchoolYear.class))).thenThrow(new RuntimeException("Database error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> schoolYearService.createSchoolYear(schoolYearRequest));

        assertEquals("Failed to create classes", exception.getMessage());

        verify(schoolYearRepository, times(1)).save(any(SchoolYear.class));
    }

    @Test
    void testUpdateSchoolYear_Success() {
        when(schoolYearRepository.findById(1L)).thenReturn(Optional.of(existingSchoolYear));
        when(schoolYearRepository.save(any(SchoolYear.class))).thenReturn(existingSchoolYear);

        SchoolYearResponse response = schoolYearService.updateSchoolYear(1L, schoolYearRequest);

        assertNotNull(response);
        assertEquals("2024-2025", response.getSchoolYear());
        assertEquals(LocalDate.of(2024, 9, 1), response.getStartDate());
        assertEquals(LocalDate.of(2025, 6, 30), response.getEndDate());

        verify(schoolYearRepository, times(1)).findById(1L);
        verify(schoolYearRepository, times(1)).save(any(SchoolYear.class));
    }

    @Test
    void testUpdateSchoolYear_NotFound() {
        when(schoolYearRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                schoolYearService.updateSchoolYear(1L, schoolYearRequest)
        );

        assertEquals("Failed to update classes", exception.getMessage());

        verify(schoolYearRepository, times(1)).findById(1L);
        verify(schoolYearRepository, never()).save(any(SchoolYear.class));
    }

    @Test
    void testDeleteSchoolYear_Success() {
        Long schoolYearId = 1L;

        when(schoolYearRepository.existsById(schoolYearId)).thenReturn(true);

        schoolYearService.deleteSchoolYear(schoolYearId);

        verify(schoolYearRepository, times(1)).existsById(schoolYearId);
        verify(schoolYearRepository, times(1)).deleteById(schoolYearId);
    }

    @Test
    void testDeleteSchoolYear_ExceptionHandling() {
        Long schoolYearId = 1L;

        when(schoolYearRepository.existsById(schoolYearId)).thenReturn(true);
        doThrow(new RuntimeException("Database error")).when(schoolYearRepository).deleteById(schoolYearId);

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                schoolYearService.deleteSchoolYear(schoolYearId)
        );

        assertEquals("Failed to delete classes", exception.getMessage());

        verify(schoolYearRepository, times(1)).existsById(schoolYearId);
        verify(schoolYearRepository, times(1)).deleteById(schoolYearId);
    }

    @Test
    void testSearchSchoolYear_Success() {
        String searchQuery = "2024";
        List<SchoolYear> foundSchoolYears = List.of(schoolYear1);

        when(schoolYearRepository.findBySchoolYearContainingIgnoringCase(searchQuery)).thenReturn(foundSchoolYears);

        List<SchoolYearResponse> responses = schoolYearService.searchSchoolYear(searchQuery);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("2024-2025", responses.get(0).getSchoolYear());

        verify(schoolYearRepository, times(1)).findBySchoolYearContainingIgnoringCase(searchQuery);
    }

    @Test
    void testSearchSchoolYear_NoResults() {
        String searchQuery = "2030"; // No matching records
        when(schoolYearRepository.findBySchoolYearContainingIgnoringCase(searchQuery)).thenReturn(List.of());

        List<SchoolYearResponse> responses = schoolYearService.searchSchoolYear(searchQuery);

        assertNotNull(responses);
        assertTrue(responses.isEmpty());

        verify(schoolYearRepository, times(1)).findBySchoolYearContainingIgnoringCase(searchQuery);
    }
}
