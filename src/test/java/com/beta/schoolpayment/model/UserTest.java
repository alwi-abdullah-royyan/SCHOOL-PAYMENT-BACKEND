package com.beta.schoolpayment.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class UserTest {
    private User user;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setUserId(UUID.randomUUID());
        user.setNis(1234L);
        user.setEmail("johndoe@example.com");
        user.setName("John Doe");
        user.setPassword("hashed_password");
        user.setRole("ADMIN");
        user.setProfilePicture("profile.jpg");
    }

    @Test
    public void testCreateUser() {
        user.onCreate(); // Simulate entity creation

        assertNotNull(user.getUserId());
        assertEquals(1234L, user.getNis());
        assertEquals("johndoe@example.com", user.getEmail());
        assertEquals("John Doe", user.getName());
        assertEquals("hashed_password", user.getPassword());
        assertEquals("ADMIN", user.getRole());
        assertEquals("profile.jpg", user.getProfilePicture());

        assertThat(user.getCreatedAt()).isNotNull();
        assertThat(user.getUpdatedAt()).isNotNull();
        assertNull(user.getDeletedAt()); // Ensure it's null at creation
    }

    @Test
    public void testUpdateUser() {
        user.onCreate();
        LocalDateTime initialUpdatedAt = user.getUpdatedAt();

        try {
            Thread.sleep(10); // Small delay to ensure time change
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        user.onUpdate(); // Simulate entity update

        assertNotNull(user.getUpdatedAt());
        assertThat(user.getUpdatedAt()).isAfter(initialUpdatedAt);
    }
}

