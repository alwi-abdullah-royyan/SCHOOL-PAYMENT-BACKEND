package com.beta.schoolpayment.repository;

import com.beta.schoolpayment.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class UserRepositoryTest {

    @Mock
    private UserRepository userRepository;
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
    }

    @Test
    public void testFindUserByNis_Success() {
        when(userRepository.findUserByNis(1234L)).thenReturn(Optional.of(user));

        Optional<User> foundUser = userRepository.findUserByNis(1234L);
        assertTrue(foundUser.isPresent());
        assertEquals(1234L, foundUser.get().getNis());
    }

    @Test
    public void testFindUserByNis_NotFound() {
        when(userRepository.findUserByNis(9999L)).thenReturn(Optional.empty());

        Optional<User> foundUser = userRepository.findUserByNis(9999L);
        assertFalse(foundUser.isPresent());
    }

    @Test
    public void testFindUserByEmail_Success() {
        when(userRepository.findUserByEmail("johndoe@example.com")).thenReturn(Optional.of(user));

        Optional<User> foundUser = userRepository.findUserByEmail("johndoe@example.com");
        assertTrue(foundUser.isPresent());
        assertEquals("johndoe@example.com", foundUser.get().getEmail());
    }

    @Test
    public void testFindUserByEmail_NotFound() {
        when(userRepository.findUserByEmail("notfound@example.com")).thenReturn(Optional.empty());

        Optional<User> foundUser = userRepository.findUserByEmail("notfound@example.com");
        assertFalse(foundUser.isPresent());
    }

    @Test
    public void testFindByEmailOrNis_Success_Email() {
        when(userRepository.findByEmailOrNis("johndoe@example.com", 9999L)).thenReturn(Optional.of(user));

        Optional<User> foundUser = userRepository.findByEmailOrNis("johndoe@example.com", 9999L);
        assertTrue(foundUser.isPresent());
        assertEquals("johndoe@example.com", foundUser.get().getEmail());
    }

    @Test
    public void testFindByEmailOrNis_Success_Nis() {
        when(userRepository.findByEmailOrNis("notfound@example.com", 1234L)).thenReturn(Optional.of(user));

        Optional<User> foundUser = userRepository.findByEmailOrNis("notfound@example.com", 1234L);
        assertTrue(foundUser.isPresent());
        assertEquals(1234L, foundUser.get().getNis());
    }

    @Test
    public void testFindByEmailOrNis_NotFound() {
        when(userRepository.findByEmailOrNis("notfound@example.com", 9999L)).thenReturn(Optional.empty());

        Optional<User> foundUser = userRepository.findByEmailOrNis("notfound@example.com", 9999L);
        assertFalse(foundUser.isPresent());
    }
}
