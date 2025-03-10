package com.beta.schoolpayment.controller;

import com.beta.schoolpayment.dto.request.AuthRequest;
import com.beta.schoolpayment.dto.request.UserRequest;
import com.beta.schoolpayment.dto.response.ApiResponse;
import com.beta.schoolpayment.dto.response.ErrorResponse;
import com.beta.schoolpayment.dto.response.PaginatedResponse;
import com.beta.schoolpayment.dto.response.UserResponse;
import com.beta.schoolpayment.exception.DataNotFoundException;
import com.beta.schoolpayment.exception.ValidationException;
import com.beta.schoolpayment.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/users")
public class UserController {
    @Autowired
    private UserService userService;

    //register
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRequest userRequest) {
        try{
            UserResponse userResponse = userService.register(userRequest);
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), userResponse));
        }catch(IllegalArgumentException e){
            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                    "Input Error",
                    e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (ValidationException e){
            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                    "Validation Error",
                    e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Internal Server Error",
                    "An unexpected error occurred.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    //login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest){
        try {
            String token = userService.login(authRequest).getToken();
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), token));
        } catch (RuntimeException e) {
            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                    "Token invalid",
                    e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Internal Server Error",
                    "An unexpected error occurred.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    @PutMapping(consumes="multipart/form-data")
    public ResponseEntity<?> updateUser(@Valid @ModelAttribute @RequestBody UserRequest userRequest, Authentication authentication) {
        try {
            UserResponse userResponse = userService.updateProfile(authentication, userRequest);
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), userResponse));
        } catch(IllegalArgumentException e){
            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                    "Input Error",
                    e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }catch (ValidationException e) {
            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                    "Validation Error",
                    e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (DataNotFoundException e) {
            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(),
                    "Data Not Found",
                    e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch(RuntimeException e){
            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                    "Failed to save image",
                    e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Internal Server Error",
                    "An unexpected error occurred.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    @GetMapping("/profile/picture/{id}")
    public ResponseEntity<?> getProfilePicture(@PathVariable UUID id) {
        try {
            byte[] image = userService.getImageById(id);
            return ResponseEntity.ok(image);
        } catch (RuntimeException e) {
            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                    "Failed to get image",
                    e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }
    @GetMapping
    public ResponseEntity<?> findAll(@RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "10") int size) {
        try {
            Page<UserResponse> response = userService.getAllUser(page, size);
            return ResponseEntity.ok(new PaginatedResponse<>(200, response));
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Internal Server Error",
                    "An unexpected error occurred.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    @GetMapping("/filter")
    public ResponseEntity<?> findUserByRole(@RequestParam String role,
                                            @RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<UserResponse> response = userService.getUserFilter(page, size, role);
            return ResponseEntity.ok(new PaginatedResponse<>(200, response));
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Internal Server Error",
                    "An unexpected error occurred.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    @PutMapping("/role/{id}")
    public ResponseEntity<?> updateRole (@PathVariable UUID id, @RequestBody UserRequest userRequest) {
        try {
            return ResponseEntity.ok(new ApiResponse<>(200, userService.updateRole(id, userRequest)));
        } catch (IllegalArgumentException e) {
            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                    "Input Error",
                    e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (DataNotFoundException e) {
            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(),
                    "Data Not Found",
                    e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Internal Server Error",
                    "An unexpected error occurred.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    @PutMapping("/delete/{id}")
    public ResponseEntity<?> softDelete(@PathVariable UUID id) {
        try {
            return ResponseEntity.ok(new ApiResponse<>(200, userService.softDelete(id)));
        } catch (DataNotFoundException e) {
            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(),
                    "Data Not Found",
                    e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Internal Server Error",
                    "An unexpected error occurred.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> hardDelete(@PathVariable UUID id) {
        try {
            userService.hardDelete(id);
            return ResponseEntity.ok(new ApiResponse<>(200, "User deleted successfully."));
        } catch (DataNotFoundException e) {
            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(),
                    "Data Not Found",
                    e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Internal Server Error",
                    "An unexpected error occurred.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
