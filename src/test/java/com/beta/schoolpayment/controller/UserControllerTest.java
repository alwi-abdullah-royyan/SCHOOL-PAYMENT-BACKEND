package com.beta.schoolpayment.controller;

import com.beta.schoolpayment.dto.request.AuthRequest;
import com.beta.schoolpayment.dto.request.UserRequest;
import com.beta.schoolpayment.dto.response.*;
import com.beta.schoolpayment.exception.DataNotFoundException;
import com.beta.schoolpayment.exception.ValidationException;
import com.beta.schoolpayment.service.UserService;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class UserControllerTest {
    @InjectMocks
    private UserController userController;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private Authentication authentication;

    @Mock
    private UserService userService;
    private UserRequest userRequest;
    private UserResponse userResponse;
    private AuthRequest authRequest;
    private AuthResponse authResponse;
    private byte[] mockImage;

    private Page<UserResponse> userPage;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(userService, "authenticationManager", authenticationManager);
        userRequest = new UserRequest();
        userRequest.setName("johndoe");
        userRequest.setEmail("johndoe@example.com");
        userRequest.setPassword("password123");
        userRequest.setRole("ADMIN");

        userResponse = new UserResponse();
        userResponse.setUserId(UUID.randomUUID());
        userResponse.setName("johndoe");
        userResponse.setEmail("johndoe@example.com");

        authRequest = new AuthRequest();
        authRequest.setIdentifier("johndoe");
        authRequest.setPassword("password123");

        authResponse = new AuthResponse();
        authResponse.setToken("valid-jwt-token");
        mockImage = new byte[]{1, 2, 3, 4, 5};

        userPage = new PageImpl<>(List.of(userResponse), PageRequest.of(0, 10), 1);

    }

    @Test
    void testRegisterUser_Success() {
        when(userService.register(userRequest)).thenReturn(userResponse);

        ResponseEntity<?> response = userController.registerUser(userRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isInstanceOf(ApiResponse.class);

        ApiResponse<?> apiResponse = (ApiResponse<?>) response.getBody();
        assertThat(apiResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(apiResponse.getData()).isEqualTo(userResponse);
    }

    @Test
    void testRegisterUser_InputError() {
        when(userService.register(userRequest)).thenThrow(new IllegalArgumentException("Invalid input"));

        ResponseEntity<?> response = userController.registerUser(userRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isInstanceOf(ErrorResponse.class);

        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertThat(errorResponse.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(errorResponse.getError()).isEqualTo("Input Error");
        assertThat(errorResponse.getMessage()).isEqualTo("Invalid input");
    }

    @Test
    void testRegisterUser_ValidationError() {
        when(userService.register(userRequest)).thenThrow(new ValidationException("Validation failed"));

        ResponseEntity<?> response = userController.registerUser(userRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isInstanceOf(ErrorResponse.class);

        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertThat(errorResponse.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(errorResponse.getError()).isEqualTo("Validation Error");
        assertThat(errorResponse.getMessage()).isEqualTo("Validation failed");
    }

    @Test
    void testRegisterUser_InternalServerError() {
        when(userService.register(userRequest)).thenThrow(new RuntimeException("Unexpected error"));

        ResponseEntity<?> response = userController.registerUser(userRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isInstanceOf(ErrorResponse.class);

        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertThat(errorResponse.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(errorResponse.getError()).isEqualTo("Internal Server Error");
        assertThat(errorResponse.getMessage()).isEqualTo("An unexpected error occurred.");
    }

    @Test
    void testLogin_Success() {
        when(userService.login(authRequest)).thenReturn(authResponse);

        ResponseEntity<?> response = userController.login(authRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isInstanceOf(ApiResponse.class);

        ApiResponse<?> apiResponse = (ApiResponse<?>) response.getBody();
        assertThat(apiResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(apiResponse.getData()).isEqualTo("valid-jwt-token");
    }

    @Test
    void testLogin_InvalidToken() {
        when(userService.login(authRequest)).thenThrow(new RuntimeException("Invalid credentials"));

        ResponseEntity<?> response = userController.login(authRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isInstanceOf(ErrorResponse.class);

        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertThat(errorResponse.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(errorResponse.getError()).isEqualTo("Token invalid");
        assertThat(errorResponse.getMessage()).isEqualTo("Invalid credentials");
    }

    @Test
    void testUpdateUser_Success() {
        when(userService.updateProfile(authentication, userRequest)).thenReturn(userResponse);

        ResponseEntity<?> response = userController.updateUser(userRequest, authentication);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isInstanceOf(ApiResponse.class);

        ApiResponse<?> apiResponse = (ApiResponse<?>) response.getBody();
        assertThat(apiResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(apiResponse.getData()).isEqualTo(userResponse);
    }

    @Test
    void testUpdateUser_InputError() {
        when(userService.updateProfile(authentication, userRequest))
                .thenThrow(new IllegalArgumentException("Invalid input"));

        ResponseEntity<?> response = userController.updateUser(userRequest, authentication);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isInstanceOf(ErrorResponse.class);

        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertThat(errorResponse.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(errorResponse.getError()).isEqualTo("Input Error");
        assertThat(errorResponse.getMessage()).isEqualTo("Invalid input");
    }

    @Test
    void testUpdateUser_ValidationError() {
        when(userService.updateProfile(authentication, userRequest))
                .thenThrow(new ValidationException("Validation failed"));

        ResponseEntity<?> response = userController.updateUser(userRequest, authentication);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isInstanceOf(ErrorResponse.class);

        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertThat(errorResponse.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(errorResponse.getError()).isEqualTo("Validation Error");
        assertThat(errorResponse.getMessage()).isEqualTo("Validation failed");
    }

    @Test
    void testUpdateUser_DataNotFound() {
        when(userService.updateProfile(authentication, userRequest))
                .thenThrow(new DataNotFoundException("User not found"));

        ResponseEntity<?> response = userController.updateUser(userRequest, authentication);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isInstanceOf(ErrorResponse.class);

        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertThat(errorResponse.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(errorResponse.getError()).isEqualTo("Data Not Found");
        assertThat(errorResponse.getMessage()).isEqualTo("User not found");
    }

    @Test
    void testUpdateUser_FailedToSaveImage() {
        when(userService.updateProfile(authentication, userRequest))
                .thenThrow(new RuntimeException("Failed to process image"));

        ResponseEntity<?> response = userController.updateUser(userRequest, authentication);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isInstanceOf(ErrorResponse.class);

        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertThat(errorResponse.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(errorResponse.getError()).isEqualTo("Failed to save image");
        assertThat(errorResponse.getMessage()).isEqualTo("Failed to process image");
    }

    @Test
    void testGetProfilePicture_Success() {
        when(userService.getImageById(userRequest.getUserId())).thenReturn(mockImage);

        ResponseEntity<?> response = userController.getProfilePicture(userRequest.getUserId());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(mockImage);
    }

    @Test
    void testGetProfilePicture_Failed() {
        when(userService.getImageById(userRequest.getUserId())).thenThrow(new RuntimeException("Image not found"));

        ResponseEntity<?> response = userController.getProfilePicture(userRequest.getUserId());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isInstanceOf(ErrorResponse.class);

        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertThat(errorResponse.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(errorResponse.getError()).isEqualTo("Failed to get image");
        assertThat(errorResponse.getMessage()).isEqualTo("Image not found");
    }

    @Test
    void testFindAll_Success() {
        when(userService.getAllUser(0, 10)).thenReturn(userPage);

        ResponseEntity<?> response = userController.findAll(0, 10);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isInstanceOf(PaginatedResponse.class);

        PaginatedResponse<?> paginatedResponse = (PaginatedResponse<?>) response.getBody();
        assertThat(paginatedResponse.getStatus()).isEqualTo(200);
        AssertionsForClassTypes.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void testFindAll_InternalServerError() {
        when(userService.getAllUser(0, 10)).thenThrow(new RuntimeException("Unexpected error"));

        ResponseEntity<?> response = userController.findAll(0, 10);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isInstanceOf(ErrorResponse.class);

        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertThat(errorResponse.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(errorResponse.getError()).isEqualTo("Internal Server Error");
        assertThat(errorResponse.getMessage()).isEqualTo("An unexpected error occurred.");
    }

    @Test
    void testFindUserByRole_Success() {
        when(userService.getUserFilter(0, 10, userRequest.getRole())).thenReturn(userPage);

        ResponseEntity<?> response = userController.findUserByRole(userRequest.getRole(), 0, 10);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isInstanceOf(PaginatedResponse.class);

        PaginatedResponse<?> paginatedResponse = (PaginatedResponse<?>) response.getBody();
        assertThat(paginatedResponse.getStatus()).isEqualTo(200);
        AssertionsForClassTypes.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void testFindUserByRole_InternalServerError() {
        when(userService.getUserFilter(0, 10, userRequest.getRole())).thenThrow(new RuntimeException("Unexpected error"));

        ResponseEntity<?> response = userController.findUserByRole(userRequest.getRole(), 0, 10);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isInstanceOf(ErrorResponse.class);

        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertThat(errorResponse.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(errorResponse.getError()).isEqualTo("Internal Server Error");
        assertThat(errorResponse.getMessage()).isEqualTo("An unexpected error occurred.");
    }

    @Test
    void testUpdateRole_Success() {
        when(userService.updateRole(userRequest.getUserId(), userRequest)).thenReturn(userResponse);

        ResponseEntity<?> response = userController.updateRole(userRequest.getUserId(), userRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isInstanceOf(ApiResponse.class);

        ApiResponse<?> apiResponse = (ApiResponse<?>) response.getBody();
        assertThat(apiResponse.getStatus()).isEqualTo(200);
        assertThat(apiResponse.getData()).isEqualTo(userResponse);
    }

    @Test
    void testUpdateRole_InputError() {
        when(userService.updateRole(userRequest.getUserId(), userRequest)).thenThrow(new IllegalArgumentException("Invalid role"));

        ResponseEntity<?> response = userController.updateRole(userRequest.getUserId(), userRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isInstanceOf(ErrorResponse.class);

        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertThat(errorResponse.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(errorResponse.getError()).isEqualTo("Input Error");
        assertThat(errorResponse.getMessage()).isEqualTo("Invalid role");
    }

    @Test
    void testUpdateRole_UserNotFound() {
        when(userService.updateRole(userRequest.getUserId(), userRequest)).thenThrow(new DataNotFoundException("User not found"));

        ResponseEntity<?> response = userController.updateRole(userRequest.getUserId(), userRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isInstanceOf(ErrorResponse.class);

        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertThat(errorResponse.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(errorResponse.getError()).isEqualTo("Data Not Found");
        assertThat(errorResponse.getMessage()).isEqualTo("User not found");
    }

    @Test
    void testUpdateRole_InternalServerError() {
        when(userService.updateRole(userRequest.getUserId(), userRequest)).thenThrow(new RuntimeException("Unexpected error"));

        ResponseEntity<?> response = userController.updateRole(userRequest.getUserId(), userRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isInstanceOf(ErrorResponse.class);

        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertThat(errorResponse.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(errorResponse.getError()).isEqualTo("Internal Server Error");
        assertThat(errorResponse.getMessage()).isEqualTo("An unexpected error occurred.");
    }

    @Test
    void testSoftDelete_Success() {
        when(userService.softDelete(userRequest.getUserId())).thenReturn(userResponse);

        ResponseEntity<?> response = userController.softDelete(userRequest.getUserId());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isInstanceOf(ApiResponse.class);

        ApiResponse<?> apiResponse = (ApiResponse<?>) response.getBody();
        assertThat(apiResponse.getStatus()).isEqualTo(200);
    }

    @Test
    void testSoftDelete_UserNotFound() {
        when(userService.softDelete(userRequest.getUserId())).thenThrow(new DataNotFoundException("User not found"));

        ResponseEntity<?> response = userController.softDelete(userRequest.getUserId());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isInstanceOf(ErrorResponse.class);

        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertThat(errorResponse.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(errorResponse.getError()).isEqualTo("Data Not Found");
        assertThat(errorResponse.getMessage()).isEqualTo("User not found");
    }

    @Test
    void testSoftDelete_InternalServerError() {
        when(userService.softDelete(userRequest.getUserId())).thenThrow(new RuntimeException("Unexpected error"));

        ResponseEntity<?> response = userController.softDelete(userRequest.getUserId());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isInstanceOf(ErrorResponse.class);

        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertThat(errorResponse.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(errorResponse.getError()).isEqualTo("Internal Server Error");
        assertThat(errorResponse.getMessage()).isEqualTo("An unexpected error occurred.");
    }

    @Test
    void testHardDelete_Success() {
        doNothing().when(userService).hardDelete(userRequest.getUserId());

        ResponseEntity<?> response = userController.hardDelete(userRequest.getUserId());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isInstanceOf(ApiResponse.class);

        ApiResponse<?> apiResponse = (ApiResponse<?>) response.getBody();
        assertThat(apiResponse.getStatus()).isEqualTo(200);
        assertThat(apiResponse.getData()).isEqualTo("User deleted successfully.");
    }

    @Test
    void testHardDelete_UserNotFound() {
        doThrow(new DataNotFoundException("User not found")).when(userService).hardDelete(userRequest.getUserId());

        ResponseEntity<?> response = userController.hardDelete(userRequest.getUserId());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isInstanceOf(ErrorResponse.class);

        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertThat(errorResponse.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(errorResponse.getError()).isEqualTo("Data Not Found");
        assertThat(errorResponse.getMessage()).isEqualTo("User not found");
    }

    @Test
    void testHardDelete_InternalServerError() {
        doThrow(new RuntimeException("Unexpected error")).when(userService).hardDelete(userRequest.getUserId());

        ResponseEntity<?> response = userController.hardDelete(userRequest.getUserId());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isInstanceOf(ErrorResponse.class);

        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertThat(errorResponse.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(errorResponse.getError()).isEqualTo("Internal Server Error");
        assertThat(errorResponse.getMessage()).isEqualTo("An unexpected error occurred.");
    }
}
