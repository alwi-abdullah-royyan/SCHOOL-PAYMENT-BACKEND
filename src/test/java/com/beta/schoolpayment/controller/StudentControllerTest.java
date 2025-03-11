package com.beta.schoolpayment.controller;
import com.beta.schoolpayment.dto.request.StudentRequest;
import com.beta.schoolpayment.dto.response.ApiResponse;
import com.beta.schoolpayment.dto.response.ErrorResponse;
import com.beta.schoolpayment.dto.response.StudentResponse;
import com.beta.schoolpayment.exception.DataNotFoundException;
import com.beta.schoolpayment.exception.ValidationException;
import com.beta.schoolpayment.service.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

class StudentControllerTest {
    @InjectMocks
    private StudentController studentController;

    @Mock
    private StudentService studentService;
    private StudentRequest studentRequest;
    private StudentResponse studentResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        studentResponse = new StudentResponse();
        studentResponse.setId(1L);
        studentResponse.setName("John Doe");

        studentRequest = new StudentRequest();
        studentRequest.setNis(1234L);
        studentRequest.setName("John Doe");
    }

    @Test
    void testGetAllStudents_Success() throws Exception {
        when(studentService.getAllStudents(0, 10)).thenReturn(new PageImpl<>(List.of(studentResponse), PageRequest.of(0, 10), 1));
        ResponseEntity<?> response = studentController.getAllStudent(0, 10);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    }

    @Test
    void testGetAllStudents_InternalServerError() {
        when(studentService.getAllStudents(0, 10)).thenThrow(new RuntimeException("Unexpected error"));

        ResponseEntity<?> response = studentController.getAllStudent(0, 10);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @Test
    void testCreateStudent_Success() {
        when(studentService.createStudent(studentRequest)).thenReturn(studentResponse);
        ResponseEntity<?> response = studentController.createStudent(studentRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(((ApiResponse<?>) response.getBody())).isEqualTo(new ApiResponse<>(200, studentResponse));

        verify(studentService, times(1)).createStudent(studentRequest);
    }

    @Test
    void testCreateStudent_ValidationError() {
        // Arrange
        when(studentService.createStudent(studentRequest)).thenThrow(new ValidationException("Invalid data"));

        // Act
        ResponseEntity<?> response = studentController.createStudent(studentRequest);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isInstanceOf(ErrorResponse.class);
        assertThat(((ErrorResponse) response.getBody()).getMessage()).isEqualTo("Invalid data");
    }

    @Test
    void testCreateStudent_DataNotFoundError() {
        // Arrange
        when(studentService.createStudent(studentRequest)).thenThrow(new DataNotFoundException("Class not found"));

        // Act
        ResponseEntity<?> response = studentController.createStudent(studentRequest);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isInstanceOf(ErrorResponse.class);
        assertThat(((ErrorResponse) response.getBody()).getMessage()).isEqualTo("Class not found");
    }

    @Test
    void testCreateStudent_InternalServerError() {
        // Arrange
        when(studentService.createStudent(studentRequest)).thenThrow(new RuntimeException("Unexpected error"));

        // Act
        ResponseEntity<?> response = studentController.createStudent(studentRequest);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isInstanceOf(ErrorResponse.class);
        assertThat(((ErrorResponse) response.getBody()).getMessage()).isEqualTo("An unexpected error occurred.");
    }
    @Test
    void testUpdateStudent_Success() {
        when(studentService.updateStudent(1L, studentRequest)).thenReturn(studentResponse);
        ResponseEntity<?> response = studentController.updateStudent(1L, studentRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(((ApiResponse<?>) response.getBody()))
                .isEqualTo(new ApiResponse<>(200, studentResponse));

        verify(studentService, times(1)).updateStudent(1L, studentRequest);
    }

    @Test
    void testUpdateStudent_ValidationError() {
        when(studentService.updateStudent(1L, studentRequest)).thenThrow(new ValidationException("Invalid data"));

        ResponseEntity<?> response = studentController.updateStudent(1L, studentRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(((ErrorResponse) response.getBody()).getMessage()).isEqualTo("Invalid data");
    }

    @Test
    void testUpdateStudent_DataNotFoundError() {
        when(studentService.updateStudent(1L, studentRequest)).thenThrow(new DataNotFoundException("Student not found"));

        ResponseEntity<?> response = studentController.updateStudent(1L, studentRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(((ErrorResponse) response.getBody()).getMessage()).isEqualTo("Student not found");
    }

    @Test
    void testUpdateStudent_InternalServerError() {
        when(studentService.updateStudent(1L, studentRequest)).thenThrow(new RuntimeException("Unexpected error"));

        ResponseEntity<?> response = studentController.updateStudent(1L, studentRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(((ErrorResponse) response.getBody()).getMessage()).isEqualTo("An unexpected error occurred.");
    }
    @Test
    void testSoftDelete_Success() {
        doNothing().when(studentService).deleteStudent(1L);

        ResponseEntity<?> response = studentController.softDelete(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(((ApiResponse<?>) response.getBody()))
                .isEqualTo(new ApiResponse<>(200, "Student deleted successfully."));

        verify(studentService, times(1)).deleteStudent(1L);
    }

    @Test
    void testSoftDelete_DataNotFound() {
        doThrow(new DataNotFoundException("Student not found")).when(studentService).deleteStudent(1L);

        ResponseEntity<?> response = studentController.softDelete(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(((ErrorResponse) response.getBody()).getMessage()).isEqualTo("Student not found");
    }

    @Test
    void testSoftDelete_InternalServerError() {
        doThrow(new RuntimeException("Unexpected error")).when(studentService).deleteStudent(1L);

        ResponseEntity<?> response = studentController.softDelete(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(((ErrorResponse) response.getBody()).getMessage()).isEqualTo("An unexpected error occurred.");
    }

    @Test
    void testHardDelete_Success() {
        doNothing().when(studentService).hardDeleteStudent(1L);

        ResponseEntity<?> response = studentController.hardDelete(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(((ApiResponse<?>) response.getBody()))
                .isEqualTo(new ApiResponse<>(200, "Student deleted successfully."));

        verify(studentService, times(1)).hardDeleteStudent(1L);
    }

    @Test
    void testHardDelete_DataNotFound() {
        doThrow(new DataNotFoundException("Student not found")).when(studentService).hardDeleteStudent(1L);

        ResponseEntity<?> response = studentController.hardDelete(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(((ErrorResponse) response.getBody()).getMessage()).isEqualTo("Student not found");
    }

    @Test
    void testHardDelete_InternalServerError() {
        doThrow(new RuntimeException("Unexpected error")).when(studentService).hardDeleteStudent(1L);

        ResponseEntity<?> response = studentController.hardDelete(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(((ErrorResponse) response.getBody()).getMessage()).isEqualTo("An unexpected error occurred.");
    }

}
