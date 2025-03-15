package com.beta.schoolpayment.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class SchoolYearTest {
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
    public void testCreateSchoolYear() {
        schoolYear.onCreate();

        assertNotNull(schoolYear.getId());
        assertEquals("2024-2025", schoolYear.getSchoolYear());
        assertEquals(LocalDate.of(2024, 9, 1), schoolYear.getStartDate());
        assertEquals(LocalDate.of(2025, 6, 30), schoolYear.getEndDate());

        assertThat(schoolYear.getCreatedAt()).isNotNull();
        assertThat(schoolYear.getUpdatedAt()).isNotNull();
        assertThat(schoolYear.getDeletedAt()).isNull();
    }

    @Test
    public void testUpdateSchoolYear() {
        schoolYear.onCreate();
        LocalDateTime initialUpdatedAt = schoolYear.getUpdatedAt();

        try {
            Thread.sleep(10); // Simulating a delay before updating
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        schoolYear.onUpdate();

        assertNotNull(schoolYear.getUpdatedAt());
        assertThat(schoolYear.getUpdatedAt()).isAfter(initialUpdatedAt);
    }
}
