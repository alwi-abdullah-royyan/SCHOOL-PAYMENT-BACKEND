package com.beta.schoolpayment.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class ClassesTest {

    private Classes classes;
    private SchoolYear schoolYear;

    @BeforeEach
    void setUp() {
        schoolYear = new SchoolYear();
        schoolYear.setId(1L);
        schoolYear.setSchoolYear("2023-2024");

        classes = new Classes();
        classes.setClassesId(1L);
        classes.setClassesName("Mathematics");
        classes.setSchoolYear(schoolYear);
    }

    @Test
    void testCreateClasses() {
        classes.onCreate();
        assertNotNull(classes.getCreatedAt());
        assertNotNull(classes.getUpdatedAt());

        assertEquals(1L, classes.getClassesId());
        assertEquals("Mathematics", classes.getClassesName());
        assertEquals(schoolYear, classes.getSchoolYear());
        assertNull(classes.getDeletedAt());
    }

    @Test
    void testUpdateClasses() {
        classes.onCreate();
        LocalDateTime initialUpdatedAt = classes.getUpdatedAt();

        try {
            Thread.sleep(10); // Simulate delay to ensure updatedAt changes
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        classes.onUpdate();

        assertNotNull(classes.getUpdatedAt());
        assertThat(classes.getUpdatedAt()).isAfter(initialUpdatedAt);
    }
}
