package com.beta.schoolpayment.repository;

import com.beta.schoolpayment.model.SchoolYear;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class SchoolYearRepositoryTest {
    @Mock
    private SchoolYearRepository schoolYearRepository;

    private SchoolYear schoolYear;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        schoolYear = new SchoolYear();
        schoolYear.setId(1L);
        schoolYear.setSchoolYear("2024-2025");
        schoolYear.setStartDate(LocalDate.of(2024, 9, 1));
        schoolYear.setEndDate(LocalDate.of(2025, 6, 30));
    }

    @Test
    public void testFindBySchoolYearContainingIgnoringCase_Success() {
        when(schoolYearRepository.findBySchoolYearContainingIgnoringCase("2024"))
                .thenReturn(List.of(schoolYear));

        List<SchoolYear> result = schoolYearRepository.findBySchoolYearContainingIgnoringCase("2024");

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("2024-2025", result.get(0).getSchoolYear());
    }

    @Test
    public void testFindBySchoolYearContainingIgnoringCase_NotFound() {
        when(schoolYearRepository.findBySchoolYearContainingIgnoringCase("2030"))
                .thenReturn(List.of());

        List<SchoolYear> result = schoolYearRepository.findBySchoolYearContainingIgnoringCase("2030");

        assertTrue(result.isEmpty());
    }

    @Test
    public void testFindByStartDateAndEndDate_Success() {
        when(schoolYearRepository.findByStartDateAndEndDate(
                LocalDate.of(2024, 9, 1),
                LocalDate.of(2025, 6, 30))
        ).thenReturn(Optional.of(schoolYear));

        Optional<SchoolYear> result = schoolYearRepository.findByStartDateAndEndDate(
                LocalDate.of(2024, 9, 1),
                LocalDate.of(2025, 6, 30)
        );

        assertTrue(result.isPresent());
        assertEquals("2024-2025", result.get().getSchoolYear());
    }

    @Test
    public void testFindByStartDateAndEndDate_NotFound() {
        when(schoolYearRepository.findByStartDateAndEndDate(
                LocalDate.of(2030, 1, 1),
                LocalDate.of(2031, 1, 1))
        ).thenReturn(Optional.empty());

        Optional<SchoolYear> result = schoolYearRepository.findByStartDateAndEndDate(
                LocalDate.of(2030, 1, 1),
                LocalDate.of(2031, 1, 1)
        );

        assertFalse(result.isPresent());
    }
}
