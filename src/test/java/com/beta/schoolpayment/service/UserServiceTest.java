package com.beta.schoolpayment.service;

import com.beta.schoolpayment.dto.request.AuthRequest;
import com.beta.schoolpayment.dto.request.UserRequest;
import com.beta.schoolpayment.dto.response.AuthResponse;
import com.beta.schoolpayment.dto.response.UserResponse;
import com.beta.schoolpayment.exception.DataNotFoundException;
import com.beta.schoolpayment.exception.ValidationException;
import com.beta.schoolpayment.model.User;
import com.beta.schoolpayment.repository.StudentRepository;
import com.beta.schoolpayment.repository.UserRepository;
import com.beta.schoolpayment.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {
    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private StudentRepository studentRepository;


    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private UserRequest userRequest;
    private User user;
    private AuthRequest authRequest;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        ReflectionTestUtils.setField(userService, "authenticationManager", authenticationManager);
        ReflectionTestUtils.setField(userService, "jwtUtil", jwtUtil);

        userRequest = new UserRequest();
        userRequest.setNis(12345L);
        userRequest.setName("John Doe");
        userRequest.setEmail("johndoe@example.com");
        userRequest.setPassword("password123");
        userRequest.setConfirmPassword("password123");

        user = new User();
        user.setNis(12345L);
        user.setName("John Doe");
        user.setEmail("johndoe@example.com");
        user.setPassword(passwordEncoder.encode("password123"));
        user.setRole("STUDENT");
        user.setProfilePicture("profile.jpg");

        authRequest = new AuthRequest();
        authRequest.setIdentifier("johndoe@example.com");
        authRequest.setPassword("password123");

    }

    @Test
    void testRegister_Success() {
        when(userRepository.findUserByNis(userRequest.getNis())).thenReturn(Optional.empty());
        when(studentRepository.existsByNis(userRequest.getNis())).thenReturn(true);
        when(userRepository.findUserByEmail(userRequest.getEmail())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponse response = userService.register(userRequest);

        assertThat(response).isNotNull();
        assertThat(response.getEmail()).isEqualTo(user.getEmail());

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testRegister_Fail_NisAlreadyExists() {
        when(userRepository.findUserByNis(userRequest.getNis())).thenReturn(Optional.of(user));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.register(userRequest);
        });

        assertThat(exception.getMessage()).isEqualTo("NIS already exists");
    }

    @Test
    void testRegister_Fail_NisDoesNotExist() {
        when(userRepository.findUserByNis(userRequest.getNis())).thenReturn(Optional.empty());
        when(studentRepository.existsByNis(userRequest.getNis())).thenReturn(false);

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
            userService.register(userRequest)
        );

        assertThat(exception.getMessage()).isEqualTo("NIS does not exist");
    }

    @Test
    void testRegister_Fail_EmailAlreadyExists() {
        when(userRepository.findUserByNis(userRequest.getNis())).thenReturn(Optional.empty());
        when(studentRepository.existsByNis(userRequest.getNis())).thenReturn(true);
        when(userRepository.findUserByEmail(userRequest.getEmail())).thenReturn(Optional.of(user));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.register(userRequest);
        });

        assertThat(exception.getMessage()).isEqualTo("Email already exists");
    }

    @Test
    void testRegister_Fail_PasswordTooShort() {
        when(userRepository.findUserByNis(userRequest.getNis())).thenReturn(Optional.empty());
        when(studentRepository.existsByNis(userRequest.getNis())).thenReturn(true);
        userRequest.setPassword("short");
        userRequest.setConfirmPassword("short");

        Exception exception = assertThrows(ValidationException.class, () -> {
            userService.register(userRequest);
        });

        assertThat(exception.getMessage()).isEqualTo("Password must be at least 8 characters");
    }

    @Test
    void testRegister_Fail_PasswordMismatch() {
        when(userRepository.findUserByNis(userRequest.getNis())).thenReturn(Optional.empty());
        when(studentRepository.existsByNis(userRequest.getNis())).thenReturn(true);
        userRequest.setConfirmPassword("differentPassword");

        Exception exception = assertThrows(ValidationException.class, () -> {
            userService.register(userRequest);
        });

        assertThat(exception.getMessage()).isEqualTo("Password and confirm password do not match");
    }

    @Test
    void testLogin_Success() {
        when(userRepository.findByEmailOrNis(authRequest.getIdentifier(),null))
                .thenReturn(Optional.of(user));

        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);

        when(jwtUtil.generateToken(user)).thenReturn("mocked-jwt-token");

        AuthResponse response = userService.login(authRequest);

        assertNotNull(response);
        assertEquals("mocked-jwt-token", response.getToken());
        verify(authenticationManager, times(1)).authenticate(any());
    }

    @Test
    void testLogin_Failure_UserNotFound() {
        when(userRepository.findByEmailOrNis(authRequest.getIdentifier(), 123456L))
                .thenReturn(Optional.empty());

        DataNotFoundException exception = assertThrows(
                DataNotFoundException.class,
                () -> userService.login(authRequest)
        );

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void testLogin_Failure_InvalidAuthentication() {
        when(userRepository.findByEmailOrNis(authRequest.getIdentifier(),null))
                .thenReturn(Optional.of(user));

        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(false);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);

        // Mock authentication failure
        when(authenticationManager.authenticate(any()))
                .thenThrow(new RuntimeException("Invalid authentication"));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> userService.login(authRequest)
        );

        assertEquals("Invalid authentication", exception.getMessage());
    }

    @Test
    public void testGetAllUser_success() {
        int page = 0, size = 5;
        Pageable pageable = PageRequest.of(page, size);

        List<User> userList = List.of(user);
        Page<User> userPage = new PageImpl<>(userList, pageable, userList.size());

        when(userRepository.findAll(pageable)).thenReturn(userPage);

        Page<UserResponse> result = userService.getAllUser(page, size);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals(user.getName(), result.getContent().getFirst().getName());

        verify(userRepository, times(1)).findAll(pageable);
    }

    @Test
    public void testGetUserFilter_success() {
        int page = 0, size = 5;
        String role = "STUDENT";
        Pageable pageable = PageRequest.of(page, size);

        List<User> userList = List.of(user); // Assuming 'user' has role "ADMIN"
        Page<User> userPage = new PageImpl<>(userList, pageable, userList.size());

        when(userRepository.findByRoleOrderByUpdatedAtDesc(role, pageable)).thenReturn(userPage);

        Page<UserResponse> result = userService.getUserFilter(page, size, role);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals(role, result.getContent().getFirst().getRole()); // Assuming UserResponse has getRole()

        verify(userRepository, times(1)).findByRoleOrderByUpdatedAtDesc(role, pageable);
    }

    @Test
    public void testUpdateRole_success() {
        UUID userId = UUID.randomUUID();
        UserRequest userRequest = new UserRequest();
        userRequest.setRole("ADMIN");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user)); // Mock user retrieval
        when(userRepository.save(any(User.class))).thenReturn(user); // Mock saving user

        UserResponse result = userService.updateRole(userId, userRequest);

        assertNotNull(result);
        assertEquals(userRequest.getRole(), result.getRole());

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testUpdateRole_nullRole_throwsException() {
        UUID userId = UUID.randomUUID();
        UserRequest userRequest = new UserRequest(); // Role is null

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.updateRole(userId, userRequest));

        assertEquals("Role cannot be null", exception.getMessage());
        verify(userRepository, never()).findById(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    public void testUpdateRole_invalidRole_throwsException() {
        UUID userId = UUID.randomUUID();
        UserRequest userRequest = new UserRequest();
        userRequest.setRole("INVALID_ROLE");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.updateRole(userId, userRequest));

        assertEquals("Role must be 'STUDENT' or 'ADMIN'", exception.getMessage());
        verify(userRepository, never()).findById(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    public void testUpdateRole_userNotFound_throwsException() {
        UUID userId = UUID.randomUUID();
        UserRequest userRequest = new UserRequest();
        userRequest.setRole("ADMIN");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        DataNotFoundException exception = assertThrows(DataNotFoundException.class,
                () -> userService.updateRole(userId, userRequest));

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).save(any());
    }

    @Test
    public void testSoftDelete_success() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponse result = userService.softDelete(userId);

        assertNotNull(result);
        assertNotNull(user.getDeletedAt());

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testSoftDelete_userNotFound_throwsException() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        DataNotFoundException exception = assertThrows(DataNotFoundException.class,
                () -> userService.softDelete(userId));

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).save(any());
    }

    @Test
    public void testHardDelete_success() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user)); // Mock user retrieval

        userService.hardDelete(userId);

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).delete(user);
    }

    @Test
    public void testHardDelete_userNotFound_throwsException() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        DataNotFoundException exception = assertThrows(DataNotFoundException.class,
                () -> userService.hardDelete(userId));

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).delete(any());
    }

    @Test
    public void testUpdateProfile_success() {
        Authentication authentication = mock(Authentication.class);
        UserDetails userDetails = mock(UserDetails.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(user.getEmail()); // Simulate logged-in user

        UserRequest userRequest = new UserRequest();
        userRequest.setEmail("newemail@example.com");
        userRequest.setPassword("newpassword123");
        userRequest.setConfirmPassword("newpassword123");

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(userRepository.findUserByEmail(userRequest.getEmail())).thenReturn(Optional.empty()); // Email not taken
        when(userRepository.save(any(User.class))).thenReturn(user);
        UserResponse result = userService.updateProfile(authentication, userRequest);

        assertNotNull(result);
        assertEquals("newemail@example.com", user.getEmail());
        assertNotEquals("newpassword123", user.getPassword());
        assertTrue(user.getPassword().startsWith("$2a$"));

        verify(userRepository, times(1)).findByEmail("johndoe@example.com");
        verify(userRepository, times(1)).findUserByEmail(userRequest.getEmail());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testUpdateProfile_userNotFound_throwsException() {
        Authentication authentication = mock(Authentication.class);
        UserDetails userDetails = mock(UserDetails.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("nonexistent@example.com");

        UserRequest userRequest = new UserRequest();
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        DataNotFoundException exception = assertThrows(DataNotFoundException.class,
                () -> userService.updateProfile(authentication, userRequest));

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findByEmail("nonexistent@example.com");
    }

    @Test
    public void testUpdateProfile_emailExists_throwsException() {
        Authentication authentication = mock(Authentication.class);
        UserDetails userDetails = mock(UserDetails.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(user.getEmail());

        UserRequest userRequest = new UserRequest();
        userRequest.setEmail("existing@example.com");

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(userRepository.findUserByEmail("existing@example.com")).thenReturn(Optional.of(new User()));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.updateProfile(authentication, userRequest));

        assertEquals("Email already exists", exception.getMessage());
    }

    @Test
    public void testUpdateProfile_shortPassword_throwsException() {
        Authentication authentication = mock(Authentication.class);
        UserDetails userDetails = mock(UserDetails.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(user.getEmail());

        UserRequest userRequest = new UserRequest();
        userRequest.setPassword("short");

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> userService.updateProfile(authentication, userRequest));

        assertEquals("Password must be at least 8 characters", exception.getMessage());
    }
    @Test
    public void testUpdateProfile_passwordMismatch_throwsException() {
        Authentication authentication = mock(Authentication.class);
        UserDetails userDetails = mock(UserDetails.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(user.getEmail());

        UserRequest userRequest = new UserRequest();
        userRequest.setPassword("newpassword123");
        userRequest.setConfirmPassword("wrongpassword");

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> userService.updateProfile(authentication, userRequest));

        assertEquals("Password and confirm password do not match", exception.getMessage());
    }

    @Test
    public void testUpdateProfile_fileUploadFailure_throwsException() throws IOException {
        Authentication authentication = mock(Authentication.class);
        UserDetails userDetails = mock(UserDetails.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(user.getEmail());

        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("profile.jpg");
        when(file.getContentType()).thenReturn("image/jpeg"); // Mock file type to be valid
        when(file.getInputStream()).thenThrow(new IOException("File upload error"));
        UserRequest userRequest = new UserRequest();
        userRequest.setProfilePicture(file);

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.updateProfile(authentication, userRequest));
        System.out.println(exception);
        assertEquals("File upload error", exception.getCause().getMessage());
    }

    @Test
    public void testGetImageById_success() throws IOException {
        UUID userId = UUID.randomUUID();
        String profilePicturePath = "profile.jpg"; // Simulated stored path
        String imageDirectory = "src/main/resources/static/images";
        Path fullPath = Path.of(imageDirectory, profilePicturePath);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        byte[] fakeImageBytes = new byte[]{1, 2, 3, 4, 5}; // Fake image data

        try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
            mockedFiles.when(() -> Files.exists(any(Path.class))).thenReturn(true);
            mockedFiles.when(() -> Files.readAllBytes(fullPath)).thenReturn(fakeImageBytes);

            byte[] result = userService.getImageById(userId);

            assertNotNull(result);
            assertArrayEquals(fakeImageBytes, result);

            verify(userRepository, times(1)).findById(userId);
        }
    }

    @Test
    public void testGetImageById_userNotFound_throwsException() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        DataNotFoundException exception = assertThrows(DataNotFoundException.class,
                () -> userService.getImageById(userId));

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    public void testGetImageById_profilePictureNotSet_throwsException() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        MockedStatic<Files> mockedFiles = mockStatic(Files.class);
        mockedFiles.when(() -> Files.exists(any(Path.class))).thenReturn(false);

        DataNotFoundException exception = assertThrows(DataNotFoundException.class,
                () -> userService.getImageById(userId));

        assertEquals("Profile picture not found for user: " + userId, exception.getMessage());
    }

    @Test
    public void testGetImageById_fileNotFound_throwsException() {
        UUID userId = UUID.randomUUID();
        String profilePicturePath = "2024/03/12/missing.jpg";
        String imageDirectory="src/main/resources/static/images";
        Path fullPath = Path.of(imageDirectory, profilePicturePath);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        user.setProfilePicture(profilePicturePath);


        DataNotFoundException exception = assertThrows(DataNotFoundException.class,
                () -> userService.getImageById(userId));

        assertThat(exception).hasMessageStartingWith("Profile picture not found for user:");

    }

}
