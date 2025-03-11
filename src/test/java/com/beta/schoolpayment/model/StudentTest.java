package com.beta.schoolpayment.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class StudentTest {
    private Student student;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        student = new Student();
        student.setClasses(new Classes());
        student.setNis(1234L);
        student.setName("John Doe");
        student.setBirthdate(LocalDate.of(2000, 1, 1));
        student.setAddress("123 Main St");
        student.setPhoneNumber("123-456-7890");
    }

    @Test
    public void testCreateStudent(){
        student.onCreate();
        student.setId(1L);
        assertNotNull(student.getId());
        assertEquals(1L, student.getId());
        assertEquals(1234L, student.getNis());
        assertEquals("John Doe", student.getName());
        assertEquals(LocalDate.of(2000, 1, 1), student.getBirthdate());
        assertEquals("123 Main St", student.getAddress());
        assertEquals("123-456-7890", student.getPhoneNumber());
        assertNotNull(student.getClasses());
        assertThat(student.getCreatedAt()).isNotNull();
        assertThat(student.getUpdatedAt()).isNotNull();
        assertThat(student.getDeletedAt()).isNull();
    }
    @Test
    public void testUpdateStudent() {
        student.onCreate();
        LocalDateTime initialUpdatedAt = student.getUpdatedAt();

        try {
            Thread.sleep(10); // 10ms delay
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        student.onUpdate();

        assertNotNull(student.getUpdatedAt());
        assertThat(student.getUpdatedAt()).isAfter(initialUpdatedAt);
    }
}
